basic framework for reading and writing from kafka and hbase

###Compile Jar Files###

mvn clean package

###Run on Local Cluster###

*indicates could be run locally

Note: You can also run the twitter examples and basestorm

#Go in the target/
cd target

#Base Storm Run
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.basestorm.LearningStormTopology

#Aggregatetor Topo
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.storm.topology.AggregatorTopology

###Requires to be run on remote cluster###
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.hbase.CreateTable
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.storm.topology.KafkaConsumeTopology
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.storm.topology.KafkaPersistTopology
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.storm.topology.HBaseReaderTopo
java -cp storm-ten-framework-1.0.0-SNAPSHOT-local.jar com.frameworks.storm.topology.HBaseWriterTopo

#Note, this code currently run in local mode, you must use the cluster mode jar to deploy to production.

#Deploy to Cluster

storm jar topology-jar-path class ...
storm kill topology-name [-w wait-time-secs]
storm deactivate topology-name
storm activate topology-name
storm rebalance topology-name [-w wait-time-secs]

###For More Instructions Look In Resources###

CredentialTemplates - Here is where you put credentials to your distributed resources

Data - Where we store the test files to read in

Flux - Alternative Settings Storage

Hbase & Kafka Commands - Commands for operating Hbase and Kafka Shell




