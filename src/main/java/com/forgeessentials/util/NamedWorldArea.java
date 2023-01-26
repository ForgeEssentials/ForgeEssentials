package com.forgeessentials.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Keeps a WorldArea linked to a particular multiworld, even if the dim-id changes
 */
public class NamedWorldArea extends WorldArea
{

    protected RegistryKey<World> worldName;

    @Expose(serialize = false)
    protected boolean isLinked = false;

    @Expose(serialize = false)
    protected boolean isValid = true;

    public NamedWorldArea(RegistryKey<World> dimension, RegistryKey<World> registryKey, Point start, Point end)
    {
        super(dimension, start, end);
        this.worldName = registryKey;
        isLinked();
    }

    public NamedWorldArea(RegistryKey<World> registryKey, Point start, Point end)
    {
        this(0, registryKey, start, end);
    }

    public NamedWorldArea(String worldName, AreaBase area)
    {
        this(0, worldName, area.getLowPoint(), area.getHighPoint());
    }

    public NamedWorldArea(RegistryKey<World> dimension, Point start, Point end)
    {
        super(dimension, start, end);
        this.worldName = APIRegistry.namedWorldHandler.getWorldName(dimension);
        isLinked();
    }

    public NamedWorldArea(int dimension, AreaBase area)
    {
        this(dimension, area.getLowPoint(), area.getHighPoint());
    }

    public NamedWorldArea(WorldArea area)
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
