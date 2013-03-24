package com.ForgeEssentials.client.util;

import java.io.Serializable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject(SaveInline = true)
public class ClientPoint implements Serializable, Comparable<ClientPoint>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3003472204175937036L;

	@SaveableField
	public double				x;

	@SaveableField
	public double				y;

	@SaveableField
	public double				z;

	public ClientPoint(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public ClientPoint(EntityPlayer player)
	{
		x = player.posX;
		y = player.posY;
		z = player.posZ;
	}

	public ClientPoint(ChunkCoordinates coords)
	{
		x = coords.posX;
		y = coords.posY;
		z = coords.posZ;
	}

	public int getX()
	{
		return (int) Math.floor(x);
	}

	public int getY()
	{
		return (int) Math.floor(y);
	}

	public int getZ()
	{
		return (int) Math.floor(z);
	}

	/**
	 * This is calculated by the whichever has higher coords.
	 * 
	 * @return Posotive number if this Point is larger. 0 if they are equal.
	 * Negative if the provided point is larger.
	 */
	@Override
	public int compareTo(ClientPoint point)
	{
		if (equals(point))
			return 0;

		int positives = 0;
		int negatives = 0;

		if (x > point.x)
		{
			positives++;
		}
		else
		{
			negatives++;
		}

		if (y > point.y)
		{
			positives++;
		}
		else
		{
			negatives++;
		}

		if (z > point.z)
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
			return (int) (x - point.x + (y - point.y) + (z - point.z));
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof ClientPoint && x == ((ClientPoint) object).x && y == ((ClientPoint) object).y && z == ((ClientPoint) object).z)
			return true;

		return false;
	}

	/**
	 * @param point
	 * @return The distance to a given Block.
	 */
	public double getDistanceTo(ClientPoint point)
	{
		return Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y) + (z - point.z) * (z - point.z));
	}

	/**
	 * 
	 * @param p
	 * @return TRUE if the points have the same coordinate on at least one axis.
	 */
	public boolean alignsWith(ClientPoint p)
	{
		return getX() == p.getX() || getY() == p.getY() || getZ() == p.getZ();
	}

	/**
	 * gets a new Point with the same data as the provided one.
	 * 
	 * @param point
	 * @return
	 */
	public ClientPoint copy(ClientPoint point)
	{
		return new ClientPoint(point.x, point.y, point.z);
	}

	/**
	 * ensures the Point is valid. Just floors the Y axis to 0. Y can't be
	 * negative.
	 */
	public static ClientPoint validate(ClientPoint point)
	{
		if (point.y < 0)
			return new ClientPoint(point.x, 0, point.z);
		else
			return point;
	}

	@Reconstructor()
	public static ClientPoint reconstruct(IReconstructData tag)
	{
		float x = (Float) tag.getFieldValue("x");
		float y = (Float) tag.getFieldValue("y");
		float z = (Float) tag.getFieldValue("z");
		return new ClientPoint(x, y, z);
	}

	@UniqueLoadingKey()
	private String getLoadingField()
	{
		return "point_" + x + "_" + y + "_" + z;
	}

	@Override
	public String toString()
	{
		return "[" + x + ";" + y + ";" + z + "]";
	}
}