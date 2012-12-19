package com.ForgeEssentials.util.AreaSelector;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;

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
	@SaveableField
	public int dim;

	public WorldPoint(int dimension, int x, int y, int z)
	{
		super(x, y, z);
		this.dim = dimension;
	}
	
	public WorldPoint(World world, int x, int y, int z)
	{
		super(x, y, z);
		this.dim = world.getWorldInfo().getDimension();
	}

	public int compareTo(WorldPoint p)
	{
		int diff = this.dim - p.dim;

		if (diff == 0)
		{
			diff = super.compareTo(p);
		}
		return diff;
	}

	public int compareTo(Point p)
	{
		return super.compareTo(p);
	}

	public boolean equals(WorldPoint p)
	{
		return this.dim == p.dim && super.equals(p);
	}

	public WorldPoint copy(WorldPoint p)
	{
		return new WorldPoint(p.dim, p.x, p.y, p.z);
	}
	
	@Reconstructor()
	private static WorldPoint reconstruct(TaggedClass tag)
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
		return "worldpoint_"+dim+"_"+x+"_"+y+"_"+z;
	}
}
