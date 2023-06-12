package org.sbe.network;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
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

public class BrokerWindowBolt
extends BaseWindowedBolt
{
    private OutputCollector collector;
    private List<Subscription> subscriptions;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector)
    {
        this.collector = collector;
        this.subscriptions = new ArrayList<>();
    }

    @Override
    public void execute(TupleWindow inputWindow)
    {
        float avgRain = (float) 0;
        float avgTemp = (float) 0;
        float avgWind = (float) 0;
        for (Tuple tuple : inputWindow.get())
        {
            byte[] serializedPublication = tuple.getBinaryByField("publication");
            Publication publication = deserializePublication(serializedPublication);
            avgRain += publication.getRain();
            avgTemp += publication.getTemp();
            avgWind += publication.getWind();
        }
        if (avgRain != 0)
        {
            avgRain /= inputWindow.get().size();
        }
        if (avgTemp != 0)
        {
            avgTemp /= inputWindow.get().size();
        }
        if (avgWind != 0)
        {
            avgWind /= inputWindow.get().size();
        }

        for (Tuple tuple : inputWindow.get())
        {
            byte[] serializedPublication = tuple.getBinaryByField("publication");
            Publication publication = deserializePublication(serializedPublication);
            Subscription subscription = (Subscription) tuple.getValueByField("subscription");
            boolean canEmit = true;
            for (Constraint constraint : subscription.getConstraints())
            {
                if (constraint.getAvg())
                {
                    switch (constraint.getFactor())
                    {
                        case "rain":
                            if (!constraint.evaluateAverage(avgRain))
                            {
                                canEmit = false;
                            }
                            break;
                        case "wind":
                            if (!constraint.evaluateAverage(avgWind))
                            {
                                canEmit = false;
                            }
                            break;
                        case "temp":
                            if(!constraint.evaluateAverage(avgTemp))
                            {
                                canEmit = false;
                            }
                            break;
                        default:
                            canEmit = false;
                            break;
                    }

                    if (!canEmit)
                    {
                        break;
                    }
                }
                else
                {
                    if (!constraint.evaluateConstraint(publication))
                    {
                        canEmit = false;
                        break;
                    }
                }
            }
            if (canEmit)
            {
                collector.emit(new Values(subscription, publication));
                collector.ack(tuple);
            }
        }

    }

    private Publication deserializePublication(byte[] serializedPublication)
    {
        ProtoClass.Publication deserializedPublication = null;
        try
        {
            deserializedPublication = ProtoClass.Publication.parseFrom(serializedPublication);
        }
        catch (InvalidProtocolBufferException e)
        {
            throw new RuntimeException(e);
        }

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date publicationDate = null;
        try
        {
            publicationDate = dateFormat.parse(deserializedPublication.getDate());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Publication newPublication = new Publication(deserializedPublication.getStationId(), deserializedPublication.getCity(),
                deserializedPublication.getTemp(), deserializedPublication.getRain(), deserializedPublication.getWind(),
                deserializedPublication.getDirection(), publicationDate);
        return newPublication;
    }
}
