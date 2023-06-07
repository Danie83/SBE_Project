package org.sbe.data;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class DataManager
{
    private static DataManager instance = null;
    private List<Publication> publications;
    private List<Subscription> subscriptions;

    private DataManager()
    {
        this.publications = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
    }

    public static DataManager getInstance()
    {
        if (instance == null)
        {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Publication> getPublications()
    {
        return publications;
    }

    public void setPublications(List<Publication> publications)
    {
        this.publications = publications;
    }

    public List<Subscription> getSubscriptions()
    {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions)
    {
        this.subscriptions = subscriptions;
    }

    public void addPublication(Publication publication)
    {
        this.publications.add(publication);
    }

    public void addSubscription(Subscription subscription)
    {
        this.subscriptions.add(subscription);
    }
}
