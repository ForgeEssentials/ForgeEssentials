package com.ForgeEssentials.util.AreaSelector;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.TaggedClass;

@SaveableObject(SaveInline = true)
public class WarpPoint extends WorldPoint
{
	@SaveableField
	public float pitch;
	@SaveableField
	public float yaw;

	public WarpPoint(int dimension, double x, double y, double z, float playerPitch, float playerYaw)
	{
		super(dimension, x, y, z);
		pitch = playerPitch;
		yaw = playerYaw;
	}

	public WarpPoint(Point p, int dimension, float playerPitch, float playerYaw)
	{
		this(dimension, p.x, p.y, p.z, playerPitch, playerYaw);
	}

	public WarpPoint(WorldPoint p, float playerPitch, float playerYaw)
	{
		this(p.dim, p.x, p.y, p.z, playerPitch, playerYaw);
	}

	public WarpPoint(EntityPlayer sender)
	{
		super(sender);
		pitch = sender.rotationPitch;
		yaw = sender.rotationYaw;
	}

	@Reconstructor()
	public static WarpPoint reconstruct(TaggedClass tag)
	{
		double x = (Double) tag.getFieldValue("x");
		double y = (Double) tag.getFieldValue("y");
		double z = (Double) tag.getFieldValue("z");
		int dim = (Integer) tag.getFieldValue("dim");
		float pitch = (Float) tag.getFieldValue("pitch");
		float yaw = (Float) tag.getFieldValue("yaw");
		return new WarpPoint(dim, x, y, z, pitch, yaw);
	}
	
	@UniqueLoadingKey()
	private String getLoadingField()
	{
		return "WarpPoint"+this;
	}
	
	@Override
	public String toString()
	{
		return "[" + dim + ";" + x + ";" + y + ";" + z + ";" + pitch + ";" + yaw + "]";
	}

}
