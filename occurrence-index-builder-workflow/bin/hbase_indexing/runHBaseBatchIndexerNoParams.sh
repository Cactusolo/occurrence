ZK_HOST=c1n9.gbif.org:2181,c1n8.gbif.org:2181,c1n10.gbif.org:2181/solr

SOLR_COLLECTION=occurrence
OUT_DIR=hdfs://c1n8.gbif.org:8020/solr/single_idx_hbase/
export HADOOP_CLASSPATH=gbif-api-0.14-SNAPSHOT.jar:occurrence-common-0.18-SNAPSHOT.jar:occurrence-search-0.18-SNAPSHOT.jar:/opt/cloudera/parcels/SOLR/lib/hbase-solr/lib/*
hadoop --config /etc/hadoop/conf jar /opt/cloudera/parcels/SOLR/lib/solr/contrib/mr/search-mr-*-job.jar  --conf /etc/hbase/conf/hbase-site.xml -D 'mapred.child.java.opts=-Xmx500m' -libjars /opt/cloudera/parcels/SOLR/lib/solr/server/webapps/solr/WEB-INF/lib/jts-1.13.jar,gbif-api-0.14-SNAPSHOT.jar,occurrence-common-0.18-SNAPSHOT.jar,occurrence-search-0.18-SNAPSHOT.jar --hbase-indexer-file hbase_occurrence_batch_morphline.xml --zk-host $ZK_HOST --collection $SOLR_COLLECTION --shards 1 --output-dir $OUT_DIR --solr-home-dir=solr/collection1/ --verbose --log4j log4j.properties