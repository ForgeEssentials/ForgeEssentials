package com.ForgeEssentials.client.util;

import java.io.Serializable;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.TaggedClass;

@SaveableObject(SaveInline = true)
public class Point implements Serializable, Comparable<Point>
{
	@SaveableField
	public double x;
	
	@SaveableField
	public double y;
	
	@SaveableField
	public double z;

	public Point(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point(EntityPlayer player) 
	{
		this.x = player.posX;
		this.y = player.posY;
		this.z = player.posZ;
	}

	public int getX()
	{
		return (int)Math.floor(x);
	}

	public int getY()
	{
		return (int)Math.floor(y);
	}

	public int getZ()
	{
		return (int)Math.floor(z);
	}

	/**
	 * This is calculated by the whichever has higher coords.
	 * 
	 * @return Posotive number if this Point is larger. 0 if they are equal. Negative if the provided point is larger.
	 */
	@Override
	public int compareTo(Point point)
	{
		if (equals(point))
			return 0;

		int positives = 0;
		int negatives = 0;

		if (x > point.x)
			positives++;
		else
			negatives++;

		if (y > point.y)
			positives++;
		else
			negatives++;

		if (z > point.z)
			positives++;
		else
			negatives++;

		if (positives > negatives)
			return +1;
		else if (negatives > positives)
			return -1;
		else
			return (int)((x - point.x) + (y - point.y) + (z - point.z));
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Point && x == ((Point) object).x && y == ((Point) object).y && z == ((Point) object).z)
			return true;

		return false;
	}

	/**
	 * @param point
	 * @return The distance to a given Block.
	 */
	public double getDistanceTo(Point point)
	{
		return Math.sqrt(((x - point.x) * (x - point.x)) + ((y - point.y) * (y - point.y)) + ((z - point.z) * (z - point.z)));
	}

	/**
	 * 
	 * @param p
	 * @return TRUE if the points have the same coordinate on at least one axis.
	 */
	public boolean alignsWith(Point p)
	{
		return this.getX() == p.getX() || this.getY() == p.getY() || this.getZ() == p.getZ();
	}

	/**
	 * gets a new Point with the same data as the provided one.
	 * 
	 * @param point
	 * @return
	 */
	public Point copy(Point point)
	{
		return new Point(point.x, point.y, point.z);
	}

	/**
	 * ensures the Point is valid. Just floors the Y axis to 0. Y can't be negative.
	 */
	public static Point validate(Point point)
	{
		if (point.y < 0)
		{
			return new Point(point.x, 0, point.z);
		} else
			return point;
	}
	
	@Reconstructor()
	public static Point reconstruct(TaggedClass tag)
	{
		float x = (Float)tag.getFieldValue("x");
		float y = (Float)tag.getFieldValue("y");
		float z = (Float)tag.getFieldValue("z");
		return new Point(x, y, z);
	}
	
	@UniqueLoadingKey()
	private String getLoadingField()
	{
		return "point_"+x+"_"+y+"_"+z;
	}
	
	@Override
	public String toString()
	{
		return "[" + x + ";" + y + ";" + z + "]";
	}
}
