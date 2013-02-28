package com.ForgeEssentials.util;

//Depreciated
import java.io.Serializable;
import java.util.ArrayList;

public class BackupArea implements Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -6482215360941545829L;
	public ArrayList<BlockSaveable>	before;
	public ArrayList<BlockSaveable>	after;

	public BackupArea()
	{
		before = new ArrayList<BlockSaveable>();
		after = new ArrayList<BlockSaveable>();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BackupArea)
			return before.equals(((BackupArea) obj).before);
		return false;
	}

}
