package com.ForgeEssentials.util.AreaSelector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.TaggedClass;

/**
 * Almost exactly like a Point, except with an additional dimension member so we can tell things apart. (So we can get back to The End or Nether using /back)
 * 
 * @author MysteriousAges
 * 
 */
@SaveableObject(SaveInline = true)
public class WorldPoint extends Point
{
	@SaveableField
	public int dim;

	public WorldPoint(int dimension, double x, double y, double z)
	{
		super(x, y, z);
		dim = dimension;
	}

	public WorldPoint(World world, double x, double y, double z)
	{
		super(x, y, z);
		dim = world.getWorldInfo().getDimension();
	}

	public WorldPoint(EntityPlayer player)
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

	@Override
	public int compareTo(Point p)
	{
		return super.compareTo(p);
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
	public static WorldPoint reconstruct(TaggedClass tag)
	{
		float x = (Float) tag.getFieldValue("x");
		float y = (Float) tag.getFieldValue("y");
		float z = (Float) tag.getFieldValue("z");
		int dim = (Integer) tag.getFieldValue("dim");
		return new WorldPoint(dim, x, y, z);
	}

	@UniqueLoadingKey()
	private String getLoadingField()
	{
		return "worldpoint_" + dim + "_" + x + "_" + y + "_" + z;
	}

	@Override
	public String toString()
	{
		return "[" + dim + ";" + x + ";" + y + ";" + z + "]";
	}
}
