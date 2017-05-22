package com.frameworks.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.frameworks.storm.aggregators.BasicAggregator;
import com.frameworks.storm.debug.Debug;
import com.frameworks.storm.operation.FruitParser;
import com.frameworks.storm.providers.LineProvider;
import com.frameworks.storm.state.custom.FruitStateFactory;
import com.frameworks.storm.state.custom.TransactionStateQuery;
import com.frameworks.storm.state.custom.TransactionStateUpdater;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;

import java.io.InputStream;

@Slf4j
@Setter
public class StateTopology {

  String filePath;
  int batchSize;

  private void getTopology()throws Exception{
    TridentTopology topology = new TridentTopology();
    LineProvider sp = new LineProvider(filePath,batchSize); //(path to file,batchsize)

    Stream aggregatedStream = topology.newStream("spout1", sp.createSpout()) //create aggregated stream
            .each(new Fields("str"),new FruitParser(),new Fields("id","fruit","color","weight"))
            //.each(new Fields("str"),new Debug(),new Fields())
            //.each(new Fields("id","fruit","color","weight"),new Debug(),new Fields())
            .groupBy(new Fields("fruit"))
            .aggregate(new Fields("fruit"),new BasicAggregator(),new Fields("fruitName","fruitCount"));
            //.each(new Fields("fruitName","fruitCount"),new Debug(),new Fields());

    TridentState FruitState = aggregatedStream //persist the data into the state
            .partitionPersist(new FruitStateFactory(),new Fields("fruitName","fruitCount"),new TransactionStateUpdater("fruitName","fruitCount"),new Fields("fruitName"));

    Stream queryStream = FruitState.newValuesStream()
            .stateQuery(FruitState,new Fields("fruitName"),new TransactionStateQuery("fruitName"),new Fields("fruitTotals"))
            .each(new Fields("fruitName","fruitTotals"),new Debug(),new Fields());

    Config conf = new Config();

    LocalCluster localCluster = new LocalCluster();

    //Local Mode
    localCluster.submitTopology("kafkaTridentTest", conf, topology.build());

    //Submit to Cluster Mode
    //StormSubmitter.submitTopology("kafkaTridentTest", conf, topology.build());

  }

  @SneakyThrows
  public static void main(String args[]){

    Yaml yaml = new Yaml();
    InputStream in = ClassLoader.getSystemResourceAsStream("confidential/topocredentials/aggr-credentials.yml");
    StateTopology aggrTopo= yaml.loadAs(in, StateTopology.class);
    aggrTopo.getTopology();
  }
}
