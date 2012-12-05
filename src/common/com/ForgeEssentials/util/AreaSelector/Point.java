package com.ForgeEssentials.util.AreaSelector;

import java.io.Serializable;

public class Point implements Serializable, Comparable<Point>
{
	public int x;
	public int y;
	public int z;

	public Point(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
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
			return (x - point.x) + (y - point.y) + (z - point.z);
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
	 * @return TRUE if the points have the same coordinate on atleast one axis.
	 */
	public boolean alignsWith(Point p)
	{
		return this.x == p.x || this.y == p.y || this.z == p.z;
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
}
