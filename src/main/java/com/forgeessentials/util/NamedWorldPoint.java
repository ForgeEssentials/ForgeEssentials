package com.forgeessentials.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

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

    public NamedWorldPoint(String dimension, int x, int y, int z, String worldName)
    {
        super(dimension, x, y, z);
        this.worldName = worldName;
        isLinked();
    }

    public NamedWorldPoint(int x, int y, int z, String worldName)
    {
        this("minecraft:overworld", x, y, z, worldName);
    }

    public NamedWorldPoint(String dimension, int x, int y, int z)
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
                ServerLevel world = APIRegistry.namedWorldHandler.getWorld(worldName);
                if (world != null)
                {
                    this.dim = world.dimension().location().toString();
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
