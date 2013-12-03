package org.gbif.occurrencestore.processor;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.OccurrenceSchemaType;
import org.gbif.occurrencestore.processor.identifiers.IdentifierStrategy;

import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdentifierStrategyTest {

  @Test
  public void testStrategies() {
    // good triplets, no occ
    int checked = 100;
    int uniqueTriplets = 100;
    int invalidTriplets = 0;
    int uniqueOccIds = 0;
    int missingOccIds = 100;
    DwcaValidationReport report =
      new DwcaValidationReport(UUID.randomUUID(), checked, uniqueTriplets, invalidTriplets, uniqueOccIds,
        missingOccIds, true);
    IdentifierStrategy strategy = new IdentifierStrategy(OccurrenceSchemaType.DWCA, report);
    assertTrue(strategy.isTripletsValid());
    assertFalse(strategy.isOccurrenceIdsValid());

    // good triplets, good occ
    checked = 100;
    uniqueTriplets = 100;
    invalidTriplets = 0;
    uniqueOccIds = 100;
    missingOccIds = 0;
    report =
      new DwcaValidationReport(UUID.randomUUID(), checked, uniqueTriplets, invalidTriplets, uniqueOccIds,
        missingOccIds, true);
    strategy = new IdentifierStrategy(OccurrenceSchemaType.DWCA, report);
    assertTrue(strategy.isTripletsValid());
    assertTrue(strategy.isOccurrenceIdsValid());

    // dupe triplets, dupe occ
    checked = 100;
    uniqueTriplets = 80;
    invalidTriplets = 0;
    uniqueOccIds = 60;
    missingOccIds = 0;
    report =
      new DwcaValidationReport(UUID.randomUUID(), checked, uniqueTriplets, invalidTriplets, uniqueOccIds,
        missingOccIds, true);
    strategy = new IdentifierStrategy(OccurrenceSchemaType.DWCA, report);
    assertFalse(strategy.isTripletsValid());
    assertFalse(strategy.isOccurrenceIdsValid());

    // some invalid triplets but unique matches, some invalid occ, but unique matches
    checked = 100;
    uniqueTriplets = 80;
    invalidTriplets = 20;
    uniqueOccIds = 20;
    missingOccIds = 80;
    report =
      new DwcaValidationReport(UUID.randomUUID(), checked, uniqueTriplets, invalidTriplets, uniqueOccIds,
        missingOccIds, true);
    strategy = new IdentifierStrategy(OccurrenceSchemaType.DWCA, report);
    assertTrue(strategy.isTripletsValid());
    assertTrue(strategy.isOccurrenceIdsValid());

    // invalid plus dupe triplets, good occ
    checked = 100;
    uniqueTriplets = 50;
    invalidTriplets = 20;
    uniqueOccIds = 100;
    missingOccIds = 0;
    report =
      new DwcaValidationReport(UUID.randomUUID(), checked, uniqueTriplets, invalidTriplets, uniqueOccIds,
        missingOccIds, true);
    strategy = new IdentifierStrategy(OccurrenceSchemaType.DWCA, report);
    assertFalse(strategy.isTripletsValid());
    assertTrue(strategy.isOccurrenceIdsValid());

    // good triplets, invalid and dupe occ
    checked = 100;
    uniqueTriplets = 100;
    invalidTriplets = 0;
    uniqueOccIds = 80;
    missingOccIds = 5;
    report =
      new DwcaValidationReport(UUID.randomUUID(), checked, uniqueTriplets, invalidTriplets, uniqueOccIds,
        missingOccIds, true);
    strategy = new IdentifierStrategy(OccurrenceSchemaType.DWCA, report);
    assertTrue(strategy.isTripletsValid());
    assertFalse(strategy.isOccurrenceIdsValid());
  }
}
