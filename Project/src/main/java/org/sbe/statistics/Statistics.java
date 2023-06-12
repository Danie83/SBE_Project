package org.sbe.statistics;

import org.sbe.data.Publication;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

public class Statistics
{
    private static Map<Publication, Timestamp> deliveryTime;
    private static boolean initialized = false;
    

    public static void initialize()
    {
        if (initialized)
        {
            return;
        }

        deliveryTime = new HashMap<>();
        initialized = true;
    }

    public static void addStartTimestamp(Publication newPublication, long start)
    {
        deliveryTime.put(newPublication, new Timestamp(start));
    }

    public static void updateEndTimestamp(Publication publication, long end)
    {
        Timestamp ts = deliveryTime.get(publication);
        if (ts == null)
        {
            return;
        }

        ts.setEnd(end);
        deliveryTime.put(publication, ts);
    }

    public static long getSuccessfulPublications()
    {
        return deliveryTime.entrySet()
                           .stream()
                           .filter(e -> e.getValue().getEnd() != null)
                           .count();
    }

    public static double getAveragePublications()
    {
        OptionalDouble average = deliveryTime.entrySet()
                                             .stream()
                                             .filter(e -> e.getValue().getEnd() != null)
                                             .mapToDouble(e -> (e.getValue().getEnd() - e.getValue().getEnd()))
                                             .average();
        if (average.isPresent())
        {
            return average.getAsDouble();
        }
        else
        {
            return 0;
        }
    }

    private static class Timestamp
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