package org.gbif.occurrence.download.hive;

import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.Term;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * TODO: this documentation is wrong
 * This provides the definitions required to construct the hdfs tables for querying during download.
 * <p/>
 * This class provides table definitions for full and simple downloads.  Simple is a subset of the full download and
 * exists only to help improve performance by reducing the amount of data scanned.
 */
public class DownloadTableDefinitions {

  /**
   * Generates the definition for the occurrence tables when used in hive.
   *
   * @return a list of fields for use in the <code>CREATE TABLE simple AS SELECT _fields_ FROM occurrence_HDFS</code>
   */
  public static List<String> fullDownload() {
    // start with the complete table definition and trim it to those we are interested in
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (InitializableField field : OccurrenceHDFSTableDefinition.definition()) {
      // omit any that are explicitly excluded
      if (!DownloadTerms.EXCLUSIONS.contains(field.getTerm())) {
        builder.add(field.getHiveField());
      }
    }
    return builder.build();
  }


  /**
   * Generates the conceptual definition for the occurrence tables when used in hive.
   *
   * @return a list of fields for use in the <code>CREATE TABLE simple AS SELECT _fields_ FROM occurrence_HDFS</code>
   */
  public static List<String> simpleDownload() {
    // start with the complete table definition and trim it to those we are interested in
    // selecting only the verbatim and interpreted fields of interest
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (InitializableField field : OccurrenceHDFSTableDefinition.definition()) {

      // formatted verbosely to aid readability
      // omit any from global exclusion list
      if (!DownloadTerms.EXCLUSIONS.contains(field.getTerm())) {

        // allow any in the interpreted or verbatim table definitions
        if (DownloadTerms.SimpleDownload.INTERPRETED_FIELDS.contains(field.getTerm()) ||
            (field.getHiveField().startsWith(HiveColumns.VERBATIM_COL_PREFIX) &&
            DownloadTerms.SimpleDownload.VERBATIM_FIELDS.contains(field.getTerm()))) {
          builder.add(field.getHiveField());
        }
      }
    }

    return builder.build();
  }
}
