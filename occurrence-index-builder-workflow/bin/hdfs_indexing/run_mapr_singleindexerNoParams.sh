ZK_HOST=c1n9.gbif.org:2181,c1n8.gbif.org:2181,c1n10.gbif.org:2181/solr
SOLR_COLLECTION=occurrence
OUT_DIR=hdfs://c1n8.gbif.org:8020/solr/solr_single_shard/
INPUT_DIR=hdfs://c1n8.gbif.org:8020/user/hive/warehouse/uat.db/occurrence_avro/
export HADOOP_CLIENT_OPTS="-Xmx2073741824 $HADOOP_CLIENT_OPTS"
hadoop --config /etc/hadoop/conf.cloudera.mapreduce1 jar /opt/cloudera/parcels/SOLR/lib/solr/contrib/mr/search-mr-*-job.jar org.apache.solr.hadoop.MapReduceIndexerTool -D mapred.child.java.opts=-Xmx2073741824 -D morphlineVariable.ENV_ZK_HOST=$ZK_HOST -D morphlineVariable.ENV_SOLR_COLLECTION=$SOLR_COLLECTION -libjars jts-1.13.jar --log4j /opt/cloudera/parcels/SOLR/share/doc/search-1.0.0/examples/solr-nrt/log4j.properties --morphline-file solr_occurrence_morphline.conf --shards 1 --output-dir $OUT_DIR --solr-home-dir=solr/collection1/ --verbose $INPUT_DIR
