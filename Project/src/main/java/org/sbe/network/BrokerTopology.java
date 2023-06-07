package org.sbe.network;

import org.apache.storm.Config;
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
        config.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, 1);
        config.setNumWorkers(3);

        // TODO: run topology in local mode
    }
}
