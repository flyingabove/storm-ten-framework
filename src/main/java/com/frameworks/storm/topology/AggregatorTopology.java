package com.frameworks.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.frameworks.storm.aggregators.BasicAggregator;
import com.frameworks.storm.debug.Debug;
import com.frameworks.storm.operation.FruitParser;
import com.frameworks.storm.providers.LineProvider;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import java.io.InputStream;

@Slf4j
@Setter
public class AggregatorTopology {

  String filePath;
  int batchSize;

  private void getTopology()throws Exception{
    TridentTopology topology = new TridentTopology();
    LineProvider sp = new LineProvider(filePath,batchSize); //(path to file,batchsize)

    Stream aggregatedStream = topology.newStream("spout1", sp.createSpout()) //create aggregated stream

            .each(new Fields("str"),new FruitParser(),new Fields("id","fruit","color","weight"))
            //.each(new Fields("id","fruit","color","weight"),new Debug(),new Fields());
            //.each(new Fields("id","fruit","color","weight"),new Debug(),new Fields())
            .groupBy(new Fields("fruit"))
            .aggregate(new Fields("fruit"),new BasicAggregator(),new Fields("fruitName","fruitCount"))
            .each(new Fields("fruitName","fruitCount"),new Debug(),new Fields());

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
    AggregatorTopology aggrTopo= yaml.loadAs(in, AggregatorTopology.class);
    aggrTopo.getTopology();
  }
}
