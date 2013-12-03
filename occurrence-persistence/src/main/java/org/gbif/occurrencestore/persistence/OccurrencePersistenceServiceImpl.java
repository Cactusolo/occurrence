package org.gbif.occurrencestore.persistence;


import org.gbif.api.exception.ServiceUnavailableException;
import org.gbif.api.model.common.Identifier;
import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.occurrencestore.common.model.constants.FieldName;
import org.gbif.occurrencestore.persistence.api.OccurrencePersistenceService;
import org.gbif.occurrencestore.persistence.constants.HBaseTableConstants;
import org.gbif.occurrencestore.persistence.hbase.HBaseFieldUtil;
import org.gbif.occurrencestore.persistence.hbase.HBaseHelper;
import org.gbif.occurrencestore.persistence.util.OccurrenceBuilder;
import org.gbif.occurrencestore.util.BasisOfRecordConverter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation of OccurrenceService and OccurrenceWriter for persisting and retrieving Occurrence objects in
 * HBase.
 */
@Singleton
public class OccurrencePersistenceServiceImpl implements OccurrencePersistenceService {

  private static final Logger LOG = LoggerFactory.getLogger(OccurrencePersistenceServiceImpl.class);
  private static final int SCANNER_BATCH_SIZE = 50;
  private static final int SCANNER_CACHE_SIZE = 50;

  private final String occurrenceTableName;
  private final HTablePool tablePool;
  private static final BasisOfRecordConverter BR_CONVERTER = new BasisOfRecordConverter();

  @Inject
  public OccurrencePersistenceServiceImpl(@Named("table_name") String tableName, HTablePool tablePool) {
    this.occurrenceTableName = checkNotNull(tableName, "tableName can't be null");
    this.tablePool = checkNotNull(tablePool, "tablePool can't be null");
  }

  @Override
  public Occurrence get(@Nullable Integer key) {
    if (key == null) {
      return null;
    }
    Occurrence occ = null;
    HTableInterface table = null;
    try {
      table = tablePool.getTable(occurrenceTableName);
      Get get = new Get(Bytes.toBytes(key));
      Result result = table.get(get);
      if (result == null || result.isEmpty()) {
        LOG.debug("Couldn't find occurrence for key [{}], returning null", key);
        return null;
      }
      occ = OccurrenceBuilder.buildOccurrence(result);
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      closeTable(table);
    }

    return occ;
  }

  /**
   * Note that the returned fragment here is a String that holds the actual xml or json snippet for this occurrence,
   * and not the Fragment object that is used elsewhere.
   *
   * @param key that identifies an occurrence
   *
   * @return a String holding the original xml or json snippet for this occurrence
   */
  @Override
  public String getFragment(int key) {
    String fragment = null;
    HTableInterface table = null;
    try {
      table = tablePool.getTable(occurrenceTableName);
      Get get = new Get(Bytes.toBytes(key));
      Result result = table.get(get);
      if (result == null || result.isEmpty()) {
        LOG.info("Couldn't find occurrence for id [{}], returning null", key);
        return null;
      }
      byte[] rawFragment = OccurrenceResultReader.getBytes(result, FieldName.FRAGMENT);
      if (rawFragment != null) {
        fragment = Bytes.toString(rawFragment);
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      closeTable(table);
    }

    return fragment;
  }

  @Override
  public Iterator<Integer> getKeysByColumn(byte[] columnValue, FieldName columnName) {
    byte[] cf = Bytes.toBytes(HBaseFieldUtil.getHBaseColumn(columnName).getColumnFamilyName());
    byte[] col = Bytes.toBytes(HBaseFieldUtil.getHBaseColumn(columnName).getColumnName());
    Scan scan = new Scan();
    scan.setBatch(SCANNER_BATCH_SIZE);
    scan.setCaching(SCANNER_CACHE_SIZE);
    scan.addColumn(cf, col);
    SingleColumnValueFilter filter =
      new SingleColumnValueFilter(cf, col, CompareFilter.CompareOp.EQUAL, columnValue);
    scan.setFilter(filter);

    return new OccurrenceKeyIterator(tablePool, occurrenceTableName, scan);
  }

  @Override
  public void update(Occurrence occ) {
    checkNotNull(occ, "occurrence can't be null");
    checkNotNull(occ.getKey(), "occurrence's key can't be null");

    // TODO bean validation of occ

    HTableInterface occTable = null;
    try {
      occTable = tablePool.getTable(occurrenceTableName);
      byte[] cf = Bytes.toBytes(HBaseTableConstants.OCCURRENCE_COLUMN_FAMILY);
      write(occTable, cf, occ, true);
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not access HBase", e);
    } finally {
      closeTable(occTable);
    }
  }

  @Override
  public Occurrence delete(int occurrenceKey) {
    Occurrence occurrence = get(occurrenceKey);
    if (occurrence == null) {
      LOG.debug("Occurrence for key [{}] not found, ignoring delete request.", occurrenceKey);
    } else {
      delete(new ImmutableList.Builder<Integer>().add(occurrenceKey).build());
    }

    LOG.debug("<< delete [{}]", occurrenceKey);
    return occurrence;
  }

  @Override
  public void delete(List<Integer> occurrenceKeys) {
    checkNotNull(occurrenceKeys, "occurrenceKeys can't be null");

    HTableInterface occTable = null;
    try {
      occTable = tablePool.getTable(occurrenceTableName);
      List<Delete> deletes = Lists.newArrayList();
      for (Integer occurrenceKey : occurrenceKeys) {
        if (occurrenceKey != null) {
          deletes.add(new Delete(Bytes.toBytes(occurrenceKey)));
        }
      }
      LOG.debug("Deleting [{}] occurrences", occurrenceKeys.size());
      occTable.delete(deletes);
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not access HBase", e);
    } finally {
      closeTable(occTable);
    }
  }


  private static void write(HTableInterface occTable, byte[] cf, Occurrence occ, boolean dn) throws IOException {
    Put put = new Put(Bytes.toBytes(occ.getKey()));
    Delete del = null;
    if (dn) del = new Delete(Bytes.toBytes(occ.getKey()));

    HBaseHelper.writeField(FieldName.I_ALTITUDE,
      occ.getAltitude() == null ? null : Bytes.toBytes(occ.getAltitude()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_BASIS_OF_RECORD,
      occ.getBasisOfRecord() == null ? null : Bytes.toBytes(BR_CONVERTER.fromEnum(occ.getBasisOfRecord())),
      dn, cf, put, del);
    HBaseHelper.writeField(FieldName.CATALOG_NUMBER,
      occ.getCatalogNumber() == null ? null : Bytes.toBytes(occ.getCatalogNumber()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_CLASS_ID,
      occ.getClassKey() == null ? null : Bytes.toBytes(occ.getClassKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_CLASS,
      occ.getClazz() == null ? null : Bytes.toBytes(occ.getClazz()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.COLLECTION_CODE,
      occ.getCollectionCode() == null ? null : Bytes.toBytes(occ.getCollectionCode()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.DATA_PROVIDER_ID,
      occ.getDataProviderId() == null ? null : Bytes.toBytes(occ.getDataProviderId()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.DATA_RESOURCE_ID,
      occ.getDataResourceId() == null ? null : Bytes.toBytes(occ.getDataResourceId()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.DATASET_KEY,
      occ.getDatasetKey() == null ? null : Bytes.toBytes(occ.getDatasetKey().toString()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_DEPTH,
      occ.getDepth() == null ? null : Bytes.toBytes(occ.getDepth()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_FAMILY,
      occ.getFamily() == null ? null : Bytes.toBytes(occ.getFamily()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_FAMILY_ID,
      occ.getFamilyKey() == null ? null : Bytes.toBytes(occ.getFamilyKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_GENUS,
      occ.getGenus() == null ? null : Bytes.toBytes(occ.getGenus()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_GENUS_ID,
      occ.getGenusKey() == null ? null : Bytes.toBytes(occ.getGenusKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_GEOSPATIAL_ISSUE,
      occ.getGeospatialIssue() == null ? null : Bytes.toBytes(occ.getGeospatialIssue()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.HOST_COUNTRY,
      occ.getHostCountry() == null ? null : Bytes.toBytes(occ.getHostCountry().getIso2LetterCode()), dn, cf, put,
      del);
    HBaseHelper.writeField(FieldName.INSTITUTION_CODE,
      occ.getInstitutionCode() == null ? null : Bytes.toBytes(occ.getInstitutionCode()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_KINGDOM,
      occ.getKingdom() == null ? null : Bytes.toBytes(occ.getKingdom()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_KINGDOM_ID,
      occ.getKingdomKey() == null ? null : Bytes.toBytes(occ.getKingdomKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_LATITUDE,
      occ.getLatitude() == null ? null : Bytes.toBytes(occ.getLatitude()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_LONGITUDE,
      occ.getLongitude() == null ? null : Bytes.toBytes(occ.getLongitude()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.LOCALITY,
      occ.getLocality() == null ? null : Bytes.toBytes(occ.getLocality()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_ISO_COUNTRY_CODE,
      occ.getCountry() == null ? null : Bytes.toBytes(occ.getCountry().getIso2LetterCode()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.COUNTY, occ.getCounty() == null ? null : Bytes.toBytes(occ.getCounty()), dn, cf,
      put, del);
    HBaseHelper.writeField(FieldName.STATE_PROVINCE,
      occ.getStateProvince() == null ? null : Bytes.toBytes(occ.getStateProvince()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.CONTINENT_OCEAN,
      occ.getContinent() == null ? null : Bytes.toBytes(occ.getContinent()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.COLLECTOR_NAME,
      occ.getCollectorName() == null ? null : Bytes.toBytes(occ.getCollectorName()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.IDENTIFIER_NAME,
      occ.getIdentifierName() == null ? null : Bytes.toBytes(occ.getIdentifierName()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.IDENTIFICATION_DATE,
      occ.getIdentificationDate() == null ? null : Bytes.toBytes(occ.getIdentificationDate().getTime()), dn,
      cf, put, del);
    HBaseHelper.writeField(FieldName.I_MODIFIED,
      occ.getModified() == null ? null : Bytes.toBytes(occ.getModified().getTime()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_MONTH,
      occ.getOccurrenceMonth() == null ? null : Bytes.toBytes(occ.getOccurrenceMonth()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_NUB_ID, occ.getNubKey() == null ? null : Bytes.toBytes(occ.getNubKey()), dn,
      cf, put, del);
    HBaseHelper.writeField(FieldName.I_OCCURRENCE_DATE,
      occ.getOccurrenceDate() == null ? null : Bytes.toBytes(occ.getOccurrenceDate().getTime()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_ORDER, occ.getOrder() == null ? null : Bytes.toBytes(occ.getOrder()), dn, cf,
      put, del);
    HBaseHelper.writeField(FieldName.I_ORDER_ID,
      occ.getOrderKey() == null ? null : Bytes.toBytes(occ.getOrderKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_OTHER_ISSUE,
      occ.getOtherIssue() == null ? null : Bytes.toBytes(occ.getOtherIssue()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.OWNING_ORG_KEY,
      occ.getOwningOrgKey() == null ? null : Bytes.toBytes(occ.getOwningOrgKey().toString()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_PHYLUM, occ.getPhylum() == null ? null : Bytes.toBytes(occ.getPhylum()), dn,
      cf, put, del);
    HBaseHelper.writeField(FieldName.I_PHYLUM_ID,
      occ.getPhylumKey() == null ? null : Bytes.toBytes(occ.getPhylumKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.PROTOCOL,
      (occ.getProtocol() == null ? null : Bytes.toBytes(occ.getProtocol().toString())), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.RESOURCE_ACCESS_POINT_ID,
      occ.getResourceAccessPointId() == null ? null : Bytes.toBytes(occ.getResourceAccessPointId()), dn, cf,put, del);
    HBaseHelper.writeField(FieldName.I_SCIENTIFIC_NAME,
      occ.getScientificName() == null ? null : Bytes.toBytes(occ.getScientificName()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_SPECIES, occ.getSpecies() == null ? null : Bytes.toBytes(occ.getSpecies()), dn,
      cf, put, del);
    HBaseHelper.writeField(FieldName.I_SPECIES_ID,
      occ.getSpeciesKey() == null ? null : Bytes.toBytes(occ.getSpeciesKey()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_TAXONOMIC_ISSUE,
      occ.getTaxonomicIssue() == null ? null : Bytes.toBytes(occ.getTaxonomicIssue()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.UNIT_QUALIFIER,
      occ.getUnitQualifier() == null ? null : Bytes.toBytes(occ.getUnitQualifier()), dn, cf, put, del);
    HBaseHelper.writeField(FieldName.I_YEAR,
      occ.getOccurrenceYear() == null ? null : Bytes.toBytes(occ.getOccurrenceYear()), dn, cf, put, del);

    if (dn) deleteOldIdentifiers(occTable, occ.getKey(), cf);

    if (occ.getIdentifiers() != null && !occ.getIdentifiers().isEmpty()) {
      addIdentifiersToPut(put, cf, occ.getIdentifiers());
    }

    occTable.put(put);
    if (dn && !del.isEmpty()) occTable.delete(del);
    occTable.flushCommits();
  }

  private static void addIdentifiersToPut(Put put, byte[] columnFamily, Collection<Identifier> records) {
    int count = -1;
    put.add(columnFamily, Bytes.toBytes(HBaseFieldUtil.getHBaseColumn(FieldName.IDENTIFIER_COUNT).getColumnName()),
      Bytes.toBytes(records.size()));
    for (Identifier record : records) {
      count++;
      String idCol = HBaseTableConstants.IDENTIFIER_COLUMN + count;
      String idTypeCol = HBaseTableConstants.IDENTIFIER_TYPE_COLUMN + count;
      put.add(columnFamily, Bytes.toBytes(idCol), Bytes.toBytes(record.getIdentifier()));
      put.add(columnFamily, Bytes.toBytes(idTypeCol), Bytes.toBytes(record.getType().toString()));
    }
  }

  private static void deleteOldIdentifiers(HTableInterface occTable, int id, byte[] columnFamily) throws IOException {
    Get get = new Get(Bytes.toBytes(id));
    get.addColumn(columnFamily,
      Bytes.toBytes(HBaseFieldUtil.getHBaseColumn(FieldName.IDENTIFIER_COUNT).getColumnName()));
    Result result = occTable.get(get);
    Integer maxCount = OccurrenceResultReader.getInteger(result, FieldName.IDENTIFIER_COUNT);
    if (maxCount != null && maxCount > 0) {
      Delete delete = new Delete(Bytes.toBytes(id));
      for (int count = 0; count < maxCount; count++) {
        String idCol = HBaseTableConstants.IDENTIFIER_COLUMN + count;
        delete.deleteColumn(columnFamily, Bytes.toBytes(idCol));
        String idTypeCol = HBaseTableConstants.IDENTIFIER_TYPE_COLUMN + count;
        delete.deleteColumn(columnFamily, Bytes.toBytes(idTypeCol));
      }
      occTable.delete(delete);
      // set count to 0
      Put put = new Put(Bytes.toBytes(id));
      put.add(columnFamily, Bytes.toBytes(HBaseFieldUtil.getHBaseColumn(FieldName.IDENTIFIER_COUNT).getColumnName()),
        Bytes.toBytes(0));
      occTable.put(put);
    }
  }

  private static void closeTable(HTableInterface table) {
    if (table != null) {
      try {
        table.close();
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }
  }
}
