package com.ForgeEssentials.util.AreaSelector;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;

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
		int x = (Integer) tag.TaggedMembers.get("x").value;
		int y = (Integer) tag.TaggedMembers.get("y").value;
		int z = (Integer) tag.TaggedMembers.get("z").value;
		int dim = (Integer) tag.TaggedMembers.get("dim").value;
		float pitch = (Float) tag.TaggedMembers.get("pitch").value;
		float yaw = (Float) tag.TaggedMembers.get("yaw").value;
		return new WarpPoint(dim, x, y, z, pitch, yaw);
	}
	
	@UniqueLoadingKey()
	private String getLoadingField()
	{
		return "warppoint_"+dim+"_"+x+"_"+y+"_"+z+"_"+pitch+"_"+yaw;
	}

}
