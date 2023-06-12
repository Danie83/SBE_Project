package org.sbe.statistics;

import org.sbe.data.Publication;

import java.util.HashMap;
import java.util.Map;

public class Statistics
{
    private static Map<Publication, Timestamp> deliveryTime;
    private boolean initialized = false;

    public void initialize()
    {
        if (initialized)
        {
            return;
        }

        deliveryTime = new HashMap<>();
        initialized = true;
    }

    public void addStartTimestamp(Publication newPublication, long start)
    {
        deliveryTime.put(newPublication, new Timestamp(start));
    }

    public void updateEndTimestamp(Publication publication, long end)
    {
        Timestamp ts = deliveryTime.get(publication);
        if (ts == null)
        {
            return;
        }

        ts.setEnd(end);
        deliveryTime.put(publication, ts);
    }

    private class Timestamp
    {
        private Long start;
        private Long end;

        public Timestamp(long start, long end)
        {
            this.start = start;
            this.end = end;
        }

        public Timestamp(long start)
        {
            this.start = start;
            this.end = null;
        }

        public long calculateTime()
        {
            return (end - start);
        }

        public Long getStart()
        {
            return start;
        }

        public void setStart(Long start)
        {
            this.start = start;
        }

        public Long getEnd()
        {
            return end;
        }

        public void setEnd(Long end)
        {
            this.end = end;
        }
    }
}