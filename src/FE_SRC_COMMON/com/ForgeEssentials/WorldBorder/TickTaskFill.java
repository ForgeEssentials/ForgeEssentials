package com.ForgeEssentials.WorldBorder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder.BorderShape;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Does the actual filling, with limited chuncks per tick.
 * @author Dries007
 */

public class TickTaskFill implements ITickTask
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8026130653577241537L;

	protected boolean			isComplete;

	protected WorldServer		world;
	protected int				dim;

	protected int				minX;
	protected int				minZ;

	protected int				maxX;
	protected int				maxZ;

	protected int				centerX;
	protected int				centerZ;
	protected int				rad;

	protected int				tps					= 20;

	protected Long				ticks				= 0L;

	public static boolean		enablemsg			= true;

	public static boolean		debug;

	protected MinecraftServer	server				= FMLCommonHandler.instance().getMinecraftServerInstance();

	private int					X;

	private int					Z;

	private int					eta;

	public TickTaskFill(World world)
	{
		isComplete = false;
		this.world = (WorldServer) world;
		X = minX = (ModuleWorldBorder.minX - ModuleWorldBorder.overGenerate) / 16;
		Z = minZ = (ModuleWorldBorder.minZ - ModuleWorldBorder.overGenerate) / 16;
		maxX = (ModuleWorldBorder.maxX + ModuleWorldBorder.overGenerate) / 16;
		maxZ = (ModuleWorldBorder.maxZ + ModuleWorldBorder.overGenerate) / 16;
		centerX = ModuleWorldBorder.X / 16;
		centerZ = ModuleWorldBorder.Z / 16;
		rad = (ModuleWorldBorder.rad + ModuleWorldBorder.overGenerate) / 16;

		TickTaskHandler.addTask(this);

		System.out.println("MinX=" + minX + " MaxX=" + maxX);
		System.out.println("MinZ=" + minZ + " MaxZ=" + maxZ);

		eta = ModuleWorldBorder.shape.getETA();

		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));

	}

	public String getETA()
	{
		return eta / 10 + " ticks.";
	}

	public void warnEveryone(String msg)
	{
		OutputHandler.info("#### " + msg);
		if (enablemsg)
		{
			for (int var2 = 0; var2 < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); ++var2)
			{
				((EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(var2)).sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
			}
		}
	}

	@Override
	public void tick()
	{
		ticks++;
		eta--;

		if (ticks % 250 == 0)
		{
			warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));
		}

		try
		{
			Chunk chunk = world.theChunkProviderServer.loadChunk(X, Z);
			chunk.setChunkModified();
			world.theChunkProviderServer.safeSaveChunk(chunk);
			world.theChunkProviderServer.unload100OldestChunks();
			world.theChunkProviderServer.unloadChunksIfNotNearSpawn(X, Z);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		next();
	}

	private void next()
	{
		if (ModuleWorldBorder.shape.equals(BorderShape.square))
		{
			if (X <= maxX)
			{
				X++;
			}
			else
			{
				if (Z <= maxZ)
				{
					X = minX;
					Z++;
				}
				else
				{
					isComplete = true;
				}
			}
		}
		else if (ModuleWorldBorder.shape.equals(BorderShape.round))
		{
			while (true)
			{
				if (X <= maxX)
				{
					X++;
				}
				else
				{
					if (Z <= maxZ)
					{
						X = minX;
						Z++;
					}
					else
					{
						isComplete = true;
					}
				}

				if (rad >= ModuleWorldBorder.getDistanceRound(centerX, centerZ, X, Z))
				{
					break;
				}
			}
		}
		else
			throw new RuntimeException("WTF?");
	}

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
