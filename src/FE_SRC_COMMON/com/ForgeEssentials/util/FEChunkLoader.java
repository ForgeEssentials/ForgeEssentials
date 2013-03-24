package com.ForgeEssentials.util;

import java.util.HashMap;
import java.util.List;

import com.ForgeEssentials.core.ForgeEssentials;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class FEChunkLoader implements LoadingCallback
{
	static FEChunkLoader instance;
	
	HashMap<Integer, Ticket> map = new HashMap<Integer, Ticket>();
	
	public static FEChunkLoader instance()
	{
		return instance;
	}
	
	public FEChunkLoader()
	{
		instance = this;
	}
	
	public boolean forceLoadWorld(World world)
	{
		if (map.containsKey(world.provider.dimensionId))
		{
			OutputHandler.debug(world.provider.dimensionId + " was already loaded. add 1 to count.");
			
			Ticket ticket = map.get(world.provider.dimensionId);
			ticket.getModData().setInteger("count", ticket.getModData().getInteger("count") + 1);
			return true;
		}
		else
		{
			Ticket ticket = ForgeChunkManager.requestTicket(ForgeEssentials.instance, world, ForgeChunkManager.Type.NORMAL);
			if (ticket == null)
			{
				OutputHandler.debug("Ticket was null ?");
				return false;
			}
			else
			{
				OutputHandler.debug("Force loaded " + world.provider.dimensionId);
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(0, 0));
				ticket.getModData().setInteger("count", 1);
				map.put(world.provider.dimensionId, ticket);
				return true;
			}
		}
	}
	
	public boolean unforceLoadWorld(World world)
	{
		if (map.containsKey(world.provider.dimensionId))
		{
			Ticket ticket = map.get(world.provider.dimensionId);
			ticket.getModData().setInteger("count", ticket.getModData().getInteger("count") - 1);
			if (ticket.getModData().getInteger("count") == 0)
			{
				OutputHandler.debug(world.provider.dimensionId + " was removed fron the force loaded list.");
				ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(0, 0));
				ForgeChunkManager.releaseTicket(ticket);
				map.remove(world.provider.dimensionId);
				return true;
			}
			else
			{
				OutputHandler.debug(world.provider.dimensionId + " is still force loaded. " + ticket.getModData().getInteger("count") + " requests remain.");
				return false;
			}
		}
		else
		{
			OutputHandler.debug(world.provider.dimensionId + " was not force loaded.");
			return false;
		}
	}
	
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		// We don't care about reloading the chunks at load right now. The filler needs to be restarted manually.
		for (Ticket ticket : tickets)
		{
			ForgeChunkManager.releaseTicket(ticket);
		}
	}
}
