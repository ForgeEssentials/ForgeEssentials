package com.ForgeEssentials.AreaSelector;

import java.io.Serializable;
import java.util.HashMap;

import net.minecraft.src.EntityPlayer;

public class Point implements Serializable, Comparable<Point>
{	
	private static HashMap<String, Point> point1;
	private static HashMap<String, Point> point2;
	
	public static Point getPlayerPoint1(String username)
	{
		Point point = point1.get(username);
		
		if (point == null)
			point = new Point();
		
		return point;
	}
	
	// GET's 
	
	public static Point getPlayerPoint1(EntityPlayer player)
	{
		return getPlayerPoint1(player.username);
	}
	
	public static Point getPlayerPoint2(String username)
	{
		Point point = point2.get(username);
		
		if (point == null)
			point = new Point();
		
		return point;
	}
	
	public static Point getPlayerPoint2(EntityPlayer player)
	{
		return getPlayerPoint2(player.username);
	}
	
	// SET's 
	
	public static void setPlayerPoint1(String username, Point newPoint)
	{
		Point point = point1.get(username);
		
		if (point == null)
			point = newPoint;
		else
			point.update(newPoint);
		
		point1.put(username, point);
	}
	
	public static void setPlayerPoint1(EntityPlayer player, Point point)
	{
		setPlayerPoint1(player.username, point);
	}
	
	public static void setPlayerPoint2(String username, Point newPoint)
	{
		Point point = point2.get(username);
		
		if (point == null)
			point = newPoint;
		else
			point.update(newPoint);
		
		point1.put(username, point);
	}
	
	public static void setPlayerPoint2(EntityPlayer player, Point point)
	{
		setPlayerPoint2(player.username, point);
	}
	
	// --------------------------------
	//  -------  the actual class now -------
	// --------------------------------
	
	private int x;
	private int y;
	private int z;
	private boolean isValid;
	
	public Point()
	{
		this(0, 0, 0);
		isValid = false;
	}
	
	public Point(int x, int y, int z)
	{
		if (y < 0)
			throw new IllegalArgumentException("Y value cannot be negative!");
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		isValid = true;
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
			return y-point.y;
	}
	
	/**
	 * x = first value   y = second value   z = 3rd value.
	 * only puting 2 values sets x and y, but not z.
	 * @param values
	 */
	public void setValues(int... values)
	{
		switch(values.length)
		{
			case 3:
				z = values[2];
			case 2:
				if (values[1] < 0)
					throw new IllegalArgumentException("Y value cannot be negative!");
				y = values[1];
			case 1:
				x = values[0];
				break;
			default: throw new IllegalArgumentException("There are too many values!");
		}
		
		isValid = true;
	}
	
	public void update(Point point)
	{
		if (!point.isValid || point.equals(this) || point == this)
			return;
		
		x = point.x;
		y = point.y;
		z = point.z;
		isValid = true;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (
			object instanceof Point &&
			x == ((Point)object).x &&
			y == ((Point)object).y &&
			z == ((Point)object).z &&
			isValid == ((Point)object).isValid
			)
			return true;
		
		return false;
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	public void invalidate()
	{
		this.isValid = false;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

}
