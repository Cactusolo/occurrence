package org.gbif.occurrence.download.hive;

import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.Term;

import java.util.Arrays;
import java.util.Set;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Definitions of terms used in downloading, and in create tables used during the download process.
 */
public class DownloadTerms {

  /**
   * Terms that are excluded from all downloads
   */
  public static final Set<Term> EXCLUSIONS = buildExclusions();

  /**
   * Defines the simple download table format
   */
  public static class SimpleDownload {
    /**
     * The terms that will be included in the verbatim table if also present in ${@link Terms#verbatimTerms()}
     */
    public static final Set<Term> VERBATIM_FIELDS = ImmutableSet.<Term>of(
      GbifTerm.gbifID,
      DcTerm.license,
      DcTerm.rights,
      DcTerm.rightsHolder,
      DcTerm.type,
      DwcTerm.basisOfRecord,
      DwcTerm.catalogNumber,
      DwcTerm.class_,
      DwcTerm.collectionCode,
      DwcTerm.continent,
      DwcTerm.coordinatePrecision,
      DwcTerm.coordinateUncertaintyInMeters,
      DwcTerm.country,
      DwcTerm.countryCode,
      DwcTerm.county,
      DwcTerm.datasetID,
      DwcTerm.datasetName,
      DwcTerm.dateIdentified,
      DwcTerm.day,
      DwcTerm.decimalLatitude,
      DwcTerm.decimalLongitude,
      DwcTerm.endDayOfYear,
      DwcTerm.eventDate,
      DwcTerm.eventID,
      DwcTerm.eventTime,
      DwcTerm.family,
      DwcTerm.footprintSpatialFit,
      DwcTerm.footprintSRS,
      DwcTerm.footprintWKT,
      DwcTerm.genus,
      DwcTerm.geodeticDatum,
      DwcTerm.higherClassification,
      DwcTerm.higherGeography,
      DwcTerm.higherGeographyID,
      DwcTerm.individualID,
      DwcTerm.institutionCode,
      DwcTerm.kingdom,
      DwcTerm.locality,
      DwcTerm.materialSampleID,
      DwcTerm.maximumDepthInMeters,
      DwcTerm.maximumDistanceAboveSurfaceInMeters,
      DwcTerm.maximumElevationInMeters,
      DwcTerm.minimumDepthInMeters,
      DwcTerm.minimumDistanceAboveSurfaceInMeters,
      DwcTerm.minimumElevationInMeters,
      DwcTerm.month,
      DwcTerm.occurrenceID,
      DwcTerm.order,
      DwcTerm.otherCatalogNumbers,
      DwcTerm.ownerInstitutionCode,
      DwcTerm.phylum,
      DwcTerm.previousIdentifications,
      DwcTerm.recordedBy,
      DwcTerm.recordNumber,
      DwcTerm.scientificName,
      DwcTerm.scientificNameAuthorship,
      DwcTerm.startDayOfYear,
      DwcTerm.stateProvince,
      DwcTerm.subgenus,
      DwcTerm.taxonRank,
      DwcTerm.typeStatus,
      DwcTerm.verbatimCoordinates,
      DwcTerm.verbatimCoordinateSystem,
      DwcTerm.verbatimDepth,
      DwcTerm.verbatimElevation,
      DwcTerm.verbatimEventDate,
      DwcTerm.verbatimLatitude,
      DwcTerm.verbatimLocality,
      DwcTerm.verbatimLongitude,
      DwcTerm.verbatimSRS,
      DwcTerm.verbatimTaxonRank,
      DwcTerm.vernacularName,
      DwcTerm.waterBody,
      DwcTerm.year
    );

    /**
     * The terms that will be included in the interpreted table if also present in ${@link Terms#interpretedTerms()}
     */
    public static final Set<Term> INTERPRETED_FIELDS = ImmutableSet.<Term>of(
      DcTerm.license,
      DcTerm.rights,
      DcTerm.rightsHolder,
      DcTerm.type,
      DwcTerm.basisOfRecord,
      DwcTerm.catalogNumber,
      DwcTerm.class_,
      DwcTerm.collectionCode,
      DwcTerm.continent,
      DwcTerm.coordinatePrecision,
      DwcTerm.coordinateUncertaintyInMeters,
      DwcTerm.countryCode,
      DwcTerm.datasetID,
      DwcTerm.datasetName,
      DwcTerm.dateIdentified,
      DwcTerm.day,
      DwcTerm.decimalLatitude,
      DwcTerm.decimalLongitude,
      DwcTerm.eventDate,
      DwcTerm.eventID,
      DwcTerm.family,
      DwcTerm.genus,
      DwcTerm.geodeticDatum,
      DwcTerm.individualID,
      DwcTerm.institutionCode,
      DwcTerm.kingdom,
      DwcTerm.locality,
      DwcTerm.materialSampleID,
      DwcTerm.month,
      DwcTerm.occurrenceID,
      DwcTerm.order,
      DwcTerm.phylum,
      DwcTerm.recordedBy,
      DwcTerm.recordNumber,
      DwcTerm.scientificName,
      DwcTerm.stateProvince,
      DwcTerm.subgenus,
      DwcTerm.typeStatus,
      DwcTerm.year,
      GbifTerm.classKey,
      GbifTerm.coordinateAccuracy,
      GbifTerm.datasetKey,
      GbifTerm.depth,
      GbifTerm.depthAccuracy,
      GbifTerm.distanceAboveSurface,
      GbifTerm.distanceAboveSurfaceAccuracy,
      GbifTerm.elevation,
      GbifTerm.elevationAccuracy,
      GbifTerm.familyKey,
      GbifTerm.gbifID,
      GbifTerm.genericName,
      GbifTerm.genusKey,
      GbifTerm.hasCoordinate,
      GbifTerm.hasGeospatialIssues,
      GbifTerm.issue,
      GbifTerm.kingdomKey,
      GbifTerm.lastInterpreted,
      GbifTerm.mediaType,
      GbifTerm.Multimedia,
      GbifTerm.orderKey,
      GbifTerm.phylumKey,
      GbifTerm.publishingCountry,
      GbifTerm.species,
      GbifTerm.speciesKey,
      GbifTerm.subgenusKey,
      GbifTerm.taxonKey,
      GbifTerm.typifiedName
    );
  }


  /**
   * @return the exclusion term set for (any) download format
   */
  private static Set<Term> buildExclusions() {
    ImmutableSet.Builder<Term> builder = ImmutableSet.builder();

    // excluded ALL dc terms not explicitly listed, as they are numerous and unnecessary
    Iterable<DcTerm> excludedDcTerms =
      Iterables.filter(Arrays.asList(DcTerm.values()),
                       Predicates.not(Predicates.in(ImmutableSet.of(DcTerm.license,
                                                                    DcTerm.rights,
                                                                    DcTerm.rightsHolder,
                                                                    DcTerm.type))));
    builder.addAll(excludedDcTerms);
    return builder.build();
  }

}
