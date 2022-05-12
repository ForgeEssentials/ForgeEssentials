package com.forgeessentials.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;

/**
 * Keeps a WorldPoint linked to a particular multiworld, even if the dim-id changes
 */
public class NamedWorldPoint extends WorldPoint
{

    protected String worldName;

    @Expose(serialize = false)
    protected boolean isLinked = false;

    @Expose(serialize = false)
    protected boolean isValid = true;

    public NamedWorldPoint(RegistryKey<World> dimension, String worldName, int x, int y, int z)
    {
        super(dimension, x, y, z);
        this.worldName = worldName;
        isLinked();
    }

    public NamedWorldPoint(String worldName, int x, int y, int z)
    {
        this(0, worldName, x, y, z);
    }

    public NamedWorldPoint(RegistryKey<World> dimension, int x, int y, int z)
    {
        super(dimension, x, y, z);
        this.worldName = APIRegistry.namedWorldHandler.getWorldName(dimension);
        isLinked();
    }

    public NamedWorldPoint(WorldPoint point)
    {
        this(point.getDimension(), point.getX(), point.getY(), point.getZ());
    }

    public NamedWorldPoint(Entity entity)
    {
        super(entity);
        this.worldName = APIRegistry.namedWorldHandler.getWorldName(dim);
        isLinked();
    }

    public boolean isLinked()
    {
        if (!isValid())
            return false;
        return isLinked;
    }

    public boolean isValid()
    {
        if (!isValid)
        {
            if (worldName != null)
            {
                // If there is a name for the dimension, use it
                ServerWorld world = APIRegistry.namedWorldHandler.getWorld(worldName);
                if (world != null)
                {
                    this.dim = world.dimension();
                    isLinked = true;
                    isValid = true;
                }
            }
            else
            {
                // If no name was set, just use dimID
                isLinked = false;
                isValid = true;
            }
        }
        return isValid;
    }

}
