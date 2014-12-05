package com.forgeessentials.multiworld;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;

import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;

/**
 * Keeps a WorldPoint linked to a particular multiworld, even if the dim-id changes
 * 
 * @author Olee
 */
public class MultiworldPoint extends WorldPoint {

    protected String worldName;

    @Expose(serialize = false)
    protected boolean isLinked = false;

    @Expose(serialize = false)
    protected boolean isValid = true;

    public MultiworldPoint(int dimension, String worldName, int x, int y, int z)
    {
        super(dimension, x, y, z);
        this.worldName = worldName;
        isLinked();
    }

    public MultiworldPoint(String worldName, int x, int y, int z)
    {
        this(0, worldName, x, y, z);
    }

    public MultiworldPoint(int dimension, int x, int y, int z)
    {
        super(0, x, y, z);
        Multiworld world = ModuleMultiworld.getMultiworldManager().getMultiworld(dim);
        if (world != null)
        {
            this.worldName = world.getName();
            isLinked();
        }
        else
        {
            this.worldName = null;
            this.isLinked = false;
            this.isValid = true;
        }
    }

    public MultiworldPoint(WorldPoint point)
    {
        this(point.getDimension(), point.getX(), point.getY(), point.getZ());
    }

    public MultiworldPoint(Entity entity)
    {
        super(entity);
        Multiworld world = ModuleMultiworld.getMultiworldManager().getMultiworld(dim);
        if (world != null)
            this.worldName = world.getName();
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
                WorldServer world = ModuleMultiworld.getMultiworldManager().getWorld(worldName);
                if (world != null)
                {
                    this.dim = world.provider.dimensionId;
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
