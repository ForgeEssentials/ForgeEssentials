package com.forgeessentials.util.selections;

import com.forgeessentials.commons.AreaBase;
import com.forgeessentials.commons.Point;
import net.minecraft.world.World;

import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;

public class WorldArea extends AreaBase
{
	
    @SaveableField
    protected int dim;

    public WorldArea(World world, Point start, Point end)
    {
        super(start, end);
        dim = world.provider.dimensionId;
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
        dim = world.provider.dimensionId;
    }

    public int getDimension()
	{
		return dim;
	}

	public boolean contains(WorldPoint p)
    {
        if (p.dim == dim)
        {
            return super.contains(p);
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

    @UniqueLoadingKey()
    private String getLoadingField()
    {
        return "WorldArea" + this;
    }

    @Override
    public String toString()
    {
        return " { " + dim + " , " + getHighPoint().toString() + " , " + getLowPoint().toString() + " }";
    }

}
