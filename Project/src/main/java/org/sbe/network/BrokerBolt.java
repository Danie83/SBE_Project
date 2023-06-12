package org.sbe.network;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.sbe.ProtoClass;
import org.sbe.data.Constraint;
import org.sbe.data.Publication;
import org.sbe.data.Subscription;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.sbe.statistics.Statistics;

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
        if (sourceComponent.equals("publisher")) {
            byte[] serializedPublication = input.getBinaryByField("publication");
            ProtoClass.Publication deserializedPublication = null;
            try {
                deserializedPublication = ProtoClass.Publication.parseFrom(serializedPublication);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date publicationDate = null;
            try {
                publicationDate = dateFormat.parse(deserializedPublication.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Publication newPublication = new Publication(deserializedPublication.getStationId(), deserializedPublication.getCity(),
                    deserializedPublication.getTemp(), deserializedPublication.getRain(), deserializedPublication.getWind(),
                    deserializedPublication.getDirection(), publicationDate);

            publications.add(newPublication);
            notifySubscribers(newPublication);
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
            boolean canEmit = true;
            for (Constraint constraint : subscription.getConstraints())
            {
                if (!constraint.evaluateConstraint(publication))
                {
                    canEmit = false;
                    break;
                }
            }
            if (canEmit)
            {
                Statistics.updateEndTimestamp(publication, System.currentTimeMillis());
                collector.emit(new Values(subscription, publication));
            }
        }
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        declarer.declare(new Fields("subscription", "publication"));
    }
}
