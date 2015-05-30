package com.forgeessentials.api.remote.data;

import net.minecraft.entity.Entity;

import com.forgeessentials.commons.selections.WorldPoint;

/**
 *
 */
public class DataIntLocation
{

    public int dim;

    public int x;

    public int y;

    public int z;

    public DataIntLocation(Entity entity)
    {
        dim = entity.dimension;
        x = (int) Math.floor(entity.posX);
        y = (int) Math.floor(entity.posY);
        z = (int) Math.floor(entity.posZ);
    }

    public DataIntLocation(WorldPoint point)
    {
        dim = point.getDimension();
        x = point.getX();
        y = point.getY();
        z = point.getZ();
    }

}
