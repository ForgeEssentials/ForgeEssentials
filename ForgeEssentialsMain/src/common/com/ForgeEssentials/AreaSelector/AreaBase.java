package com.ForgeEssentials.AreaSelector;

public class AreaBase
{
	// only really used for copying.. the points it was defined from.
	public Point start; // start selection
	public Point end; // end selection
	
	// used for pretty much everything else.
	private Point high;
	private Point low;

	/**
	 * Points are inclusive.
	 * @param start
	 * @param end
	 */
	public AreaBase(Point start, Point end)
	{
		this.start = Point.copy(start);
		this.end = Point.copy(end);
		this.start.validate();
		this.end.validate();
		
		Point[] points = getAlignedPoints(start, end);
		low = points[0];
		high = points[1];
	}

	public int[] getDimensions()
	{
		int[] array = new int[3];
		array[0] = Math.abs(start.x - end.x);
		array[1] = Math.abs(start.y - end.z);
		array[2] = Math.abs(start.z - end.z);
		return array;
	}

	public int getXLength()
	{
		return Math.abs(end.x - start.x) + 1;
	}

	public int getZLength()
	{
		return Math.abs(end.z - start.z) + 1;
	}

	public int getYLength()
	{
		return Math.abs(end.y - start.y) + 1;
	}
	
	public Point getHighPoint()
	{
		return high;
	}
	
	public Point getLowPoint()
	{
		return low;
	}
	
	public Point getStart()
	{
		return start;
	}

	public void setStart(Point start)
	{
		this.start = start;
		Point[] points = getAlignedPoints(start, end);
		low = points[0];
		high = points[1];
	}

	public Point getEnd()
	{
		return end;
	}

	public void setEnd(Point end)
	{
		this.end = end;
		Point[] points = getAlignedPoints(start, end);
		low = points[0];
		high = points[1];
	}

	/**
	 * Orders the points so the start is smaller than the end.
	 */
	public static Point[] getAlignedPoints(Point p1, Point p2)
	{
		int diffx = p1.x - p2.x;
		int diffy = p1.y - p2.y;
		int diffz = p1.z - p2.z;
		
		int newX1 = p2.x;
		int newX2 = p1.x;
		int newY1 = p2.y;
		int newY2 = p1.y;
		int newZ1 = p2.z;
		int newZ2 = p1.z;
		
		if (diffx < 0)
		{
			newX1 = p1.x;
			newX2 = p2.x;
		}
		
		if (diffy < 0)
		{
			newY1 = p1.y;
			newY2 = p2.y;
		}
		
		if (diffx < 0)
		{
			newZ1 = p1.z;
			newZ2 = p2.z;
		}
		return new Point[] {new Point(newX1, newY1, newZ1), new Point(newX2, newY2, newZ2)};
	}

	/**
	 * Determines if a given point is within the bounds of an area.
	 * @param p Point to check against the Area
	 * @return True, if the Point p is inside the area.
	 */
	public boolean contains(Point p)
	{
		return high.compareTo(p) >= 0 && low.compareTo(p) <= 0;
	}
	
	/**
	 * checks if this area contains with another
	 * @param area to check against this area
	 * @return True, AreaBAse area is completely within this area
	 */
	public boolean contains(AreaBase area)
	{
		if (this.contains(area.high) && this.contains(area.low))
			return true;
		return false;
	}
	
	/**
	 * checks if this area is overlapping with another
	 * @param area to check against this area
	 * @return True, if the given area overlaps with this one.
	 */
	public boolean overlaps(AreaBase area)
	{
		if (this.contains(area.high) || this.contains(area.low))
			return true;
		return false;
	}
}
