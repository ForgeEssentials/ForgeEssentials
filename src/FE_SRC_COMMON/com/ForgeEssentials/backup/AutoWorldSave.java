package com.ForgeEssentials.backup;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.ForgeEssentials.util.tasks.TaskRegistry;

public class AutoWorldSave implements Runnable
{
	public static String	start;
	public static String	done;
	public static boolean	isSaving	= false;

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

		ModuleBackup.msg(start);
		isSaving = true;

		for (int i : DimensionManager.getIDs())
		{
			try
			{
				WorldServer world = DimensionManager.getWorld(i);
				boolean bl = world.canNotSave;
				world.canNotSave = false;
				world.saveAllChunks(true, (IProgressUpdate) null);
				world.canNotSave = bl;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		isSaving = false;
		ModuleBackup.msg(done);

		System.gc();
	}
}
