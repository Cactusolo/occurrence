package org.gbif.occurrence.persistence.hbase;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.GbifInternalTerm;

import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ColumnsTest {


  @Test
  public void testGetColumn() throws Exception {
    assertEquals("scientificName", Columns.column(DwcTerm.scientificName));
    assertEquals("countryCode", Columns.column(DwcTerm.countryCode));
    assertEquals("v_catalogNumber", Columns.column(DwcTerm.catalogNumber));
    assertEquals("class", Columns.column(DwcTerm.class_));
    assertEquals("order", Columns.column(DwcTerm.order));
    assertEquals("kingdomKey", Columns.column(GbifTerm.kingdomKey));
    //TODO: is this correct ???
    assertEquals("taxonKey", Columns.column(GbifTerm.taxonKey));
    assertEquals("v_occurrenceID", Columns.column(DwcTerm.occurrenceID));
    assertEquals("v_taxonID", Columns.column(DwcTerm.taxonID));
    assertEquals("basisOfRecord", Columns.column(DwcTerm.basisOfRecord));
    assertEquals("taxonKey", Columns.column(GbifTerm.taxonKey));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetVerbatimColumnIllegal() {
    Columns.verbatimColumn(GbifInternalTerm.crawlId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetColumnIllegal3() {
    Columns.column(DwcTerm.country);
  }

  public void testGetVerbatimColumn() throws Exception {
    assertEquals("v_basisOfRecord", Columns.verbatimColumn(DwcTerm.basisOfRecord));
  }

  public void testGetTermFromVerbatimColumn() throws Exception {
    assertEquals(DwcTerm.basisOfRecord, Columns.termFromVerbatimColumn(Bytes.toBytes("v_basisOfRecord")));
  }

}
