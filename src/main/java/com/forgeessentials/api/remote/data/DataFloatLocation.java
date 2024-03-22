package com.forgeessentials.api.remote.data;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.world.entity.Entity;

/**
 *
 */
public class DataFloatLocation
{

    public String dim;

    public double x;

    public double y;

    public double z;

    public DataFloatLocation(String dim, double x, double y, double z)
    {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DataFloatLocation(Entity entity)
    {
        dim = entity.level.dimension().location().toString();
        x = entity.position().x;
        y = entity.position().y;
        z = entity.position().z;
    }

    public DataFloatLocation(WarpPoint point)
    {
        dim = point.getDimension();
        x = point.getX();
        y = point.getY();
        z = point.getZ();
    }

}
