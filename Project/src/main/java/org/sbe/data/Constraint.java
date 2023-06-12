package org.sbe.data;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that represents a subscription constraint.
 */
public class Constraint
{
    /** The publication field as a string that is part of the constraint. */

    private String factor;

    /** The operator which is used to evaluate the constraint. */
    private String operator;

    /** The required value that is evaluated against the publication field. */
    private String requiredValue;

    private Boolean avg;

    /**
     * Basic constructor.
     *
     * @param   factor
     *          The factor of the constraint.
     * @param   operator
     *          The operator of the constraint.
     * @param   requiredValue
     *          The required value of the constraint.
     */
    public Constraint(String factor, String operator, String requiredValue, String avg)
    {
        this.setFactor(factor);
        this.setOperator(operator);
        this.setRequiredValue(requiredValue);
        this.setAvg(avg.equals("True"));
    }

    /**
     * Getter for the factor property.
     *
     * @return  The factor property.
     */
    public String getFactor()
    {
        return factor;
    }

    public void setFactor(String newFactor)
    {
        this.factor = newFactor.equals("station_id") ? "stationId" : newFactor;
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

    public boolean evaluateConstraint(Publication publication)
    {
        Object value = null;
        try
        {
            Class<?> targetPublicationClass = publication.getClass();
            Field field = targetPublicationClass.getDeclaredField(factor);
            field.setAccessible(true);
            value = field.get(publication);
        }
        catch (NoSuchFieldException e)
        {
            return false;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }

        if (value == null)
        {
            return false;
        }

        switch (operator)
        {
            case "==":
                return compareEqual(value, requiredValue);
            case "!=":
                return !compareEqual(value, requiredValue);
            case "<":
                return compareLower(value, requiredValue);
            case ">=":
                return !compareLower(value, requiredValue);
            case ">":
                return compareGreater(value, requiredValue);
            case "<=":
                return !compareGreater(value, requiredValue);
            default:
                return false;
        }
    }

    public boolean evaluateAverage(float averageValue)
    {
        switch (operator)
        {
            case "<":
                return Float.valueOf(requiredValue) < averageValue;
            case ">=":
                return Float.valueOf(requiredValue) >= averageValue;
            case ">":
                return Float.valueOf(requiredValue) > averageValue;
            case "<=":
                return Float.valueOf(requiredValue) <= averageValue;
            default:
                return false;
        }
    }

    private boolean compareEqual(Object value, String requiredValue)
    {
        if (value instanceof Integer)
        {
            return Integer.parseInt(requiredValue) == (Integer) value;
        }
        else if (value instanceof Float)
        {
            return Float.parseFloat(requiredValue) == (Float) value;
        }
        else if (value instanceof Date)
        {
            Date dateValue = (Date) value;
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            try
            {
                Date requiredDate = dateFormat.parse(requiredValue);
                return dateValue.equals(requiredDate);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else if (value instanceof String)
        {
            return requiredValue.equals(value);
        }
        return false;
    }

    private boolean compareLower(Object value, String requiredValue)
    {
        if (value instanceof Integer)
        {
            return Integer.parseInt(requiredValue) < (Integer) value;
        }
        else if (value instanceof Float)
        {
            return Float.parseFloat(requiredValue) < (Float) value;
        }
        else if (value instanceof Date)
        {
            Date dateValue = (Date) value;
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            try
            {
                Date requiredDate = dateFormat.parse(requiredValue);
                return dateValue.compareTo(requiredDate) < 0;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else if (value instanceof String)
        {
            return requiredValue.equals(value);
        }
        return false;
    }

    private boolean compareGreater(Object value, String requiredValue)
    {
        if (value instanceof Integer)
        {
            return Integer.parseInt(requiredValue) > (Integer) value;
        }
        else if (value instanceof Float)
        {
            return Float.parseFloat(requiredValue) > (Float) value;
        }
        else if (value instanceof Date)
        {
            Date dateValue = (Date) value;
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            try
            {
                Date requiredDate = dateFormat.parse(requiredValue);
                return dateValue.compareTo(requiredDate) > 0;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else if (value instanceof String)
        {
            return requiredValue.equals(value);
        }
        return false;
    }

    public Boolean getAvg() {
        return avg;
    }

    public void setAvg(Boolean avg) {
        this.avg = avg;
    }
}
