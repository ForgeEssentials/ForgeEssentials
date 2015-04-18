package com.forgeessentials.teleport.util;

import com.forgeessentials.commons.selections.WarpPoint;

public class PWarp {
    public static final String SEPERATOR = "#";

    private String name;

    private WarpPoint point;

    public PWarp(String username, String name, WarpPoint point)
    {
        this.name = username + SEPERATOR + name;
        this.point = point;
    }

    public String getFilename()
    {
        return name;
    }

    public String getUsername()
    {
        return name.split(SEPERATOR)[0];
    }

    public String getName()
    {
        return name.split(SEPERATOR)[1];
    }

    public WarpPoint getPoint()
    {
        return point;
    }

}
