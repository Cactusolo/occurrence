cd ..
mvn -Poozie clean package assembly:single
mvn -Psolr package assembly:single
cd bin
cat jobsingleshard.properties | sed 's/\./_/g' > .properties.file
. .properties.file
rm .properties.file
echo "Cleaning HDFS directory $oozieWfDestination"
hadoop fs -rm -r -skipTrash $oozieWfDestination*
echo "Copying workflow to HDFS directory $oozieWfDestination" 
hadoop dfs -put ../target/oozie-workflow/* $oozieWfDestination
oozie_server=${oozie_server//[_]/.}
echo "Running oozie workflow on server $oozie_server"
oozie job -oozie $oozie_server -config jobsingleshard.properties -run