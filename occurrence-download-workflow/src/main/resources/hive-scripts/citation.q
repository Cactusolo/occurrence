set mapred.output.compress=true;
set hive.exec.compress.output=true;

CREATE TABLE ${citation_table}
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
AS
SELECT datasetkey, count(*) as num_occurrences FROM ${query_result_table} GROUP BY datasetkey;
