<#-- @ftlvariable name="field" type="org.gbif.occurrence.download.hive.HBaseField" -->
<#--
  This is a freemarker template which will generate an HQL script.
  When run in Hive as a parameterized query, this will create a Hive table which is a
  backed by the HBase table.
-->

<#-- Required syntax to escape Hive parameters. Outputs "USE ${hive_db};" -->
USE ${r"${hive_db}"};

-- create the HBase table view
CREATE EXTERNAL TABLE IF NOT EXISTS occurrence_hbase (
<#list occurrence_hbase as field>
  ${field.hiveField} ${field.hiveDataType}<#if field_has_next>,</#if>
</#list>
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES ("hbase.columns.mapping" = "
<#list occurrence_hbase as field>
  ${field.hbaseColumn}<#if field_has_next>,</#if>
</#list>
") TBLPROPERTIES(
  "hbase.table.name" = "${r"${occurrence_hbase_table}"}",
  "hbase.table.default.storage.type" = "binary"
);
