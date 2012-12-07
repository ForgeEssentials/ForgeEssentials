package com.ForgeEssentials.util.AreaSelector;

public class WarpPoint extends WorldPoint
{
	
	public float pitch;
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

}
