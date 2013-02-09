package com.ForgeEssentials.WorldBorder;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Does the actual filling, with limited chuncks per tick.
 * 
 * @author Dries007
 * 
 */

public abstract class TickTaskFill implements ITickTask
{
	protected boolean						isComplete;

	protected WorldServer					world;
	protected int							dim;

	protected int							minX;
	protected int							minZ;

	protected int							maxX;
	protected int							maxZ;

	protected int							centerX;
	protected int							centerZ;
	protected int							rad;

	protected int							tps			= 20;

	protected Long							ticks		= 0L;
	protected int							chunksAtick	= 1;

	public boolean							autoPilot	= false;
	public int								targetTPS	= 20;

	public static boolean					debug;

	protected MinecraftServer				server		= FMLCommonHandler.instance().getMinecraftServerInstance();

	protected ArrayList<ChunkCoordIntPair>	toDo		= new ArrayList<ChunkCoordIntPair>();

	public TickTaskFill(WorldServer world)
	{
		isComplete = false;
		this.world = world;
		minX = ModuleWorldBorder.minX - ModuleWorldBorder.overGenerate;
		minZ = ModuleWorldBorder.minZ - ModuleWorldBorder.overGenerate;
		maxX = ModuleWorldBorder.maxX + ModuleWorldBorder.overGenerate;
		maxZ = ModuleWorldBorder.maxZ + ModuleWorldBorder.overGenerate;
		centerX = ModuleWorldBorder.X;
		centerZ = ModuleWorldBorder.Z;
		rad = ModuleWorldBorder.rad;

		genList();

		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));

	}

	public double getTPS()
	{
		double tps = FunctionHelper.getTPS(dim);
		if ((int) tps == 0)
		{
			tps = 1;
		}
		this.tps = (int) tps;
		return tps;
	}

	public String getETA()
	{
		return (int) ((toDo.size() / chunksAtick) / getTPS()) + Localization.get(Localization.UNIT_SECONDS);
	}

	public void warnEveryone(String msg)
	{
		OutputHandler.info("#### " + msg);
		for (int var2 = 0; var2 < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); ++var2)
		{
			((EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(var2)).sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
		}
	}

	public void engageTurbo(int speed)
	{
		warnEveryone(Localization.get(Localization.WB_TURBO_ON));
		chunksAtick = speed;
	}

	public void disengageTurbo()
	{
		warnEveryone(Localization.get(Localization.WB_TURBO_OFF));
		chunksAtick = 1;
	}

	public void engageAutopilot(int speed)
	{
		targetTPS = speed;
		autoPilot = true;
	}

	public void disengageAutopilot()
	{
		autoPilot = false;
		chunksAtick = 1;
	}

	@Override
	public void tick()
	{
		/*
		 * User & console waring
		 */

		getTPS();

		ticks++;
		if (ticks % 20 == 0)
		{
			if (debug)
				warnEveryone(world.theChunkProviderServer.makeString() + " " + "toDo: " + toDo.size());
			warnEveryone(Localization.get(Localization.WB_FILL_STILLGOING).replaceAll("%eta", getETA()).replaceAll("%ctp", chunksAtick + ""));
		}

		/*
		 * AutoPilot
		 */

		if (autoPilot)
		{
			if (tps < targetTPS)
			{
				if (chunksAtick > 0)
				{
					--chunksAtick;
				}
				OutputHandler.finer("WB Autopilot: Less CPT:" + chunksAtick + " TPS:" + tps + " < " + targetTPS);
			}
			else if (tps - 2 > targetTPS)
			{
				// We can handle more!
				++chunksAtick;
				OutputHandler.finer("WB Autopilot: More CPT:" + chunksAtick + " TPS:" + tps + " > " + targetTPS);
			}
			else
			{
				OutputHandler.finer("WB Autopilot: Good CPT:" + chunksAtick + " TPS:" + tps + " ~~ " + targetTPS);
			}
		}

		/*
		 * Actual chunk gen
		 */

		int i = 0;
		while (i < chunksAtick && !isComplete)
		{
			ChunkCoordIntPair coords = toDo.get(0);
			toDo.remove(coords);
			if (!world.theChunkProviderServer.chunkExists(coords.chunkXPos, coords.chunkZPos))
			{
				i++;
				Chunk chunk = world.theChunkProviderServer.provideChunk(coords.chunkXPos, coords.chunkZPos);
				world.theChunkProviderServer.unloadChunksIfNotNearSpawn(coords.chunkXPos, coords.chunkZPos);
				world.theChunkProviderServer.unloadAllChunks();
				world.theChunkProviderServer.unload100OldestChunks();
			}

			if (toDo.size() == 0)
			{
				isComplete = true;
			}
		}
	}

	public abstract void genList();

	@Override
	public void onComplete()
	{
		try
		{
			boolean var6 = world.canNotSave;
			world.canNotSave = false;
			world.saveAllChunks(true, (IProgressUpdate) null);
			world.canNotSave = var6;
		}
		catch (MinecraftException var7)
		{
			warnEveryone("Save FAILED!");
			return;
		}
		warnEveryone(Localization.get(Localization.WB_FILL_DONE));
		warnEveryone(Localization.get(Localization.WB_FILL_FINISHED).replaceAll("%ticks", "" + ticks).replaceAll("%sec", "" + (int) (ticks / tps)));
		CommandWB.taskGooing = null;
	}

	@Override
	public boolean isComplete()
	{
		return isComplete;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}

	public void stop()
	{
		warnEveryone(Localization.get(Localization.WB_FILL_ABORTED));
		isComplete = true;
	}

	public void debug(String string)
	{
		if (debug)
		{
			OutputHandler.finer(string);
		}
	}
}
