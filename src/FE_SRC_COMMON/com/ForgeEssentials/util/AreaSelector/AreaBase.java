package com.ForgeEssentials.util.AreaSelector;

import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.SaveableObject.SaveableField;

@SaveableObject
public abstract class AreaBase
{
	// used for pretty much everything else.
	@SaveableField
	private Point high;
	@SaveableField
	private Point low;

	/**
	 * Points are inclusive.
	 * 
	 * @param start
	 * @param end
	 */
	public AreaBase(Point start, Point end)
	{
		Point[] points = getAlignedPoints(start, end);
		low = points[0];
		high = points[1];
	}

	public int getXLength()
	{
		return high.getX() - low.getX() + 1;
	}

	public int getYLength()
	{
		return high.getY() - low.getY() + 1;
	}

	public int getZLength()
	{
		return high.getZ() - low.getZ() + 1;
	}

	public Point getHighPoint()
	{
		return high;
	}

	public Point getLowPoint()
	{
		return low;
	}

	/**
	 * Orders the points so the start is smaller than the end.
	 */
	public static Point[] getAlignedPoints(Point p1, Point p2)
	{
		int diffx = p1.getX() - p2.getX();
		int diffy = p1.getY() - p2.getY();
		int diffz = p1.getZ() - p2.getZ();

		int newX1 = p2.getX();
		int newX2 = p1.getX();
		int newY1 = p2.getY();
		int newY2 = p1.getY();
		int newZ1 = p2.getZ();
		int newZ2 = p1.getZ();

		if (diffx < 0)
		{
			newX1 = p1.getX();
			newX2 = p2.getX();
		}

		if (diffy < 0)
		{
			newY1 = p1.getY();
			newY2 = p2.getY();
		}

		if (diffz < 0)
		{
			newZ1 = p1.getZ();
			newZ2 = p2.getZ();
		}
		return new Point[] { new Point(newX1, newY1, newZ1), new Point(newX2, newY2, newZ2) };
	}

	/**
	 * Determines if a given point is within the bounds of an area.
	 * 
	 * @param p
	 *            Point to check against the Area
	 * @return True, if the Point p is inside the area.
	 */
	public boolean contains(Point p)
	{
		return high.compareTo(p) >= 0 && low.compareTo(p) <= 0;
	}

	/**
	 * checks if this area contains with another
	 * 
	 * @param area
	 *            to check against this area
	 * @return True, AreaBAse area is completely within this area
	 */
	public boolean contains(AreaBase area)
	{
		if (this.contains(area.high) && this.contains(area.low))
		{
			return true;
		}
		return false;
	}

	/**
	 * checks if this area is overlapping with another
	 * 
	 * @param area
	 *            to check against this area
	 * @return True, if the given area overlaps with this one.
	 */
	public boolean intersectsWith(AreaBase area)
	{
		if (this.contains(area.high) || this.contains(area.low))
		{
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param area
	 *            The area to be checked.
	 * @return NULL if the areas to do not intersect. Argument if this area completely contains the argument.
	 */
	public AreaBase getIntersection(AreaBase area)
	{
		if (intersectsWith(area))
		{
			return null;
		}
		else if (this.contains(area))
		{
			return area;
		}
		else
		{
			// highest low-point.
			Point iLow = getAlignedPoints(low, area.low)[1];
			// lowest high-point
			Point iHigh = getAlignedPoints(high, area.high)[0];
			return new Selection(iLow, iHigh);
		}
	}

	public boolean makesCuboidWith(AreaBase area)
	{
		boolean alignX = low.x == area.low.x && high.x == area.high.x;
		boolean alignY = low.y == area.low.y && high.y == area.high.y;
		boolean alignZ = low.z == area.low.z && high.z == area.high.z;

		return alignX || alignY || alignZ;
	}

	/**
	 * 
	 * @param area
	 *            The area to be checked.
	 * @return NULL if the areas to do not make a cuboid together.
	 */
	public AreaBase getUnity(AreaBase area)
	{
		if (!makesCuboidWith(area))
		{
			return null;
		}
		else
		{
			// lowest low-point.
			Point iLow = getAlignedPoints(low, area.low)[0];
			// highest high-point.
			Point iHigh = getAlignedPoints(high, area.high)[1];
			return new Selection(iLow, iHigh);
		}
	}

	public void redefine(Point p1, Point p2)
	{
		Point[] points = getAlignedPoints(p1, p2);
		low = points[0];
		high = points[1];
	}

	public AreaBase copy()
	{
		return new Selection(low, high);
	}
}
