package com.forgeessentials.backup;

import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.util.tasks.TaskRegistry;

public class AutoWorldSave implements Runnable
{

	public AutoWorldSave()
	{
		TaskRegistry.registerRecurringTask(this, 0, BackupConfig.worldSaveInterval, 0, 0, 0, BackupConfig.worldSaveInterval, 0, 0);
	}

	@Override
	public void run()
	{
		while (AutoBackup.isBackingUp)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		for (int i : DimensionManager.getIDs())
		{
			WorldSaver.addWorldNeedsSave(i);
		}
	}
}
