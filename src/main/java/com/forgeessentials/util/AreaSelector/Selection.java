package com.forgeessentials.util.AreaSelector;

public class Selection extends AreaBase
{
	// only really used for copying.. the points it was defined from.
	private Point	start;	// start selection
	private Point	end;	// end selection

	public Selection(Point point1, Point point2)
	{
		super(point1, point2);
		start = point1;
		start.validate();
		end = point2;
		point2.validate();
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
		start.validate();
		redefine(this.start, end);
	}

	public void setEnd(Point end)
	{
		this.end = end;
		end.validate();
		redefine(start, this.end);
	}
}
