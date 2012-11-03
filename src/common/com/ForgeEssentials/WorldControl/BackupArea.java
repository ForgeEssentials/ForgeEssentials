package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * @author UnknownCoder : Max Bruce Defines an area to be backed up including before/after snapshots Area for undo and redo commands not saveable
 */
public class BackupArea
{
	private ArrayList<BlueprintBlock> before = new ArrayList<BlueprintBlock>();
	private ArrayList<BlueprintBlock> after = new ArrayList<BlueprintBlock>();
	public boolean hasUndone = false;

	// implicit constructor

	public void addBlockBefore(int x, int y, int z, int blockID, int metadata, TileEntity te)
	{
		before.add(new BlueprintBlock(x, y, z, blockID, metadata, te));
	}

	public void addBlockAfter(int x, int y, int z, int blockID, int metadata, TileEntity te)
	{
		after.add(new BlueprintBlock(x, y, z, blockID, metadata, te));
	}

	public void loadAreaBefore(World worldObj)
	{
		for (int i = 0; i < after.size(); i++)
			after.get(i).setInWorld(worldObj);
		hasUndone = true;
	}

	public void loadAreaAfter(World worldObj)
	{
		for (int i = 0; i < after.size(); i++)
			after.get(i).setInWorld(worldObj);

		hasUndone = false;
	}
}
