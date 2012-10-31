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
	 * decides whats bigger on all values (x ,y z)  perhaps whichever has more negatives or something.
	 */
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
	
	public double getDistanceTo(Point point)
	{
		return Math.sqrt(
				((x-point.x)*(x-point.x)) +
				((y-point.y)*(y-point.y)) +
				((z-point.z)*(z-point.z))
				);
	}
	
	public static Point copy(Point point)
	{
		return new Point(point.x, point.y, point.z);
	}
}
