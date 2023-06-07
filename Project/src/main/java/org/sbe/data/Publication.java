package org.sbe.data;

import java.util.Date;

public class Publication
{
    private int stationId;
    private String city;
    private int temp;
    private float rain;
    private int wind;
    private String direction;
    private Date date;

    public Publication(int stationId,
                       String city,
                       int temp,
                       float rain,
                       int wind,
                       String direction,
                       Date date)
    {
        this.setStationId(stationId);
        this.setCity(city);
        this.setTemp(temp);
        this.setRain(rain);
        this.setWind(wind);
        this.setDirection(direction);
        this.setDate(date);
    }

    public int getStationId()
    {
        return stationId;
    }

    public void setStationId(int stationId)
    {
        this.stationId = stationId;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public int getTemp()
    {
        return temp;
    }

    public void setTemp(int temp)
    {
        this.temp = temp;
    }

    public float getRain()
    {
        return rain;
    }

    public void setRain(float rain)
    {
        this.rain = rain;
    }

    public int getWind()
    {
        return wind;
    }

    public void setWind(int wind)
    {
        this.wind = wind;
    }

    public String getDirection()
    {
        return direction;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
