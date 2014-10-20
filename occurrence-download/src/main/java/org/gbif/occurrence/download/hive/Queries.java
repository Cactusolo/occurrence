package org.gbif.occurrence.download.hive;

import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.Term;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Utilities related to the actual queries executed at runtime.
 * The queries relate closely to the data definitions (obviously) and this class provides the bridge between the
 * definitions and the queries.
 */
class Queries {
  /**
   * @return the select fields for the verbatim table in the simple download
   */
  static List<InitializableField> selectSimpleVerbatimFields() {
    ImmutableList.Builder<InitializableField> builder = ImmutableList.builder();
    // always add the GBIF ID
    builder.add(new InitializableField(
      GbifTerm.gbifID,
      HiveColumns.columnFor(GbifTerm.gbifID),
      HiveDataTypes.typeForTerm(GbifTerm.gbifID, true)));

    for (Term term : DownloadTerms.SimpleDownload.VERBATIM_FIELDS) {
      if (GbifTerm.gbifID == term) {
        continue; // for safety, we code defensively as it may be added
      }

      // TODO: add UDFs
      builder.add(new InitializableField(
        term,
        HiveColumns.VERBATIM_COL_PREFIX + term.simpleName().toLowerCase(), // no escape needed due to prefix
        HiveDataTypes.TYPE_STRING));
    }
    return builder.build();
  }

  /**
   * @return the select fields for the interpreted table in the simple download
   */
  static List<InitializableField> selectSimpleInterpretedFields() {
    ImmutableList.Builder<InitializableField> builder = ImmutableList.builder();
    // always add the GBIF ID
    builder.add(new InitializableField(
      GbifTerm.gbifID,
      HiveColumns.columnFor(GbifTerm.gbifID),
      HiveDataTypes.typeForTerm(GbifTerm.gbifID, true)));

    for (Term term : DownloadTerms.SimpleDownload.INTERPRETED_FIELDS) {
      if (GbifTerm.gbifID == term) {
        continue; // for safety, we code defensively as it may be added
      }

      // TODO: add UDFs
      builder.add(new InitializableField(
        term,
        HiveColumns.columnFor(term),
        HiveDataTypes.TYPE_STRING));
    }
    return builder.build();
  }
}
