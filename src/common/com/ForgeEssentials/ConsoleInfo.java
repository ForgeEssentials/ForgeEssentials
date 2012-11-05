package com.ForgeEssentials;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

public class ConsoleInfo
{
	public static ConsoleInfo instance;
	
	private Point sel1;
	private Point sel2;
	private Selection selection;
	
	public ConsoleInfo()
	{
		sel1 = new Point(0,0,0);
		sel2 = new Point(0,0,0);
		selection = new Selection(sel1, sel2);
	}

	public Point getPoint1()
	{
		return sel1;
	}

	public void setPoint1(Point sel1)
	{
		this.sel1 = sel1;
		selection.start = sel1;
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;
		selection.end = sel2;
	}
	
	public Selection getSelection()
	{
		return selection;
	}
}
