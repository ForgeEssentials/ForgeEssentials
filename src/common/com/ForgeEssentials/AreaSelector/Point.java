package com.ForgeEssentials.AreaSelector;

import java.io.Serializable;
import java.util.HashMap;

import net.minecraft.src.EntityPlayer;

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

	@Override
	/**
	 * decides whats bigger by the Y value only.
	 */
	public int compareTo(Point point)
	{
		if (y == point.y)
			return 0;
		else
			return y - point.y;
	}

	/**
	 * x = first value y = second value z = 3rd value. only puting 2 values sets x and y, but not z.
	 * 
	 * @param values
	 */
	public void setValues(int... values)
	{
		switch (values.length)
		{
			case 3:
				z = values[2];
			case 2:
				y = values[1];
			case 1:
				x = values[0];
				break;
			default:
				throw new IllegalArgumentException("There are too many values!");
		}
	}

	public void update(Point point)
	{
		if (point.equals(this) || point == this)
			return;

		x = point.x;
		y = point.y;
		z = point.z;
	}

	public void add(Point point)
	{
		x += point.x;
		y += point.y;
		z += point.z;
	}

	public void subtract(Point point)
	{
		x -= point.x;
		y -= point.y;
		z -= point.z;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Point && x == ((Point) object).x && y == ((Point) object).y && z == ((Point) object).z)
			return true;

		return false;
	}

	public void validate()
	{
		if (y < 0) {
			y = 0;
		}
	}
}
