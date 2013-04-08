package com.ForgeEssentials.backup;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WorldSaver implements ITickHandler
{
	public static String	start;
	public static String	done;
	public static String	failed;
	
	private static boolean isSaving;
	
	private static ConcurrentLinkedQueue<Integer> worlds = new ConcurrentLinkedQueue<Integer>();
	
	public static void addWorldNeedsSave(World world)
	{
		worlds.add(world.provider.dimensionId);
	}
	
	public static void addWorldNeedsSave(int id)
	{
		worlds.add(id);
	}

	public WorldSaver()
	{
		// nthing
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		// nothing.
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		WorldServer world = (WorldServer) tickData[0];
		// it needs saving. save it.
		String name = world.provider.getDimensionName();
		int id = world.provider.dimensionId;
		if (worlds.contains(id))
		{
			isSaving = true;
			ModuleBackup.msg(String.format(start, name));
			boolean bl = world.canNotSave;
			world.canNotSave = false;
			try
			{
				world.saveAllChunks(true, (IProgressUpdate) null);
			}
			catch (MinecraftException e)
			{
				OutputHandler.exception(Level.SEVERE, String.format(failed, name), e);
				ModuleBackup.msg(String.format(failed, name));
			}
			world.canNotSave = bl;
			
			while (worlds.remove(id))
			{
				//just do nothing and remoev them ALL
			}
			isSaving = false;
			ModuleBackup.msg(String.format(done, name));
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel()
	{
		return "ForgeEssentials-ModuleBackup-WorldSaver";
	}
	
	public static boolean isSaving()
	{
		return isSaving;
	}
}
