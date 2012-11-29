package com.ForgeEssentials.client.core;

import java.io.Serializable;

import com.ForgeEssentials.core.AreaSelector.Point;
import com.ForgeEssentials.core.AreaSelector.Selection;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * Clone of the PlayerInfo for the client only.
 * 
 * @author AbrarSyed
 */
@SideOnly(value = Side.CLIENT)
public class PlayerInfoClient implements Serializable
{
	// selection stuff
	private Point sel1;
	private Point sel2;
	private Selection selection;

	// home
	public Point home;

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

		if (selection == null)
		{
			if (sel1 != null && sel2 != null)
				selection = new Selection(sel1, sel2);
		} else
			selection.setStart(sel1);
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;

		if (selection == null)
		{
			if (sel1 != null && sel2 != null)
				selection = new Selection(sel1, sel2);
		} else
			selection.setEnd(sel2);
	}

	public Selection getSelection()
	{
		return selection;
	}
}
