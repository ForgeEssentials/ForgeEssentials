package com.ForgeEssentials.WorldBorder;

import java.util.ArrayList;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder.BorderShape;
import com.ForgeEssentials.playerLogger.LogLoop;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLCommonHandler;

public class Filler implements Runnable 
{
	public static Thread thread;
	
	private boolean isComplete;

	private WorldServer world;
	private int dim;

	private int minX;
	private int minZ;

	private int maxX;
	private int maxZ;

	private int centerX;
	private int centerZ;
	private int rad;
	
	private Long ticks = 0L;
	
	public static boolean debug;

	private MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	
	private static ArrayList<ChunkCoordIntPair> toDo = new ArrayList<ChunkCoordIntPair>();

	private BorderShape shape;
	
	public Filler(WorldServer world, BorderShape shape, boolean newList) 
	{
		if(newList) toDo = new ArrayList<ChunkCoordIntPair>();
		
		isComplete = false;
		this.world = world;
		minX = ModuleWorldBorder.minX - ModuleWorldBorder.overGenerate;
		minZ = ModuleWorldBorder.minZ - ModuleWorldBorder.overGenerate;
		maxX = ModuleWorldBorder.maxX + ModuleWorldBorder.overGenerate;
		maxZ = ModuleWorldBorder.maxZ + ModuleWorldBorder.overGenerate;
		centerX = ModuleWorldBorder.X;
		centerZ = ModuleWorldBorder.Z;
		rad = ModuleWorldBorder.rad;
		this.shape = shape;
		
		thread = new Thread(this, "ForgeEssentials - WorldBorder - Filler");
		thread.start();
	}

	/*
	 * Main loop
	 */
	
	@Override
	public void run() 
	{
		try 
		{
			warnEveryone(Localization.get(Localization.WB_FILL_START));
			
			if(toDo.isEmpty()) genList();
			
			while (!isComplete)
			{
				if(toDo.size() == 0)
				{
					isComplete = true;
				}
				
				ticks ++;
				
				if(ticks % 100 == 0)
				{
					warnEveryone(world.theChunkProviderServer.makeString()  + " " + "toDo: " + toDo.size());
				}
				
				if(ticks % 50 == 0)
				{
					try
					{
						boolean var6 = world.canNotSave;
						world.canNotSave = false;
						world.saveAllChunks(true, (IProgressUpdate)null);
						world.canNotSave = var6;
					}
					catch (MinecraftException var7)
					{
						warnEveryone("Save FAILED!");
					    return;
					}
					world.theChunkProviderServer.unload100OldestChunks();
				}
				
				ChunkCoordIntPair coords = toDo.get(0);
				
				try 
				{
					if(!world.theChunkProviderServer.chunkExists(coords.chunkXPos, coords.chunkZPos))
					{
						world.theChunkProviderServer.provideChunk(coords.chunkXPos, coords.chunkZPos);
						world.theChunkProviderServer.unloadChunksIfNotNearSpawn(coords.chunkXPos, coords.chunkZPos);
					}
					toDo.remove(coords);
				}
				catch (Exception e) 
				{
					warnEveryone("Exception in chunk: " + coords.toString() + " Marked for later update.");
					warnEveryone(e.toString());
					e.printStackTrace();
				}
			}
			
			try
			{
				boolean var6 = world.canNotSave;
				world.canNotSave = false;
				world.saveAllChunks(true, (IProgressUpdate)null);
				world.canNotSave = var6;
			}
			catch (MinecraftException var7)
			{
				warnEveryone("Save FAILED!");
			    return;
			}
			
			warnEveryone(Localization.get(Localization.WB_FILL_DONE));
		}
		catch (Exception e) 
		{
			warnEveryone(e.toString());
			e.printStackTrace();
		}
		finally
		{
			warnEveryone("Chunks done: " + ticks);
			CommandWB.taskGooing = null;
		}
	}

	private void genList() 
	{
		if(shape == shape.square)
		{
			for(int X = this.minX; X <= this.maxX; X = X + 16)
			{
				for(int Z = this.minZ; Z <= this.maxZ; Z = Z + 16)
				{
					toDo.add(new ChunkCoordIntPair(X, Z));
				}
			}
		}
		if(shape == shape.round)
		{
			for(int X = this.minX; X <= this.maxX; X = X + 16)
			{
				for(int Z = this.minZ; Z <= this.maxZ; Z = Z + 16)
				{
					if((rad + ModuleWorldBorder.overGenerate) < ModuleWorldBorder.getDistanceRound(centerX, centerZ, X, Z))
					{
						toDo.add(new ChunkCoordIntPair(X, Z));
					}
				}
			}
		}
		warnEveryone(toDo.size() + " chunks to generate.");
	}
	
	public void stop()
	{
		warnEveryone(Localization.get(Localization.WB_FILL_ABORTED));
		isComplete = true;
	}
	
	public void onComplete()
	{
		try
        {
			boolean var6 = world.canNotSave;
			world.canNotSave = false;
			world.saveAllChunks(true, (IProgressUpdate)null);
			world.canNotSave = var6;
        }
        catch (MinecraftException var7)
        {
        	warnEveryone("Save FAILED!");
            return;
        }
		warnEveryone(Localization.get(Localization.WB_FILL_DONE));
		CommandWB.taskGooing = null;
	}
	
	public void debug(String string) 
	{
		if(debug)
		{
			OutputHandler.finer(string);
		}
	}

	public void warnEveryone(String msg)
	{
		OutputHandler.info("#### " + msg);
		for (int var2 = 0; var2 < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); ++var2)
		{
			((EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(var2))
					.sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
		}
	}
}
