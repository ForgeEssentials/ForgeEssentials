package com.ForgeEssentials.AreaSelector;

import java.io.Serializable;

public class Point implements Serializable, Comparable<Point>
{
	public final int x;
	public final int y;
	public final int z;

	public Point(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	/**
	 * This is calculated by the whichever has higher coords.
	 * @return Posotive number if this Point is larger. 0 if they are equal. Negative if the provided point is larger.
	 */
	@Override
	public int compareTo(Point point)
	{
		if (equals(point))
			return 0;
		
		int posotives = 0;
		int negatives = 0;
		
		if (x > point.x)
			posotives++;
		else
			negatives++;
		
		if (y > point.y)
			posotives++;
		else
			negatives++;
		
		if (z > point.z)
			posotives++;
		else
			negatives++;
		
		if (posotives > negatives)
			return +1;
		else if (negatives > posotives)
			return -1;
		else
		{
			return (x-point.x) + (y-point.y) + (z-point.z);
		}

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
		return Math.sqrt(
				((x-point.x)*(x-point.x)) +
				((y-point.y)*(y-point.y)) +
				((z-point.z)*(z-point.z))
				);
	}
	
	/**
	 * gets a new Point with the same data as the provided one.
	 * @param point
	 * @return
	 */
	public static Point copy(Point point)
	{
		return new Point(point.x, point.y, point.z);
	}
	
	/**
	 * ensures the Point is valid.
	 * Just floors the Y axis to 0. Y can't be negative.
	 */
	public static Point validate(Point point)
	{
		if (point.y < 0) {
			return new Point(point.x, 0, point.z);
		}
		else
			return point; 
	}
}
