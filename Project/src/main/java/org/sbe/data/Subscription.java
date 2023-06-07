package org.sbe.data;

import java.util.List;

public class Subscription
{
    private List<Constraint> constraints;

    public Subscription(List<Constraint> constraints)
    {
        this.setConstraints(constraints);
    }

    public List<Constraint> getConstraints()
    {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints)
    {
        this.constraints = constraints;
    }

    public void addConstraint(Constraint constraint)
    {
        this.constraints.add(constraint);
    }
}
