package com.forgeessentials.commons.selections;

import net.minecraft.world.World;

public class WorldArea extends AreaBase
{
	
    protected int dim;

    public WorldArea(World world, Point start, Point end)
    {
        super(start, end);
        dim = world.provider.getDimensionId();
    }

    public WorldArea(int dim, Point start, Point end)
    {
        super(start, end);
        this.dim = dim;
    }

    public WorldArea(int dim, AreaBase area)
    {
        super(area.getHighPoint(), area.getLowPoint());
        this.dim = dim;
    }

    public WorldArea(World world, AreaBase area)
    {
        super(area.getHighPoint(), area.getLowPoint());
        dim = world.provider.getDimensionId();
    }

    public int getDimension()
	{
		return dim;
	}

    public void setDimension(int dimensionId)
    {
        this.dim = dimensionId;
    }

    @Override
    public WorldPoint getCenter()
    {
        return new WorldPoint(dim, (high.x + low.x) / 2, (high.y + low.y) / 2, (high.z + low.z) / 2);
    }

	public boolean contains(WorldPoint point)
    {
        if (point.dim == dim)
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
        if (area.dim == dim)
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
        if (area.dim == dim)
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
        if (area.dim == dim)
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
        if (area.dim == dim)
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
