package com.forgeessentials.api.remote.data;

import com.forgeessentials.commons.selections.WorldPoint;

import net.minecraft.world.entity.Entity;

/**
 *
 */
public class DataIntLocation
{

    public String dim;

    public int x;

    public int y;

    public int z;

    public DataIntLocation(Entity entity)
    {
        dim = entity.level.dimension().location().toString();
        x = (int) Math.floor(entity.position().x);
        y = (int) Math.floor(entity.position().y);
        z = (int) Math.floor(entity.position().z);
    }

    public DataIntLocation(WorldPoint point)
    {
        dim = point.getDimension();
        x = point.getX();
        y = point.getY();
        z = point.getZ();
    }

}
