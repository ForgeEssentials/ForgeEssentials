package com.forgeessentials.multiworld;

import net.minecraft.world.WorldServer;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;

/**
 * Keeps a WorldArea linked to a particular multiworld, even if the dim-id changes
 * 
 * @author Olee
 */
public class MultiworldArea extends WorldArea {

    protected String worldName;

    @Expose(serialize = false)
    protected boolean isLinked = false;

    @Expose(serialize = false)
    protected boolean isValid = true;

    public MultiworldArea(int dimension, String worldName, Point start, Point end)
    {
        super(dimension, start, end);
        this.worldName = worldName;
        isLinked();
    }

    public MultiworldArea(String worldName, Point start, Point end)
    {
        this(0, worldName, start, end);
    }

    public MultiworldArea(String worldName, AreaBase area)
    {
        this(0, worldName, area.getLowPoint(), area.getHighPoint());
    }

    public MultiworldArea(int dimension, Point start, Point end)
    {
        super(dimension, start, end);
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

    public MultiworldArea(WorldArea area)
    {
        this(area.getDimension(), area.getLowPoint(), area.getHighPoint());
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

    @Override
    public boolean contains(WorldPoint point)
    {
        if (!isValid())
            return false;
        return super.contains(point);
    }

    @Override
    public boolean contains(WorldArea area)
    {
        if (!isValid())
            return false;
        return super.contains(area);
    }

}
