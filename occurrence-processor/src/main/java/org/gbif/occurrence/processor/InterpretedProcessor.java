package org.gbif.occurrence.processor;

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.model.occurrence.VerbatimOccurrence;
import org.gbif.api.vocabulary.OccurrencePersistenceStatus;
import org.gbif.common.messaging.api.MessagePublisher;
import org.gbif.common.messaging.api.messages.OccurrenceMutatedMessage;
import org.gbif.occurrence.persistence.api.Fragment;
import org.gbif.occurrence.persistence.api.FragmentPersistenceService;
import org.gbif.occurrence.persistence.api.OccurrencePersistenceService;
import org.gbif.occurrence.processor.interpreting.OccurrenceInterpretationResult;
import org.gbif.occurrence.processor.interpreting.VerbatimOccurrenceInterpreter;
import org.gbif.occurrence.processor.zookeeper.ZookeeperConnector;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Takes VerbatimOccurrences and interprets their raw (String) fields into typed fields, producing Occurrence records.
 */
@Singleton
public class InterpretedProcessor {

  private final FragmentPersistenceService fragmentPersister;
  private final VerbatimOccurrenceInterpreter verbatimInterpreter;
  private final OccurrencePersistenceService occurrencePersister;
  private final MessagePublisher messagePublisher;
  private final ZookeeperConnector zookeeperConnector;

  private final Meter interpProcessed = Metrics.newMeter(
      InterpretedProcessor.class, "interps", "interps", TimeUnit.SECONDS);
  private final Timer interpTimer = Metrics.newTimer(
      InterpretedProcessor.class, "interp time", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
  private final Timer msgTimer = Metrics.newTimer(
      InterpretedProcessor.class, "msg send time", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

  private static final Logger LOG = LoggerFactory.getLogger(InterpretedProcessor.class);

  @Inject
  public InterpretedProcessor(FragmentPersistenceService fragmentPersister,
    VerbatimOccurrenceInterpreter verbatimInterpreter, OccurrencePersistenceService occurrencePersister,
    MessagePublisher messagePublisher, ZookeeperConnector zookeeperConnector) {
    this.fragmentPersister = checkNotNull(fragmentPersister, "fragmentPersister can't be null");
    this.verbatimInterpreter = checkNotNull(verbatimInterpreter, "verbatimInterpreter can't be null");
    this.occurrencePersister = checkNotNull(occurrencePersister, "occurrencePersister can't be null");
    this.messagePublisher = checkNotNull(messagePublisher, "messagePublisher can't be null");
    this.zookeeperConnector = checkNotNull(zookeeperConnector, "zookeeperConnector can't be null");
  }

  /**
   * Builds and persists an Occurrence record by interpreting the fields of the VerbatimOccurrence identified by the
   * passed in occurrenceKey. Note that UNCHANGED occurrences are ignored.
   *
   * @param occurrenceKey the key of the VerbatimOccurrence that will be fetched from HBase and interpreted
   * @param status        whether this is a NEW, UPDATED, or UNCHANGED occurrence
   * @param fromCrawl     true if this method is called as part of a crawl
   * @param attemptId     the crawl attempt id, only used for passing along in logs and subsequent messages.
   * @param datasetKey    the dataset that this occurrence belongs to (must not be null if fromCrawl is true)
   */
  public void buildInterpreted(int occurrenceKey, OccurrencePersistenceStatus status, boolean fromCrawl,
    @Nullable Integer attemptId, @Nullable UUID datasetKey) {
    checkArgument(occurrenceKey > 0, "occurrenceKey must be greater than 0");
    checkNotNull(status, "status can't be null");
    if (fromCrawl) {
      checkNotNull(datasetKey, "datasetKey can't be null if fromCrawl is true");
      checkArgument(attemptId != null && attemptId > 0, "attemptId must be greater than 0 if fromCrawl is true");
    }

    if (status == OccurrencePersistenceStatus.UNCHANGED) {
      LOG.debug("Ignoring verbatim of status UNCHANGED.");
      return;
    }

    int localAttemptId;
    if (fromCrawl) {
      localAttemptId = attemptId;
    } else {
      Fragment fragment = fragmentPersister.get(occurrenceKey);
      if (fragment == null) {
        LOG.warn(
          "Could not find fragment with key [{}] when looking up attemptId for non-crawl interpretation - skipping.",
          occurrenceKey);
        return;
      }
      localAttemptId = fragment.getCrawlId();
    }

    VerbatimOccurrence verbatim = occurrencePersister.getVerbatim(occurrenceKey);
    if (verbatim == null) {
      logError("Could not find", occurrenceKey, datasetKey, fromCrawl);
      return;
    }

    OccurrenceInterpretationResult interpretationResult;
    final TimerContext interpContext = interpTimer.time();
    try {
      interpretationResult = verbatimInterpreter.interpret(verbatim, status, fromCrawl);
    } finally {
      interpContext.stop();
    }

    if (interpretationResult.getUpdated() == null) {
      logError("Could not interpret", occurrenceKey, datasetKey, fromCrawl);
      return;
    }

    Occurrence interpreted = interpretationResult.getUpdated();

    LOG.debug("sending messages");
    OccurrenceMutatedMessage interpMsg;
    // can only be NEW or UPDATED
    if (status == OccurrencePersistenceStatus.NEW) {
      interpMsg = OccurrenceMutatedMessage.buildNewMessage(interpreted.getDatasetKey(), interpreted, localAttemptId);
    } else {
      interpMsg = OccurrenceMutatedMessage
        .buildUpdateMessage(interpreted.getDatasetKey(), interpretationResult.getOriginal(), interpreted,
          localAttemptId);
    }

    final TimerContext context = msgTimer.time();
    try {
      messagePublisher.send(interpMsg);
    } catch (IOException e) {
      LOG.warn("Could not send OccurrencePersistedMessage for successful [{}]", status.toString(), e);
    } finally {
      context.stop();
    }

    interpProcessed.mark();
  }

  private void logError(String message, int occurrenceKey, UUID datasetKey, boolean fromCrawl) {
    // TODO: send msg?
    LOG.warn("{} verbatim occurrence with key [{}] - skipping.", message, occurrenceKey);
    if (fromCrawl) {
      LOG.debug("Updating zookeeper for InterpretedOccurrencePersistedError");
      zookeeperConnector.addCounter(datasetKey, ZookeeperConnector.CounterName.INTERPRETED_OCCURRENCE_PERSISTED_ERROR);
    }
  }
}
