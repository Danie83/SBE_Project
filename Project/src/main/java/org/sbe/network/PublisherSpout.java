package org.sbe.network;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.sbe.ProtoClass;
import org.sbe.data.DataManager;
import org.sbe.data.Publication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PublisherSpout
extends BaseRichSpout
{
    private SpoutOutputCollector collector;
    private List<Publication> publications;
    private int index;

    public void open(Map conf,
                     TopologyContext context,
                     SpoutOutputCollector collector)
    {
        this.collector = collector;
        this.publications = DataManager.getInstance().getPublications();
        this.index = 0;
    }

    public void nextTuple()
    {
        Publication publication = publications.get(index);
        ProtoClass.Publication.Builder serializedPublication = new ProtoClass.Publication().newBuilder()
                .setCity(publication.getCity())
                .setDate(publication.getDate().toString())
                .setDirection(publication.getDirection())
                .setStationId(publication.getStationId())
                .setTemp(publication.getTemp())
                .setRain(publication.getRain())
                .setWind(publication.getWind());

        ProtoClass.Publication message = serializedPublication.build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try
        {
            message.writeTo(outputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        byte[] emmitedSerializedPublication = outputStream.toByteArray();
        this.collector.emit(new Values(emmitedSerializedPublication));
        index++;

        if (index > publications.size())
        {
            index = 0;
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        declarer.declare(new Fields("publication"));
    }
}
