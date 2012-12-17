package com.ForgeEssentials.util.AreaSelector;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;

@SaveableObject(SaveInline = true)
public class WarpPoint extends WorldPoint
{
	@SaveableField
	public float pitch;
	@SaveableField
	public float yaw;

	public WarpPoint(int dimension, int x, int y, int z, float playerPitch, float playerYaw)
	{
		super(dimension, x, y, z);
		this.pitch = playerPitch;
		this.yaw = playerYaw;
	}
	
	public WarpPoint(Point p, int dimension, float playerPitch, float playerYaw)
	{
		this(dimension, p.x, p.y, p.z, playerPitch, playerYaw);
	}
	
	public WarpPoint(WorldPoint p, float playerPitch, float playerYaw)
	{
		this(p.dim, p.x, p.y, p.z, playerPitch, playerYaw);
	}
	
	@Reconstructor()
	private static WarpPoint reconstruct(TaggedClass tag)
	{
		int x = (Integer) tag.TaggedMembers.get("x").Value;
		int y = (Integer) tag.TaggedMembers.get("y").Value;
		int z = (Integer) tag.TaggedMembers.get("z").Value;
		int dim = (Integer) tag.TaggedMembers.get("dim").Value;
		float pitch = (Float) tag.TaggedMembers.get("pitch").Value;
		float yaw = (Float) tag.TaggedMembers.get("yaw").Value;
		return new WarpPoint(dim, x, y, z, pitch, yaw);
	}

}
