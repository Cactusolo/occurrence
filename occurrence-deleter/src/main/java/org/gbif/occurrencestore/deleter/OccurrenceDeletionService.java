package org.gbif.occurrencestore.deleter;

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.occurrencestore.common.model.HolyTriplet;
import org.gbif.occurrencestore.common.model.UniqueIdentifier;
import org.gbif.occurrencestore.persistence.api.OccurrenceKeyPersistenceService;
import org.gbif.occurrencestore.persistence.api.OccurrencePersistenceService;
import org.gbif.occurrencestore.persistence.api.VerbatimOccurrence;
import org.gbif.occurrencestore.persistence.api.VerbatimOccurrencePersistenceService;

import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple service that can handle the deletion of a single occurrence (including its secondary index entry).
 */
public class OccurrenceDeletionService {

  private static final Logger LOG = LoggerFactory.getLogger(OccurrenceDeletionService.class);

  private final OccurrencePersistenceService occurrenceService;
  private final OccurrenceKeyPersistenceService occurrenceKeyService;
  private final VerbatimOccurrencePersistenceService verbatimService;

  private final Meter occurrencesDeleted =
    Metrics.newMeter(OccurrenceDeletionService.class, "deletes", "deletes", TimeUnit.SECONDS);

  public OccurrenceDeletionService(OccurrencePersistenceService occurrenceService,
    OccurrenceKeyPersistenceService occurrenceKeyService,
    VerbatimOccurrencePersistenceService verbatimService) {
    this.occurrenceService = checkNotNull(occurrenceService, "occurrenceService can't be null");
    this.occurrenceKeyService = checkNotNull(occurrenceKeyService, "occurrenceKeyService can't be null");
    this.verbatimService = checkNotNull(verbatimService, "verbatimService can't be null");
  }

  public Occurrence deleteOccurrence(int occurrenceKey) {
    checkArgument(occurrenceKey > 0, "occurrenceKey must be > 0");
    LOG.debug("Deleting occurrence for key [{}]", occurrenceKey);

    // TODO: include dwcOccurrenceId lookup deletion (requires occ id on verbatim object)
    VerbatimOccurrence verbatim = verbatimService.get(occurrenceKey);
    if (verbatim == null) {
      LOG.info("No occurrence for key [{}], ignoring deletion request", occurrenceKey);
      return null;
    }

    UniqueIdentifier triplet = null;
    try {
      if (verbatim.getDatasetKey() != null) {
        triplet = new HolyTriplet(verbatim.getDatasetKey(), verbatim.getInstitutionCode(), verbatim.getCollectionCode(),
          verbatim.getCatalogNumber(), verbatim.getUnitQualifier());
      }
    } catch (IllegalArgumentException e) {
      LOG.debug("No valid triplet for occurrenceKey [{}]", occurrenceKey, e);
    }
    if (triplet == null) {
      LOG.info("No valid triplet for occurrenceKey [{}] therefore can't delete triplet lookup", occurrenceKey);
    } else {
      occurrenceKeyService.deleteKeyByUniqueIdentifiers(Sets.newHashSet(triplet));
    }

    // return the deleted occurrence
    Occurrence deleted = occurrenceService.delete(occurrenceKey);
    if (deleted != null) {
      occurrencesDeleted.mark();
    }
    return deleted;
  }
}
