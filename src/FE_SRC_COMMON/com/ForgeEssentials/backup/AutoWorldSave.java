package com.ForgeEssentials.backup;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class AutoWorldSave implements Runnable
{
	private Thread			thread;
	public static boolean	isSaving	= false;

	public AutoWorldSave()
	{
		thread = new Thread(this, "ForgeEssentials - AutoWorldSave");
		thread.start();
	}

	@Override
	public void run()
	{
		while (MinecraftServer.getServer().isServerRunning() && BackupConfig.worldSaveInterval != 0)
		{
			try
			{
				thread.sleep(BackupConfig.worldSaveInterval * 1000 * 60);
			}
			catch (InterruptedException e)
			{
				break;
			}

			while (AutoBackup.isBackingUp)
			{
				try
				{
					thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}

			ModuleBackup.msg("Saving all loaded worlds.");
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
			ModuleBackup.msg("The sever has saved the worlds.");
		}

		System.gc();
	}

	public void interrupt()
	{
		thread.interrupt();
	}
}
