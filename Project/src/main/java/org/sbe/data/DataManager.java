package org.sbe.data;

import java.util.ArrayList;
import java.util.List;

public class DataManager
{
    private List<Publication> publications;
    private List<Subscription> subscriptions;

    public DataManager()
    {
        this.publications = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
    }

    public DataManager(List<Publication> publications, List<Subscription> subscriptions)
    {
        this.setPublications(publications);
        this.setSubscriptions(subscriptions);
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
