package org.sbe.network;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.sbe.data.DataManager;
import org.sbe.data.Publication;

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
        this.collector.emit(new Values(publication));
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
