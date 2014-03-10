package com.forgeessentials.client.util;

import com.forgeessentials.client.util.SaveableObject.SaveableField;


@SaveableObject
public abstract class ClientAreaBase
{
	// used for pretty much everything else.
	@SaveableField
	private ClientPoint	high;
	@SaveableField
	private ClientPoint	low;

	/**
	 * Points are inclusive.
	 * 
	 * @param start
	 * @param end
	 */
	public ClientAreaBase(ClientPoint start, ClientPoint end)
	{
		ClientPoint[] points = getAlignedPoints(start, end);
		low = points[0];
		high = points[1];
	}

	public int getXLength()
	{
		return (int) (high.x - low.x + 1);
	}

	public int getYLength()
	{
		return (int) (high.y - low.y + 1);
	}

	public int getZLength()
	{
		return (int) (high.z - low.z + 1);
	}

	public ClientPoint getHighPoint()
	{
		return high;
	}

	public ClientPoint getLowPoint()
	{
		return low;
	}

	/**
	 * Orders the points so the start is smaller than the end.
	 */
	public static ClientPoint[] getAlignedPoints(ClientPoint p1, ClientPoint p2)
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
		return new ClientPoint[]
		{ new ClientPoint(newX1, newY1, newZ1), new ClientPoint(newX2, newY2, newZ2) };
	}

	/**
	 * Determines if a given point is within the bounds of an area.
	 * 
	 * @param p
	 * Point to check against the Area
	 * @return True, if the Point p is inside the area.
	 */
	public boolean contains(ClientPoint p)
	{
		return high.compareTo(p) >= 0 && low.compareTo(p) <= 0;
	}

	/**
	 * checks if this area contains with another
	 * 
	 * @param area
	 * to check against this area
	 * @return True, AreaBAse area is completely within this area
	 */
	public boolean contains(ClientAreaBase area)
	{
		if (this.contains(area.high) && this.contains(area.low))
			return true;
		return false;
	}

	/**
	 * checks if this area is overlapping with another
	 * 
	 * @param area
	 * to check against this area
	 * @return True, if the given area overlaps with this one.
	 */
	public boolean intersectsWith(ClientAreaBase area)
	{
		if (this.contains(area.high) || this.contains(area.low))
			return true;
		return false;
	}

	/**
	 * 
	 * @param area
	 * The area to be checked.
	 * @return NULL if the areas to do not intersect. Argument if this area
	 * completely contains the argument.
	 */
	public ClientAreaBase getIntersection(ClientAreaBase area)
	{
		if (intersectsWith(area))
			return null;
		else if (this.contains(area))
			return area;
		else
		{
			// highest low-point.
			ClientPoint iLow = getAlignedPoints(low, area.low)[1];
			// lowest high-point
			ClientPoint iHigh = getAlignedPoints(high, area.high)[0];
			return new ClientSelection(iLow, iHigh);
		}
	}

	public boolean makesCuboidWith(ClientAreaBase area)
	{
		boolean alignX = low.x == area.low.x && high.x == area.high.x;
		boolean alignY = low.y == area.low.y && high.y == area.high.y;
		boolean alignZ = low.z == area.low.z && high.z == area.high.z;

		return alignX || alignY || alignZ;
	}

	/**
	 * 
	 * @param area
	 * The area to be checked.
	 * @return NULL if the areas to do not make a cuboid together.
	 */
	public ClientAreaBase getUnity(ClientAreaBase area)
	{
		if (!makesCuboidWith(area))
			return null;
		else
		{
			// lowest low-point.
			ClientPoint iLow = getAlignedPoints(low, area.low)[0];
			// highest high-point.
			ClientPoint iHigh = getAlignedPoints(high, area.high)[1];
			return new ClientSelection(iLow, iHigh);
		}
	}

	public void redefine(ClientPoint p1, ClientPoint p2)
	{
		ClientPoint[] points = getAlignedPoints(p1, p2);
		low = points[0];
		high = points[1];
	}

	public ClientAreaBase copy()
	{
		return new ClientSelection(low, high);
	}
}