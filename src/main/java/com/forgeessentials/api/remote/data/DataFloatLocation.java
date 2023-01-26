package com.forgeessentials.api.remote.data;

import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.WarpPoint;

/**
 *
 */
public class DataFloatLocation
{

    public RegistryKey<World> dim;

    public double x;

    public double y;

    public double z;

    public DataFloatLocation(RegistryKey<World> dim, double x, double y, double z)
    {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DataFloatLocation(Entity entity)
    {
        dim = entity.level.dimension();
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
