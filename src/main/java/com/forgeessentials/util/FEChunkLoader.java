package com.forgeessentials.util;

import java.util.HashMap;
import java.util.List;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.Ticket;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.common.world.ForgeChunkManager.LoadingValidationCallback;
import net.minecraftforge.common.world.ForgeChunkManager.TicketHelper;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

public class FEChunkLoader implements LoadingValidationCallback
{
    static FEChunkLoader instance;

    HashMap<RegistryKey<World>, Ticket> map = new HashMap<RegistryKey<World>, Ticket>();

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
        if (map.containsKey(world.dimension()))
        {
            LoggingHandler.felog.debug(world.dimension() + " was already loaded. add 1 to count.");

            Ticket ticket = map.get(world.dimension());
            ticket.getModData().setInteger("count", ticket.getModData().getInteger("count") + 1);
            return true;
        }
        else
        {
            Ticket ticket = ForgeChunkManager.requestTicket(ForgeEssentials.instance, world, ForgeChunkManager.Type.NORMAL);
            if (ticket == null)
            {
                LoggingHandler.felog.debug("Ticket was null ?");
                return false;
            }
            else
            {
                LoggingHandler.felog.debug("Force loaded " + world.dimension());
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(0, 0));
                ticket.getModData().setInteger("count", 1);
                map.put(world.dimension(), ticket);
                return true;
            }
        }
    }

    public boolean unforceLoadWorld(World world)
    {
        if (map.containsKey(world.dimension()))
        {
            Ticket ticket = map.get(world.dimension());
            ticket.getModData().setInteger("count", ticket.getModData().getInteger("count") - 1);
            if (ticket.getModData().getInteger("count") == 0)
            {
                LoggingHandler.felog.debug(world.dimension() + " was removed fron the force loaded list.");
                ForgeChunkManager.unforceChunk(ticket, new ChunkPos(0, 0));
                ForgeChunkManager.releaseTicket(ticket);
                map.remove(world.dimension());
                return true;
            }
            else
            {
                LoggingHandler.felog.debug(world.dimension() + " is still force loaded. " + ticket.getModData().getInteger("count") + " requests remain.");
                return false;
            }
        }
        else
        {
            LoggingHandler.felog.debug(world.dimension() + " was not force loaded.");
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

	@Override
	public void validateTickets(ServerWorld world, TicketHelper ticketHelper) {
		// TODO Auto-generated method stub
		
	}
}
