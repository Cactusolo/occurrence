<#--
  This is a freemarker template which will generate an HQL script.
  When run in Hive as a parameterized query, this will create a set of optimized tables which are used
  during downloads.
-->
<#-- Required syntax to escape Hive parameters. Outputs "USE ${hive_db};" -->
USE ${r"${hive_db}"};

-- snappy compression
SET hive.exec.compress.output=true;
SET mapred.output.compression.type=BLOCK;
SET mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec;

--
-- Full download
--
CREATE TABLE download_full STORED AS RCFILE AS
SELECT
<TODO/>
FROM occurrence_hdfs;

--
-- Simple download
--
CREATE TABLE download_simple STORED AS RCFILE AS
SELECT
<TODO/>
FROM occurrence_hdfs;



