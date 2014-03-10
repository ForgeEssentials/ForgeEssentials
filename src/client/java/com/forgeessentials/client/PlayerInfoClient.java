package com.forgeessentials.client;

import java.util.HashMap;

import com.forgeessentials.client.util.ClientPoint;
import com.forgeessentials.client.util.ClientSelection;

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
	public boolean		playerLogger	= false;

	// selection stuff
	private ClientPoint		sel1;
	private ClientPoint		sel2;
	private ClientSelection	selection;

	/*
	 * Int => Type of change. (color in cui)
	 * 0 = place
	 * 1 = break
	 * 2 = interact
	 */
    public HashMap<ClientPoint, Integer>  rbList = new HashMap<ClientPoint, Integer>();

	public PlayerInfoClient()
	{
		sel1 = null;
		sel2 = null;
		selection = null;
	}

	public ClientPoint getPoint1()
	{
		return sel1;
	}

	public void setPoint1(ClientPoint sel1)
	{
		this.sel1 = sel1;

		if (sel1 != null)
			if (selection == null)
			{
				if (sel1 != null && sel2 != null)
				{
					selection = new ClientSelection(sel1, sel2);
				}
			}
			else
			{
				selection.setStart(sel1);
			}
	}

	public ClientPoint getPoint2()
	{
		return sel2;
	}

	public void setPoint2(ClientPoint sel2)
	{
		this.sel2 = sel2;

		if (sel2 != null)
			if (selection == null)
			{
				if (sel1 != null && sel2 != null)
				{
					selection = new ClientSelection(sel1, sel2);
				}
			}
			else
			{
				selection.setEnd(sel2);
			}
	}

	public ClientSelection getSelection()
	{
		return selection;
	}

	public void clearSelection()
	{
		selection = null;
		sel1 = null;
		sel2 = null;
	}
}
