package com.forgeessentials.util.selections;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Almost exactly like a Point, except with an additional dimension member so we
 * can tell things apart. (So we can get back to The End or Nether using /back)
 *
 * @author MysteriousAges
 */
@SaveableObject(SaveInline = true)
public class WorldPoint extends Point {
    private static final long serialVersionUID = 5462406378573144189L;
    
    @SaveableField
    protected int dim;

    public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

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

    public WorldPoint(Entity player)
    {
        super(player);
        dim = player.dimension;
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

    @Reconstructor()
    public static WorldPoint reconstruct(IReconstructData tag)
    {
        int x = (Integer) tag.getFieldValue("x");
        int y = (Integer) tag.getFieldValue("y");
        int z = (Integer) tag.getFieldValue("z");
        int dim = (Integer) tag.getFieldValue("dim");
        return new WorldPoint(dim, x, y, z);
    }

    @UniqueLoadingKey()
    private String getLoadingField()
    {
        return "WorldPoint" + this;
    }

    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + ", dim=" + dim + "]";
    }

	private static final Pattern fromStringPattern = Pattern.compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*dim\\s*=\\s*(-?\\d+)\\s*\\]\\s*");

	public static WorldPoint fromString(String value)
	{
		Matcher m = fromStringPattern.matcher(value);
		if (m.matches())
		{
			try
			{
				return new WorldPoint(
					Integer.parseInt(m.group(4)), 
					Integer.parseInt(m.group(1)), 
					Integer.parseInt(m.group(2)), 
					Integer.parseInt(m.group(3)));
			}
			catch (NumberFormatException e)
			{
			}
		}
		return null;
	}

}
