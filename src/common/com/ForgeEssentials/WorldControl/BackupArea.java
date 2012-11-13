package com.ForgeEssentials.WorldControl;

import java.io.Serializable;
import java.util.ArrayList;

import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

public class BackupArea implements Serializable, Comparable
{
	public ArrayList<BlockSaveable> before;
	public ArrayList<BlockSaveable> after;
	public int backupID;
	
	public BackupArea()
	{
		before = new ArrayList<BlockSaveable>();
	}

	@Override
	public int compareTo(Object arg0)
	{
		// TODO Auto-generated method stub
		return 0;
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
