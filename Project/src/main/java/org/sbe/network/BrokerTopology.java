package org.sbe.network;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

public class BrokerTopology
{
    public static void execute()
    {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("publication-spout", new PublicationSpout());
        builder.setBolt("subscription-filter-bolt", new SubscriptionFilterBolt())
               .shuffleGrouping("publication-spout");

        Config config = new Config();
        config.setDebug(true);
        config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 1024);
        config.setNumWorkers(3);

        LocalCluster cluster = new LocalCluster();
        StormTopology topology = builder.createTopology();
        cluster.submitTopology("project-topology", config, topology);

        try
        {
            Thread.sleep(20000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        cluster.killTopology("project-topology");
        cluster.shutdown();
    }
}
