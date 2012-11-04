package com.ForgeEssentials;

import java.util.ArrayList;
import java.util.HashMap;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.CopyArea;

public class ConsoleInfo
{
	public static ConsoleInfo instance;
	
	private Point sel1;
	private Point sel2;
	private Selection selection;
	public CopyArea copy;
	private ArrayList<BackupArea> backups; // max = 5 backups.
	private int backupID;
	
	public ConsoleInfo()
	{
		backups = new ArrayList<BackupArea>(5);
		backupID = -1;
		sel1 = new Point(0,0,0);
		sel2 = new Point(0,0,0);
		selection = new Selection(sel1, sel2);
	}

	public Point getPoint1()
	{
		return sel1;
	}

	public void setPoint1(Point sel1)
	{
		this.sel1 = sel1;
		selection.start = sel1;
	}

	public Point getPoint2()
	{
		return sel2;
	}

	public void setPoint2(Point sel2)
	{
		this.sel2 = sel2;
		selection.end = sel2;
	}
	
	public Selection getSelection()
	{
		return selection;
	}
	
	public boolean canUndo()
	{
		return backupID >= 0;
	}
	
	public boolean canRedo()
	{
		return backupID < backups.size()-1;
	}
	
	public BackupArea getBackupForUndo()
	{
		return backups.get(backupID--);
	}
	
	public BackupArea getBackupForRedo()
	{
		if (backupID >= backups.size()-1)
			backupID = backups.size()-1;
		else
			backupID++;
		BackupArea backup = backups.get(backupID);
		return backup;
	}
	
	public void addBackup(BackupArea back)
	{
		if (backupID >= 4)
		{
			backupID = 4;
			backups.add(back);
			backups.remove(0);
		}
		else
		{
			backupID++;
			for (int i = backups.size(); i > backupID; i-- )
				backups.remove(i);
			backups.add(back);
		}
	}
}
