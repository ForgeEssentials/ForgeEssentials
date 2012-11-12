package com.ForgeEssentials.AreaSelector;

public class Selection extends AreaBase
{
	public Selection(Point point1, Point point2)
	{
		super(point1, point2);
	}

	public Point set(Point start, int blockID, int metadata)
	{
		return start;
	}
}
