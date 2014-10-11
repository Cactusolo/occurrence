<#--
  This is a freemarker template which will generate an HQL script which is run at download time.
  When run in Hive as a parameterized query, this will create a set of tables ... TODO... .
-->
<#-- Required syntax to escape Hive parameters. Outputs "USE ${hive_db};" -->
USE ${r"${hive_db}"};

CREATE TEMPORARY FUNCTION cleanNull AS 'org.gbif.occurrence.hive.udf.NullStringRemoverUDF';
CREATE TEMPORARY FUNCTION contains AS 'org.gbif.occurrence.hive.udf.ContainsUDF';
CREATE TEMPORARY FUNCTION toISO8601 AS 'org.gbif.occurrence.hive.udf.ToISO8601UDF';
CREATE TEMPORARY FUNCTION cleanDelimiters AS 'org.gbif.occurrence.hive.udf.CleanDelimiterCharsUDF';
CREATE TEMPORARY FUNCTION joinArray AS 'brickhouse.udf.collect.JoinArrayUDF';

-- TODO: enable this when we're done testing the basics
-- setup for our custom, combinable deflated compression
-- SET hive.exec.compress.output=true;
-- SET io.seqfile.compression.type=BLOCK;
-- SET mapred.output.compression.codec=org.gbif.hadoop.compress.d2.D2Codec;
-- SET io.compression.codecs=org.gbif.hadoop.compress.d2.D2Codec;

-- in case this job is relaunched
DROP TABLE IF EXISTS ${verbatimTable};
DROP TABLE IF EXISTS ${interpretedTable};
DROP TABLE IF EXISTS ${citationTable};
DROP TABLE IF EXISTS ${multimediaTable};

-- pre-create verbatim table so it can be used in the multi-insert
CREATE TABLE ${verbatimTable} (
<#list verbatimFields as field>
${field.hiveField} ${field.hiveDataType}<#if field_has_next>,</#if>
</#list>
) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';

-- pre-create interpreted table so it can be used in the multi-insert
CREATE TABLE ${interpretedTable} (
<#list interpretedFields as field>
${field.hiveField} ${field.hiveDataType}<#if field_has_next>,</#if>
</#list>
) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';

--
-- Uses multi-table inserts format to reduce to a single scan of the source table.
--
<#-- NOTE: Formatted below to generate nice output at expense of ugliness in this template -->
FROM ${sourceTable}
  INSERT INTO TABLE ${verbatimTable}
  SELECT
<#list verbatimFields as field>
    ${field}<#if field_has_next>,</#if>
</#list>
  WHERE ${whereClause}
  INSERT INTO TABLE ${interpTable}
  SELECT
<#list interpretedFields as field>
    ${field}<#if field_has_next>,</#if>
</#list>
  WHERE ${whereClause};

--
-- Creates the citation table
--
CREATE TABLE ${citationTable}
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
AS SELECT datasetkey, count(*) as num_occurrences FROM ${interpTable} GROUP BY datasetkey;

--
-- Creates the multimedia table
--
CREATE TABLE ${multimediaTable}
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
AS SELECT m.*
FROM multimedia_hdfs m
  JOIN ${interpTable} i ON m.gbifId = i.id;
