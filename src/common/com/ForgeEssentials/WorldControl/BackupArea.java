package com.ForgeEssentials.WorldControl;

//Depreciated
import java.io.Serializable;
import java.util.ArrayList;

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
