package com.ForgeEssentials.util.AreaSelector;

import com.ForgeEssentials.api.data.ITaggedClass;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Almost exactly like a Point, except with an additional dimension member so we can tell things apart. (So we can get back to The End or Nether using /back)
 * 
 * @author MysteriousAges
 * 
 */
@SaveableObject(SaveInline = true)
public class WorldPoint extends Point
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3743470804433969687L;
	@SaveableField
	public int					dim;

	public WorldPoint(int dimension, int x, int y, int z)
	{
		super(x, y, z);
		dim = dimension;
	}

	public WorldPoint(World world, int x, int y, int z)
	{
		super(x, y, z);
		dim = world.getWorldInfo().getDimension();
	}

	public WorldPoint(Entity player)
	{
		super(player);
		dim = player.dimension;
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
		return new WorldPoint(p.dim, p.x, p.y, p.z);
	}

	@Reconstructor()
	public static WorldPoint reconstruct(ITaggedClass tag)
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
		return "WorldPoint[" + dim + ", " + x + ", " + y + ", " + z + "]";
	}
}
