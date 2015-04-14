package com.forgeessentials.teleport.util;

import com.forgeessentials.commons.selections.WarpPoint;

public class Warp {

    private String name;

    private WarpPoint point;

    public Warp(String name, WarpPoint point)
    {
        this.name = name;
        this.point = point;
    }

    public String getName()
    {
        return name;
    }

    public WarpPoint getPoint()
    {
        return point;
    }

}
