package com.ForgeEssentials.api.AreaSelector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject(SaveInline = true)
public class WarpPoint extends WorldPoint
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1534182424702360150L;

	@SaveableField
	public float				pitch;

	@SaveableField
	public float				yaw;

	// stops the coords from Point to be saved.
	@SaveableField(overrideParent = "x")
	public double				xd;

	// stops the coords from Point to be saved.
	@SaveableField(overrideParent = "y")
	public double				yd;

	// stops the coords from Point to be saved.
	@SaveableField(overrideParent = "z")
	public double				zd;

	public WarpPoint(int dimension, double x, double y, double z, float playerPitch, float playerYaw)
	{
		super(dimension, (int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
		xd = x;
		yd = y;
		zd = z;
		pitch = playerPitch;
		yaw = playerYaw;
	}

	public WarpPoint(Point p, int dimension, float playerPitch, float playerYaw)
	{
		this(dimension, p.x, p.y, p.z, playerPitch, playerYaw);
		xd = x;
		yd = y;
		zd = z;
	}

	public WarpPoint(WorldPoint p, float playerPitch, float playerYaw)
	{
		this(p.dim, p.x, p.y, p.z, playerPitch, playerYaw);
		xd = x;
		yd = y;
		zd = z;
	}

	public WarpPoint(EntityPlayer sender)
	{
		super(sender);
		xd = sender.posX;
		yd = sender.posY;
		zd = sender.posZ;
		pitch = sender.rotationPitch;
		yaw = sender.rotationYaw;
	}

	/**
	 * This is calculated by the whichever has higher coords.
	 * @return Posotive number if this Point is larger. 0 if they are equal.
	 * Negative if the provided point is larger.
	 */
	public int compareTo(WarpPoint point)
	{
		if (equals(point))
			return 0;

		int positives = 0;
		int negatives = 0;

		if (xd > point.xd)
		{
			positives++;
		}
		else
		{
			negatives++;
		}

		if (yd > point.yd)
		{
			positives++;
		}
		else
		{
			negatives++;
		}

		if (zd > point.zd)
		{
			positives++;
		}
		else
		{
			negatives++;
		}

		if (positives > negatives)
			return +1;
		else if (negatives > positives)
			return -1;
		else
			return (int) (xd - point.xd + (yd - point.yd) + (zd - point.zd));
	}

	/**
	 * gets a new Point with the same data as the provided one.
	 * @param point
	 * @return
	 */
	public static WarpPoint copy(WarpPoint point)
	{
		return new WarpPoint(point.dim, point.xd, point.yd, point.zd, point.pitch, point.yaw);
	}

	/**
	 * ensures the Point is valid. Just floors the Y axis to 0. Y can't be
	 * negative.
	 */
	@Override
	public void validate()
	{
		if (yd < 0)
		{
			yd = 0;
		}

		super.validate();
	}

	/**
	 * @param point
	 * @return The distance to a given Block.
	 */
	public double getDistanceTo(WarpPoint point)
	{
		return Math.sqrt((xd - point.xd) * (xd - point.xd) + (yd - point.yd) * (yd - point.yd) + (zd - point.zd) * (zd - point.zd));
	}

	/**
	 * @param point
	 * @return The distance to a given Block.
	 */
	public double getDistanceTo(Entity e)
	{
		return Math.sqrt((xd - e.posX) * (xd - e.posX) + (yd - e.posY) * (yd - e.posY) + (zd - e.posZ) * (zd - e.posZ));
	}

	@Reconstructor()
	public static WarpPoint reconstruct(IReconstructData tag)
	{
		double x = (Double) tag.getFieldValue("xd");
		double y = (Double) tag.getFieldValue("yd");
		double z = (Double) tag.getFieldValue("zd");
		int dim = (Integer) tag.getFieldValue("dim");
		float pitch = (Float) tag.getFieldValue("pitch");
		float yaw = (Float) tag.getFieldValue("yaw");
		return new WarpPoint(dim, x, y, z, pitch, yaw);
	}

	@UniqueLoadingKey()
	private String getLoadingField()
	{
		return "WarpPoint" + this;
	}

	@Override
	public String toString()
	{
		return "WarpPoint[" + dim + "," + xd + "," + yd + "," + zd + "," + pitch + "," + yaw + "]";
	}

}
