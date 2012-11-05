package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;

import com.ForgeEssentials.OutputHandler;
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
	
	public void addBlockBefore(BlueprintBlock block)
	{
		before.add(block);
	}

	public void addBlockAfter(BlueprintBlock block)
	{
		after.add(block);
	}

	public void loadAreaBefore(World worldObj)
	{
		for (int i = 0; i < after.size(); i++)
			before.get(i).setInWorld(worldObj);
		hasUndone = true;
	}

	public void loadAreaAfter(World worldObj)
	{
		for (int i = 0; i < after.size(); i++)
			after.get(i).setInWorld(worldObj);

		hasUndone = false;
	}
}
