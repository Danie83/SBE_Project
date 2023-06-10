package org.sbe.data;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private boolean evaluateConstraint(Publication publication)
    throws NoSuchFieldException, IllegalAccessException
    {
        Class<?> targetPublicationClass = publication.getClass();
        Field field = targetPublicationClass.getDeclaredField(factor);
        field.setAccessible(true);
        Object value = field.get(publication);

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
}
