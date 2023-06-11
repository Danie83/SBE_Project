package org.sbe.network;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class BrokerTopology
{
    public static void execute()
    {
        TopologyBuilder builder = new TopologyBuilder();

        PublisherSpout spout = new PublisherSpout();
        String spoutId = "publisherSpout";
        int spoutParallelism = 3;

        BrokerBolt brokerBolt = new BrokerBolt();
        String brokerBoltId = "brokerBolt";
        int brokerBoltParallelism = 3;

        builder.setSpout(spoutId, spout, spoutParallelism);
        builder.setBolt(brokerBoltId, brokerBolt, brokerBoltParallelism)
                .setNumTasks(brokerBoltParallelism)
               .shuffleGrouping(spoutId);

        Config config = new Config();
        config.setDebug(true);
        config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 1024);
        config.setNumWorkers(3);

        LocalCluster cluster = new LocalCluster();
        StormTopology topology = builder.createTopology();
        cluster.submitTopology("publish-subscribe-topology", config, topology);

        Utils.sleep(20000);

        cluster.killTopology("project-topology");
        cluster.shutdown();
    }
}
