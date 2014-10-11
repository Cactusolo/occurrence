
# TODO: alter this as development stabilizes
hadoop fs -rmr /user/tim/create-download-tables
hadoop fs -put target/workflow-create-tables /user/tim/create-download-tables


# Copy the hive HBase jars and Brickhouse (UDF lib) to the oozie share lib, so we can ignore them in our workflows
# Use of shared lib is enabled by setting:
#   <property>
#    <name>oozie.use.system.libpath</name>
#    <value>true</value>
#  </property>
# TODO: CDH5 has a different shared lib, so this will need changed
hadoop fs -cp /user/hive/auxjars2/brickhouse.jar /user/oozie/share/lib/hive
hadoop fs -cp /user/hive/auxjars2/guava-11.0.2.jar /user/oozie/share/lib/hive
hadoop fs -cp /user/hive/auxjars2/hbase-0.94.6-cdh4.3.0-security.jar /user/oozie/share/lib/hive
hadoop fs -cp /user/hive/auxjars2/hive-hbase-handler-0.10.0-cdh4.3.0.jar /user/oozie/share/lib/hive
hadoop fs -cp /user/hive/auxjars2/zookeeper-3.4.5-cdh4.3.0.jar /user/oozie/share/lib/hive
