package com.ForgeEssentials.WorldControl;

//Depreciated
import java.io.Serializable;
import java.util.ArrayList;

import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

public class BackupArea implements Serializable
{
	public ArrayList<BlockSaveable> before;
	public ArrayList<BlockSaveable> after;
	
	public BackupArea()
	{
		before = new ArrayList<BlockSaveable>();
		after = new ArrayList<BlockSaveable>();
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof BackupArea)
		{
			return before.equals(((BackupArea) obj).before);
		}
		return false;
	}

}
