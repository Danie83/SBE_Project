package org.sbe.network;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.sbe.data.DataManager;
import org.sbe.data.Publication;
import org.sbe.data.Subscription;

import java.util.List;
import java.util.Map;

public class SubscriptionFilterBolt
extends BaseRichBolt
{
    private OutputCollector collector;
    private List<Subscription> subscriptions;

    public void prepare(Map<String, Object> conf,
                        TopologyContext context,
                        OutputCollector collector)
    {
        this.collector = collector;
        this.subscriptions = DataManager.getInstance().getSubscriptions();
    }

    public void execute(Tuple input)
    {
        Publication publication = (Publication) input.getValueByField("publication");

        for (Subscription subscription : subscriptions)
        {
            // TODO: match constraints
            if (subscription.getConstraints().size() == 0)
            {
                collector.emit(new Values(publication));
                break;
            }
        }

        collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        declarer.declare(new Fields("publication"));
    }
}
