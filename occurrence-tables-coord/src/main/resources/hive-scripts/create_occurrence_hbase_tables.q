USE ${hive_db};

CREATE EXTERNAL TABLE IF NOT EXISTS occurrence_multimedia_hbase (gbifid INT,ext_multimedia STRING) STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES ("hbase.columns.mapping" = ":key,o:http_//rs.gbif.org/terms/1.0/Multimedia") TBLPROPERTIES("hbase.table.name" = "${occurrence_hbase_table}","hbase.table.default.storage.type" = "binary");

CREATE EXTERNAL TABLE IF NOT EXISTS occurrence_hbase (gbifid INT,v_abstract STRING,v_accessrights STRING,v_accrualmethod STRING,v_accrualperiodicity STRING,v_accrualpolicy STRING,v_alternative STRING,v_audience STRING,v_available STRING,v_bibliographiccitation STRING,v_conformsto STRING,v_contributor STRING,v_coverage STRING,v_created STRING,v_creator STRING,v_date_ STRING,v_dateaccepted STRING,v_datecopyrighted STRING,v_datesubmitted STRING,v_description STRING,v_educationlevel STRING,v_extent STRING,v_format_ STRING,v_hasformat STRING,v_haspart STRING,v_hasversion STRING,v_identifier STRING,v_instructionalmethod STRING,v_isformatof STRING,v_ispartof STRING,v_isreferencedby STRING,v_isreplacedby STRING,v_isrequiredby STRING,v_isversionof STRING,v_issued STRING,v_language STRING,v_license STRING,v_mediator STRING,v_medium STRING,v_modified STRING,v_provenance STRING,v_publisher STRING,v_references STRING,v_relation STRING,v_replaces STRING,v_requires STRING,v_rights STRING,v_rightsholder STRING,v_source STRING,v_spatial STRING,v_subject STRING,v_tableofcontents STRING,v_temporal STRING,v_title STRING,v_type STRING,v_valid STRING,v_acceptednameusage STRING,v_acceptednameusageid STRING,v_associatedmedia STRING,v_associatedoccurrences STRING,v_associatedreferences STRING,v_associatedsequences STRING,v_associatedtaxa STRING,v_basisofrecord STRING,v_bed STRING,v_behavior STRING,v_catalognumber STRING,v_class STRING,v_collectioncode STRING,v_collectionid STRING,v_continent STRING,v_coordinateprecision STRING,v_coordinateuncertaintyinmeters STRING,v_country STRING,v_countrycode STRING,v_county STRING,v_datageneralizations STRING,v_datasetid STRING,v_datasetname STRING,v_dateidentified STRING,v_day STRING,v_decimallatitude STRING,v_decimallongitude STRING,v_disposition STRING,v_dynamicproperties STRING,v_earliestageorloweststage STRING,v_earliesteonorlowesteonothem STRING,v_earliestepochorlowestseries STRING,v_earliesteraorlowesterathem STRING,v_earliestperiodorlowestsystem STRING,v_enddayofyear STRING,v_establishmentmeans STRING,v_eventdate STRING,v_eventid STRING,v_eventremarks STRING,v_eventtime STRING,v_family STRING,v_fieldnotes STRING,v_fieldnumber STRING,v_footprintsrs STRING,v_footprintspatialfit STRING,v_footprintwkt STRING,v_formation STRING,v_genus STRING,v_genericname STRING,v_geodeticdatum STRING,v_geologicalcontextid STRING,v_georeferenceddate STRING,v_georeferenceprotocol STRING,v_georeferenceremarks STRING,v_georeferencesources STRING,v_georeferenceverificationstatus STRING,v_georeferencedby STRING,v_group_ STRING,v_habitat STRING,v_higherclassification STRING,v_highergeography STRING,v_highergeographyid STRING,v_highestbiostratigraphiczone STRING,v_identificationid STRING,v_identificationqualifier STRING,v_identificationreferences STRING,v_identificationremarks STRING,v_identificationverificationstatus STRING,v_identifiedby STRING,v_individualcount STRING,v_individualid STRING,v_informationwithheld STRING,v_infraspecificepithet STRING,v_institutioncode STRING,v_institutionid STRING,v_island STRING,v_islandgroup STRING,v_kingdom STRING,v_latestageorhigheststage STRING,v_latesteonorhighesteonothem STRING,v_latestepochorhighestseries STRING,v_latesteraorhighesterathem STRING,v_latestperiodorhighestsystem STRING,v_lifestage STRING,v_lithostratigraphicterms STRING,v_locality STRING,v_locationaccordingto STRING,v_locationid STRING,v_locationremarks STRING,v_lowestbiostratigraphiczone STRING,v_materialsampleid STRING,v_maximumdepthinmeters STRING,v_maximumdistanceabovesurfaceinmeters STRING,v_maximumelevationinmeters STRING,v_member STRING,v_minimumdepthinmeters STRING,v_minimumdistanceabovesurfaceinmeters STRING,v_minimumelevationinmeters STRING,v_month STRING,v_municipality STRING,v_nameaccordingto STRING,v_nameaccordingtoid STRING,v_namepublishedin STRING,v_namepublishedinid STRING,v_namepublishedinyear STRING,v_nomenclaturalcode STRING,v_nomenclaturalstatus STRING,v_occurrencedetails STRING,v_occurrenceid STRING,v_occurrenceremarks STRING,v_occurrencestatus STRING,v_order_ STRING,v_originalnameusage STRING,v_originalnameusageid STRING,v_othercatalognumbers STRING,v_ownerinstitutioncode STRING,v_parentnameusage STRING,v_parentnameusageid STRING,v_phylum STRING,v_pointradiusspatialfit STRING,v_preparations STRING,v_previousidentifications STRING,v_recordnumber STRING,v_recordedby STRING,v_reproductivecondition STRING,v_samplingeffort STRING,v_samplingprotocol STRING,v_scientificname STRING,v_scientificnameauthorship STRING,v_scientificnameid STRING,v_sex STRING,v_specificepithet STRING,v_startdayofyear STRING,v_stateprovince STRING,v_subgenus STRING,v_taxonconceptid STRING,v_taxonid STRING,v_taxonrank STRING,v_taxonremarks STRING,v_taxonomicstatus STRING,v_typestatus STRING,v_typifiedname STRING,v_verbatimcoordinatesystem STRING,v_verbatimcoordinates STRING,v_verbatimdepth STRING,v_verbatimelevation STRING,v_verbatimeventdate STRING,v_verbatimlatitude STRING,v_verbatimlocality STRING,v_verbatimlongitude STRING,v_verbatimsrs STRING,v_verbatimtaxonrank STRING,v_vernacularname STRING,v_waterbody STRING,v_year STRING,identifiercount INT,crawlid INT,fragmentcreated BIGINT,xmlschema STRING,publishingorgkey STRING,unitqualifier STRING,abstract STRING,accessrights STRING,accrualmethod STRING,accrualperiodicity STRING,accrualpolicy STRING,alternative STRING,audience STRING,available STRING,bibliographiccitation STRING,conformsto STRING,contributor STRING,coverage STRING,created STRING,creator STRING,date_ STRING,dateaccepted STRING,datecopyrighted STRING,datesubmitted STRING,description STRING,educationlevel STRING,extent STRING,format_ STRING,hasformat STRING,haspart STRING,hasversion STRING,identifier STRING,instructionalmethod STRING,isformatof STRING,ispartof STRING,isreferencedby STRING,isreplacedby STRING,isrequiredby STRING,isversionof STRING,issued STRING,language STRING,license STRING,mediator STRING,medium STRING,modified BIGINT,provenance STRING,publisher STRING,references STRING,relation STRING,replaces STRING,requires STRING,rights STRING,rightsholder STRING,source STRING,spatial STRING,subject STRING,tableofcontents STRING,temporal STRING,title STRING,type STRING,valid STRING,acceptednameusage STRING,acceptednameusageid STRING,associatedoccurrences STRING,associatedreferences STRING,associatedsequences STRING,associatedtaxa STRING,basisofrecord STRING,bed STRING,behavior STRING,catalognumber STRING,class STRING,collectioncode STRING,collectionid STRING,continent STRING,countrycode STRING,county STRING,datageneralizations STRING,datasetid STRING,datasetname STRING,dateidentified BIGINT,day INT,decimallatitude DOUBLE,decimallongitude DOUBLE,disposition STRING,dynamicproperties STRING,earliestageorloweststage STRING,earliesteonorlowesteonothem STRING,earliestepochorlowestseries STRING,earliesteraorlowesterathem STRING,earliestperiodorlowestsystem STRING,enddayofyear STRING,establishmentmeans STRING,eventdate BIGINT,eventid STRING,eventremarks STRING,eventtime STRING,family STRING,fieldnotes STRING,fieldnumber STRING,footprintsrs STRING,footprintspatialfit STRING,footprintwkt STRING,formation STRING,genus STRING,genericname STRING,geodeticdatum STRING,geologicalcontextid STRING,georeferenceddate STRING,georeferenceprotocol STRING,georeferenceremarks STRING,georeferencesources STRING,georeferenceverificationstatus STRING,georeferencedby STRING,group_ STRING,habitat STRING,higherclassification STRING,highergeography STRING,highergeographyid STRING,highestbiostratigraphiczone STRING,identificationid STRING,identificationqualifier STRING,identificationreferences STRING,identificationremarks STRING,identificationverificationstatus STRING,identifiedby STRING,individualcount STRING,individualid STRING,informationwithheld STRING,infraspecificepithet STRING,institutioncode STRING,institutionid STRING,island STRING,islandgroup STRING,kingdom STRING,latestageorhigheststage STRING,latesteonorhighesteonothem STRING,latestepochorhighestseries STRING,latesteraorhighesterathem STRING,latestperiodorhighestsystem STRING,lifestage STRING,lithostratigraphicterms STRING,locality STRING,locationaccordingto STRING,locationid STRING,locationremarks STRING,lowestbiostratigraphiczone STRING,materialsampleid STRING,maximumdistanceabovesurfaceinmeters STRING,member STRING,minimumdistanceabovesurfaceinmeters STRING,month INT,municipality STRING,nameaccordingto STRING,nameaccordingtoid STRING,namepublishedin STRING,namepublishedinid STRING,namepublishedinyear STRING,nomenclaturalcode STRING,nomenclaturalstatus STRING,occurrencedetails STRING,occurrenceid STRING,occurrenceremarks STRING,occurrencestatus STRING,order_ STRING,originalnameusage STRING,originalnameusageid STRING,othercatalognumbers STRING,ownerinstitutioncode STRING,parentnameusage STRING,parentnameusageid STRING,phylum STRING,pointradiusspatialfit STRING,preparations STRING,previousidentifications STRING,recordnumber STRING,recordedby STRING,reproductivecondition STRING,samplingeffort STRING,samplingprotocol STRING,scientificname STRING,scientificnameid STRING,sex STRING,specificepithet STRING,startdayofyear STRING,stateprovince STRING,subgenus STRING,taxonconceptid STRING,taxonid STRING,taxonrank STRING,taxonremarks STRING,taxonomicstatus STRING,typestatus STRING,typifiedname STRING,verbatimcoordinatesystem STRING,verbatimcoordinates STRING,verbatimdepth STRING,verbatimelevation STRING,verbatimeventdate STRING,verbatimlocality STRING,verbatimsrs STRING,verbatimtaxonrank STRING,vernacularname STRING,waterbody STRING,year INT,datasetkey STRING,publishingcountry STRING,lastinterpreted BIGINT,coordinateaccuracy DOUBLE,elevation DOUBLE,elevationaccuracy DOUBLE,depth DOUBLE,depthaccuracy DOUBLE,distanceabovesurface STRING,distanceabovesurfaceaccuracy STRING,issue STRING,hascoordinate BOOLEAN,hasgeospatialissues BOOLEAN,taxonkey INT,kingdomkey INT,phylumkey INT,classkey INT,orderkey INT,familykey INT,genuskey INT,subgenuskey INT,specieskey INT,species STRING,protocol STRING,lastparsed BIGINT,lastcrawled BIGINT,zero_coordinate INT,coordinates_out_of_range INT,country_coordinate_mismatch INT,country_mismatch INT,country_invalid INT,country_derived_from_coordinates INT,continent_country_mismatch INT,continent_invalid INT,continent_derived_from_coordinates INT,presumed_swapped_coordinate INT,presumed_negated_longitude INT,presumed_negated_latitude INT,recorded_date_mismatch INT,recorded_date_invalid INT,recorded_year_unlikely INT,taxon_match_fuzzy INT,taxon_match_higherrank INT,taxon_match_none INT,depth_not_metric INT,depth_unlikely INT,depth_min_max_swapped INT,depth_non_numeric INT,elevation_unlikely INT,elevation_min_max_swapped INT,elevation_not_metric INT,elevation_non_numeric INT,modified_date_invalid INT,modified_date_unlikely INT,identified_date_unlikely INT,identified_date_invalid INT,basis_of_record_invalid INT,type_status_invalid INT,multimedia_date_invalid INT,multimedia_uri_invalid INT) STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES ("hbase.columns.mapping" = ":key,o:v_abstract,o:v_accessRights,o:v_accrualMethod,o:v_accrualPeriodicity,o:v_accrualPolicy,o:v_alternative,o:v_audience,o:v_available,o:v_bibliographicCitation,o:v_conformsTo,o:v_contributor,o:v_coverage,o:v_created,o:v_creator,o:v_date,o:v_dateAccepted,o:v_dateCopyrighted,o:v_dateSubmitted,o:v_description,o:v_educationLevel,o:v_extent,o:v_format,o:v_hasFormat,o:v_hasPart,o:v_hasVersion,o:v_identifier,o:v_instructionalMethod,o:v_isFormatOf,o:v_isPartOf,o:v_isReferencedBy,o:v_isReplacedBy,o:v_isRequiredBy,o:v_isVersionOf,o:v_issued,o:v_language,o:v_license,o:v_mediator,o:v_medium,o:v_modified,o:v_provenance,o:v_publisher,o:v_references,o:v_relation,o:v_replaces,o:v_requires,o:v_rights,o:v_rightsHolder,o:v_source,o:v_spatial,o:v_subject,o:v_tableOfContents,o:v_temporal,o:v_title,o:v_type,o:v_valid,o:v_acceptedNameUsage,o:v_acceptedNameUsageID,o:v_associatedMedia,o:v_associatedOccurrences,o:v_associatedReferences,o:v_associatedSequences,o:v_associatedTaxa,o:v_basisOfRecord,o:v_bed,o:v_behavior,o:v_catalogNumber,o:v_class,o:v_collectionCode,o:v_collectionID,o:v_continent,o:v_coordinatePrecision,o:v_coordinateUncertaintyInMeters,o:v_country,o:v_countryCode,o:v_county,o:v_dataGeneralizations,o:v_datasetID,o:v_datasetName,o:v_dateIdentified,o:v_day,o:v_decimalLatitude,o:v_decimalLongitude,o:v_disposition,o:v_dynamicProperties,o:v_earliestAgeOrLowestStage,o:v_earliestEonOrLowestEonothem,o:v_earliestEpochOrLowestSeries,o:v_earliestEraOrLowestErathem,o:v_earliestPeriodOrLowestSystem,o:v_endDayOfYear,o:v_establishmentMeans,o:v_eventDate,o:v_eventID,o:v_eventRemarks,o:v_eventTime,o:v_family,o:v_fieldNotes,o:v_fieldNumber,o:v_footprintSRS,o:v_footprintSpatialFit,o:v_footprintWKT,o:v_formation,o:v_genus,o:v_genericName,o:v_geodeticDatum,o:v_geologicalContextID,o:v_georeferencedDate,o:v_georeferenceProtocol,o:v_georeferenceRemarks,o:v_georeferenceSources,o:v_georeferenceVerificationStatus,o:v_georeferencedBy,o:v_group,o:v_habitat,o:v_higherClassification,o:v_higherGeography,o:v_higherGeographyID,o:v_highestBiostratigraphicZone,o:v_identificationID,o:v_identificationQualifier,o:v_identificationReferences,o:v_identificationRemarks,o:v_identificationVerificationStatus,o:v_identifiedBy,o:v_individualCount,o:v_individualID,o:v_informationWithheld,o:v_infraspecificEpithet,o:v_institutionCode,o:v_institutionID,o:v_island,o:v_islandGroup,o:v_kingdom,o:v_latestAgeOrHighestStage,o:v_latestEonOrHighestEonothem,o:v_latestEpochOrHighestSeries,o:v_latestEraOrHighestErathem,o:v_latestPeriodOrHighestSystem,o:v_lifeStage,o:v_lithostratigraphicTerms,o:v_locality,o:v_locationAccordingTo,o:v_locationID,o:v_locationRemarks,o:v_lowestBiostratigraphicZone,o:v_materialSampleID,o:v_maximumDepthInMeters,o:v_maximumDistanceAboveSurfaceInMeters,o:v_maximumElevationInMeters,o:v_member,o:v_minimumDepthInMeters,o:v_minimumDistanceAboveSurfaceInMeters,o:v_minimumElevationInMeters,o:v_month,o:v_municipality,o:v_nameAccordingTo,o:v_nameAccordingToID,o:v_namePublishedIn,o:v_namePublishedInID,o:v_namePublishedInYear,o:v_nomenclaturalCode,o:v_nomenclaturalStatus,o:v_occurrenceDetails,o:v_occurrenceID,o:v_occurrenceRemarks,o:v_occurrenceStatus,o:v_order,o:v_originalNameUsage,o:v_originalNameUsageID,o:v_otherCatalogNumbers,o:v_ownerInstitutionCode,o:v_parentNameUsage,o:v_parentNameUsageID,o:v_phylum,o:v_pointRadiusSpatialFit,o:v_preparations,o:v_previousIdentifications,o:v_recordNumber,o:v_recordedBy,o:v_reproductiveCondition,o:v_samplingEffort,o:v_samplingProtocol,o:v_scientificName,o:v_scientificNameAuthorship,o:v_scientificNameID,o:v_sex,o:v_specificEpithet,o:v_startDayOfYear,o:v_stateProvince,o:v_subgenus,o:v_taxonConceptID,o:v_taxonID,o:v_taxonRank,o:v_taxonRemarks,o:v_taxonomicStatus,o:v_typeStatus,o:v_typifiedName,o:v_verbatimCoordinateSystem,o:v_verbatimCoordinates,o:v_verbatimDepth,o:v_verbatimElevation,o:v_verbatimEventDate,o:v_verbatimLatitude,o:v_verbatimLocality,o:v_verbatimLongitude,o:v_verbatimSRS,o:v_verbatimTaxonRank,o:v_vernacularName,o:v_waterBody,o:v_year,o:identifierCount,o:crawlId,o:fragmentCreated,o:xmlSchema,o:publishingOrgKey,o:unitQualifier,o:v_abstract,o:v_accessRights,o:v_accrualMethod,o:v_accrualPeriodicity,o:v_accrualPolicy,o:v_alternative,o:v_audience,o:v_available,o:v_bibliographicCitation,o:v_conformsTo,o:v_contributor,o:v_coverage,o:v_created,o:v_creator,o:v_date,o:v_dateAccepted,o:v_dateCopyrighted,o:v_dateSubmitted,o:v_description,o:v_educationLevel,o:v_extent,o:v_format,o:v_hasFormat,o:v_hasPart,o:v_hasVersion,o:v_identifier,o:v_instructionalMethod,o:v_isFormatOf,o:v_isPartOf,o:v_isReferencedBy,o:v_isReplacedBy,o:v_isRequiredBy,o:v_isVersionOf,o:v_issued,o:v_language,o:v_license,o:v_mediator,o:v_medium,o:modified,o:v_provenance,o:v_publisher,o:v_references,o:v_relation,o:v_replaces,o:v_requires,o:v_rights,o:v_rightsHolder,o:v_source,o:v_spatial,o:v_subject,o:v_tableOfContents,o:v_temporal,o:v_title,o:v_type,o:v_valid,o:v_acceptedNameUsage,o:v_acceptedNameUsageID,o:v_associatedOccurrences,o:v_associatedReferences,o:v_associatedSequences,o:v_associatedTaxa,o:basisOfRecord,o:v_bed,o:v_behavior,o:v_catalogNumber,o:class,o:v_collectionCode,o:v_collectionID,o:continent,o:countryCode,o:v_county,o:v_dataGeneralizations,o:v_datasetID,o:v_datasetName,o:dateIdentified,o:day,o:decimalLatitude,o:decimalLongitude,o:v_disposition,o:v_dynamicProperties,o:v_earliestAgeOrLowestStage,o:v_earliestEonOrLowestEonothem,o:v_earliestEpochOrLowestSeries,o:v_earliestEraOrLowestErathem,o:v_earliestPeriodOrLowestSystem,o:v_endDayOfYear,o:establishmentMeans,o:eventDate,o:v_eventID,o:v_eventRemarks,o:v_eventTime,o:family,o:v_fieldNotes,o:v_fieldNumber,o:v_footprintSRS,o:v_footprintSpatialFit,o:v_footprintWKT,o:v_formation,o:genus,o:genericName,o:v_geodeticDatum,o:v_geologicalContextID,o:v_georeferencedDate,o:v_georeferenceProtocol,o:v_georeferenceRemarks,o:v_georeferenceSources,o:v_georeferenceVerificationStatus,o:v_georeferencedBy,o:v_group,o:v_habitat,o:v_higherClassification,o:v_higherGeography,o:v_higherGeographyID,o:v_highestBiostratigraphicZone,o:v_identificationID,o:v_identificationQualifier,o:v_identificationReferences,o:v_identificationRemarks,o:v_identificationVerificationStatus,o:v_identifiedBy,o:individualCount,o:v_individualID,o:v_informationWithheld,o:infraspecificEpithet,o:v_institutionCode,o:v_institutionID,o:v_island,o:v_islandGroup,o:kingdom,o:v_latestAgeOrHighestStage,o:v_latestEonOrHighestEonothem,o:v_latestEpochOrHighestSeries,o:v_latestEraOrHighestErathem,o:v_latestPeriodOrHighestSystem,o:lifeStage,o:v_lithostratigraphicTerms,o:v_locality,o:v_locationAccordingTo,o:v_locationID,o:v_locationRemarks,o:v_lowestBiostratigraphicZone,o:v_materialSampleID,o:v_maximumDistanceAboveSurfaceInMeters,o:v_member,o:v_minimumDistanceAboveSurfaceInMeters,o:month,o:v_municipality,o:v_nameAccordingTo,o:v_nameAccordingToID,o:v_namePublishedIn,o:v_namePublishedInID,o:v_namePublishedInYear,o:v_nomenclaturalCode,o:v_nomenclaturalStatus,o:v_occurrenceDetails,o:v_occurrenceID,o:v_occurrenceRemarks,o:v_occurrenceStatus,o:order,o:v_originalNameUsage,o:v_originalNameUsageID,o:v_otherCatalogNumbers,o:v_ownerInstitutionCode,o:v_parentNameUsage,o:v_parentNameUsageID,o:phylum,o:v_pointRadiusSpatialFit,o:v_preparations,o:v_previousIdentifications,o:v_recordNumber,o:v_recordedBy,o:v_reproductiveCondition,o:v_samplingEffort,o:v_samplingProtocol,o:scientificName,o:v_scientificNameID,o:sex,o:specificEpithet,o:v_startDayOfYear,o:stateProvince,o:subgenus,o:v_taxonConceptID,o:v_taxonID,o:taxonRank,o:v_taxonRemarks,o:v_taxonomicStatus,o:typeStatus,o:typifiedName,o:v_verbatimCoordinateSystem,o:v_verbatimCoordinates,o:v_verbatimDepth,o:v_verbatimElevation,o:v_verbatimEventDate,o:v_verbatimLocality,o:v_verbatimSRS,o:v_verbatimTaxonRank,o:v_vernacularName,o:waterBody,o:year,o:datasetKey,o:publishingCountry,o:lastInterpreted,o:coordinateAccuracy,o:elevation,o:elevationAccuracy,o:depth,o:depthAccuracy,o:v_distanceAboveSurface,o:v_distanceAboveSurfaceAccuracy,o:issue,o:v_hasCoordinate,o:v_hasGeospatialIssues,o:taxonKey,o:kingdomKey,o:phylumKey,o:classKey,o:orderKey,o:familyKey,o:genusKey,o:subgenusKey,o:speciesKey,o:species,o:protocol,o:lastParsed,o:lastCrawled,o:_iss_ZERO_COORDINATE,o:_iss_COORDINATES_OUT_OF_RANGE,o:_iss_COUNTRY_COORDINATE_MISMATCH,o:_iss_COUNTRY_MISMATCH,o:_iss_COUNTRY_INVALID,o:_iss_COUNTRY_DERIVED_FROM_COORDINATES,o:_iss_CONTINENT_COUNTRY_MISMATCH,o:_iss_CONTINENT_INVALID,o:_iss_CONTINENT_DERIVED_FROM_COORDINATES,o:_iss_PRESUMED_SWAPPED_COORDINATE,o:_iss_PRESUMED_NEGATED_LONGITUDE,o:_iss_PRESUMED_NEGATED_LATITUDE,o:_iss_RECORDED_DATE_MISMATCH,o:_iss_RECORDED_DATE_INVALID,o:_iss_RECORDED_YEAR_UNLIKELY,o:_iss_TAXON_MATCH_FUZZY,o:_iss_TAXON_MATCH_HIGHERRANK,o:_iss_TAXON_MATCH_NONE,o:_iss_DEPTH_NOT_METRIC,o:_iss_DEPTH_UNLIKELY,o:_iss_DEPTH_MIN_MAX_SWAPPED,o:_iss_DEPTH_NON_NUMERIC,o:_iss_ELEVATION_UNLIKELY,o:_iss_ELEVATION_MIN_MAX_SWAPPED,o:_iss_ELEVATION_NOT_METRIC,o:_iss_ELEVATION_NON_NUMERIC,o:_iss_MODIFIED_DATE_INVALID,o:_iss_MODIFIED_DATE_UNLIKELY,o:_iss_IDENTIFIED_DATE_UNLIKELY,o:_iss_IDENTIFIED_DATE_INVALID,o:_iss_BASIS_OF_RECORD_INVALID,o:_iss_TYPE_STATUS_INVALID,o:_iss_MULTIMEDIA_DATE_INVALID,o:_iss_MULTIMEDIA_URI_INVALID") TBLPROPERTIES("hbase.table.name" = "${occurrence_hbase_table}","hbase.table.default.storage.type" = "binary");

