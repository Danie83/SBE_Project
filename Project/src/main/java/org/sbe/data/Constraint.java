package org.sbe.data;

public class Constraint
{
    private String factor;
    private String operator;
    private String requiredValue;

    public Constraint(String factor, String operator, String requiredValue)
    {
        this.setFactor(factor);
        this.setOperator(operator);
        this.setRequiredValue(requiredValue);
    }

    public String getFactor()
    {
        return factor;
    }

    public void setFactor(String factor)
    {
        this.factor = factor;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getRequiredValue()
    {
        return requiredValue;
    }

    public void setRequiredValue(String requiredValue)
    {
        this.requiredValue = requiredValue;
    }
}
