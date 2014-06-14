package com.forgeessentials.commands.util;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class WeatherTimeData {
    public static final int dayTimeStart = 1;
    public static final int dayTimeEnd = 11;
    public static final int nightTimeStart = 14;
    public static final int nightTimeEnd = 22;

    @UniqueLoadingKey
    @SaveableField
    public int dimID;

    @SaveableField
    public boolean weatherSpecified;

    @SaveableField
    public boolean rain;

    @SaveableField
    public boolean storm;

    @SaveableField
    public boolean timeSpecified;

    @SaveableField
    public boolean day;

    @SaveableField
    public boolean timeFreeze;

    @SaveableField
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

    @Reconstructor
    private static WeatherTimeData reconstruct(IReconstructData tag)
    {
        WeatherTimeData data = new WeatherTimeData(Integer.parseInt(tag.getUniqueKey()));

        data.weatherSpecified = tag.getFieldValue("weatherSpecified").toString().equals(true);
        data.rain = tag.getFieldValue("rain").toString().equals(true);
        data.storm = tag.getFieldValue("storm").toString().equals(true);
        data.timeSpecified = tag.getFieldValue("timeSpecified").toString().equals(true);
        data.day = tag.getFieldValue("day").toString().equals(true);
        data.timeFreeze = tag.getFieldValue("timeFreeze").toString().equals(true);

        return data;
    }
}

