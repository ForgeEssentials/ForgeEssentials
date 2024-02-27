package com.forgeessentials.commons.selections;

import net.minecraft.world.level.Level;

public class WorldArea extends AreaBase
{

    protected String dim;

    public WorldArea(String world, Point start, Point end)
    {
        super(start, end);
        dim = world;
    }

    public WorldArea(String world, AreaBase area)
    {
        super(area.getHighPoint(), area.getLowPoint());
        dim = world;
    }

    public String getDimension()
    {
        return dim;
    }

    public void setDimension(Level dimensionId)
    {
        this.dim = dimensionId.dimension().location().toString();
    }

    @Override
    public WorldPoint getCenter()
    {
        return new WorldPoint(dim, (high.x + low.x) / 2, (high.y + low.y) / 2, (high.z + low.z) / 2);
    }

    public boolean contains(WorldPoint point)
    {
        if (point.dim.equals(dim))
        {
            return super.contains(point);
        }
        else
        {
            return false;
        }
    }

    public boolean contains(WorldArea area)
    {
        if (area.dim.equals(dim))
        {
            return super.contains(area);
        }
        else
        {
            return false;
        }
    }

    public boolean intersectsWith(WorldArea area)
    {
        if (area.dim.equals(dim))
        {
            return super.intersectsWith(area);
        }
        else
        {
            return false;
        }
    }

    public AreaBase getIntersection(WorldArea area)
    {
        if (area.dim.equals(dim))
        {
            return super.getIntersection(area);
        }
        else
        {
            return null;
        }
    }

    public boolean makesCuboidWith(WorldArea area)
    {
        if (area.dim.equals(dim))
        {
            return super.makesCuboidWith(area);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return " { " + dim + " , " + getHighPoint().toString() + " , " + getLowPoint().toString() + " }";
    }

}
