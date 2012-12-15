package com.ForgeEssentials.util.AreaSelector;

public class Selection extends AreaBase
{
	// only really used for copying.. the points it was defined from.
	private Point	start;	// start selection
	private Point	end;	// end selection

	public Selection(Point point1, Point point2)
	{
		super(point1, point2);
		start = Point.validate(point1);
		end = Point.validate(point2);
	}

	public Point getStart()
	{
		return start;
	}
	
	public Point getEnd()
	{
		return end;
	}

	public void setStart(Point start)
	{
		this.start = start;
		this.redefine(this.start, end);
	}

	public void setEnd(Point end)
	{
		this.end = end;
		this.redefine(start, this.end);
	}
}
