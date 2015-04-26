package com.forgeessentials.commands.util;


public class WeatherTimeData {
    
    public static final int dayTimeStart = 1;
    public static final int dayTimeEnd = 11;
    public static final int nightTimeStart = 14;
    public static final int nightTimeEnd = 22;

    public int dimID;

    public boolean weatherSpecified;

    public boolean rain;

    public boolean storm;

    public boolean timeSpecified;

    public boolean day;

    public boolean timeFreeze;

    public long freezeTime;

    public WeatherTimeData(int dimID)
    {
        this.dimID = dimID;
        this.weatherSpecified = false;
        this.timeSpecified = false;
        this.timeFreeze = false;
        this.storm = false;
        this.rain = false;
        this.day = true;
    }

}

