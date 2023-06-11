package org.sbe.network;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.sbe.data.Constraint;
import org.sbe.data.Publication;
import org.sbe.data.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrokerBolt
extends BaseRichBolt
{
    private OutputCollector collector;
    private List<Publication> publications;
    private List<Subscription> subscriptions;

    @Override
    public void prepare(Map conf,
                        TopologyContext context,
                        OutputCollector collector)
    {
        this.collector = collector;
        this.publications = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
    }

    @Override
    public void execute(Tuple input)
    {
        String sourceComponent = input.getSourceComponent();
        if (sourceComponent.equals("publisher"))
        {
            Publication publication = (Publication) input.getValueByField("publication");
            publications.add(publication);
            notifySubscribers(publication);
        }
        else if (sourceComponent.equals("subscriber"))
        {
            Subscription subscription = (Subscription) input.getValueByField("subscription");
            subscriptions.add(subscription);
        }
        collector.ack(input);
    }

    private void notifySubscribers(Publication publication)
    {
        for (Subscription subscription : subscriptions)
        {
            for (Constraint constraint : subscription.getConstraints())
            {
                if (constraint.evaluateConstraint(publication))
                {
                    collector.emit(new Values(subscription, publication));
                }
            }
        }
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        declarer.declare(new Fields("subscription", "publication"));
    }
}
