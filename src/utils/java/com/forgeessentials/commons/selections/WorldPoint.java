package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Point which stores dimension as well
 */
public class WorldPoint extends Point {

    protected int dim;

    public WorldPoint(int dimension, int x, int y, int z)
    {
        super(x, y, z);
        dim = dimension;
    }

    public WorldPoint(World world, int x, int y, int z)
    {
        super(x, y, z);
        dim = world.provider.dimensionId;
    }

    public WorldPoint(Entity entity)
    {
        super(entity);
        dim = entity.dimension;
    }

    public WorldPoint(int dim, Vec3 vector)
    {
        super(vector);
        this.dim = dim;
    }

    public int getDimension()
    {
        return dim;
    }

    public void setDimension(int dim)
    {
        this.dim = dim;
    }

    public int compareTo(WorldPoint p)
    {
        int diff = dim - p.dim;

        if (diff == 0)
        {
            diff = super.compareTo(p);
        }
        return diff;
    }

    public boolean equals(WorldPoint p)
    {
        return dim == p.dim && super.equals(p);
    }

    public WorldPoint copy(WorldPoint p)
    {
        return new WorldPoint(p.dim, p.getX(), p.getY(), p.getZ());
    }

    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + ", dim=" + dim + "]";
    }

    private static final Pattern fromStringPattern = Pattern
            .compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*dim\\s*=\\s*(-?\\d+)\\s*\\]\\s*");

    public static WorldPoint fromString(String value)
    {
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WorldPoint(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
            }
            catch (NumberFormatException e)
            {
                /* do nothing */
            }
        }
        return null;
    }

    public WarpPoint toWarpPoint(float pitch, float yaw)
    {
        return new WarpPoint(dim, x + 0.5, y, z + 0.5, pitch, yaw);
    }

}
