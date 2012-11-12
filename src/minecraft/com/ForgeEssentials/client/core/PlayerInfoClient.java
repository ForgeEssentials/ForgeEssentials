package com.ForgeEssentials.client.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.core.OutputHandler;

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
	public transient static File FECSAVES = new File(ProxyClient.FEDIRC, "saves/");

	private String worldName;

	// selection stuff
	private Point sel1;
	private Point sel2;
	private Selection selection;

	// home
	public Point home;

	public PlayerInfoClient(World world)
	{
		sel1 = null;
		sel2 = null;
		selection = null;
		worldName = world.getSaveHandler().getSaveDirectoryName();
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

	public void savePlayerInfo(EntityPlayer player)
	{
		try
		{
			String world = player.worldObj.getSaveHandler().getSaveDirectoryName();
			FileOutputStream fos = new FileOutputStream(FECSAVES + world + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.close();
			fos.close();
		} catch (Exception e)
		{
			OutputHandler.SOP("Error while saving info for player " + player.username + " in world " + player.worldObj.getSaveHandler().getSaveDirectoryName());
		}
	}
}
