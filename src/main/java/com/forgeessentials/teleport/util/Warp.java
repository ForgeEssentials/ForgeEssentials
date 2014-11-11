package com.forgeessentials.teleport.util;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.selections.WarpPoint;

@SaveableObject
public class Warp {
    @UniqueLoadingKey
    @SaveableField
    private String name;

    @SaveableField
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

    @Reconstructor
    private static Warp reconstruct(IReconstructData tag)
    {
        return new Warp((String) tag.getFieldValue("name"), (WarpPoint) tag.getFieldValue("point"));
    }
}
