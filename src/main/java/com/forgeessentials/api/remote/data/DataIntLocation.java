package com.forgeessentials.api.remote.data;

import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.WorldPoint;

/**
 *
 */
public class DataIntLocation
{

    public RegistryKey<World> dim;

    public int x;

    public int y;

    public int z;

    public DataIntLocation(Entity entity)
    {
        dim = entity.level.dimension();
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
