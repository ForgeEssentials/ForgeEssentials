package com.ForgeEssentials.client.core;

import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Clone of the PlayerInfo for the client only.
 * 
 * @author AbrarSyed
 */
@SideOnly(value = Side.CLIENT)
public class PlayerInfoClient
{
	// selection stuff
	private Point sel1;
	private Point sel2;
	private Selection selection;

	public PlayerInfoClient()
	{
		sel1 = null;
		sel2 = null;
		selection = null;
	}

	public Point getPoint1()
	{
		return sel1;
	}

	public void setPoint1(Point sel1)
	{
		this.sel1 = sel1;

		if (sel1 != null)
		{
			if (selection == null)
			{
				if (sel1 != null && sel2 != null)
					selection = new Selection(sel1, sel2);
			} else
				selection.setStart(sel1);
		}
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;

		if (sel2 != null)
		{
			if (selection == null)
			{
				if (sel1 != null && sel2 != null)
					selection = new Selection(sel1, sel2);
			} else
				selection.setEnd(sel2);
		}
	}

	public Selection getSelection()
	{
		return selection;
	}
}
