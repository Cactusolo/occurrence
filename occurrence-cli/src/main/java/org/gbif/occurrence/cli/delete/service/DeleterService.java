package org.gbif.occurrence.cli.delete.service;

import org.gbif.common.messaging.DefaultMessagePublisher;
import org.gbif.common.messaging.MessageListener;
import org.gbif.occurrencestore.deleter.OccurrenceDeletionService;
import org.gbif.occurrencestore.deleter.messaging.DeleteOccurrenceListener;
import org.gbif.occurrencestore.persistence.OccurrenceKeyPersistenceServiceImpl;
import org.gbif.occurrencestore.persistence.OccurrencePersistenceServiceImpl;
import org.gbif.occurrencestore.persistence.VerbatimOccurrencePersistenceServiceImpl;
import org.gbif.occurrencestore.persistence.api.OccurrenceKeyPersistenceService;
import org.gbif.occurrencestore.persistence.api.OccurrencePersistenceService;
import org.gbif.occurrencestore.persistence.api.VerbatimOccurrencePersistenceService;
import org.gbif.occurrencestore.persistence.keygen.HBaseLockingKeyService;
import org.gbif.occurrencestore.persistence.keygen.KeyPersistenceService;

import com.google.common.util.concurrent.AbstractIdleService;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTablePool;

public class DeleterService extends AbstractIdleService {

  private final DeleterConfiguration config;
  private MessageListener listener;
  private HTablePool tablePool;

  public DeleterService(DeleterConfiguration config) {
    this.config = config;
  }

  @Override
  protected void startUp() throws Exception {
    config.ganglia.start();

    tablePool = new HTablePool(HBaseConfiguration.create(), config.hbasePoolSize);

    KeyPersistenceService<Integer> keyService =
      new HBaseLockingKeyService(config.lookupTable, config.counterTable, config.occTable, tablePool);
    OccurrenceKeyPersistenceService occurrenceKeyService = new OccurrenceKeyPersistenceServiceImpl(keyService);
    OccurrencePersistenceService occurrenceService = new OccurrencePersistenceServiceImpl(config.occTable, tablePool);
    VerbatimOccurrencePersistenceService verbatimService =
      new VerbatimOccurrencePersistenceServiceImpl(config.occTable, tablePool);
    OccurrenceDeletionService occurrenceDeletionService =
      new OccurrenceDeletionService(occurrenceService, occurrenceKeyService, verbatimService);

    listener = new MessageListener(config.messaging.getConnectionParameters());
    listener.listen(config.queueName, 1, new DeleteOccurrenceListener(occurrenceDeletionService,
      new DefaultMessagePublisher(config.messaging.getConnectionParameters())));
  }

  @Override
  protected void shutDown() throws Exception {
    if (listener != null) {
      listener.close();
    }
    if (tablePool != null) {
      tablePool.close();
    }
  }
}
