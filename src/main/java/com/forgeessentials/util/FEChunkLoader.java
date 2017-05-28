package com.forgeessentials.util;

import java.util.HashMap;
import java.util.List;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

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
        if (map.containsKey(world.provider.getDimension()))
        {
            LoggingHandler.felog.debug(world.provider.getDimension() + " was already loaded. add 1 to count.");

            Ticket ticket = map.get(world.provider.getDimension());
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
                LoggingHandler.felog.debug("Force loaded " + world.provider.getDimension());
                ForgeChunkManager.forceChunk(ticket, new ChunkPos(0, 0));
                ticket.getModData().setInteger("count", 1);
                map.put(world.provider.getDimension(), ticket);
                return true;
            }
        }
    }

    public boolean unforceLoadWorld(World world)
    {
        if (map.containsKey(world.provider.getDimension()))
        {
            Ticket ticket = map.get(world.provider.getDimension());
            ticket.getModData().setInteger("count", ticket.getModData().getInteger("count") - 1);
            if (ticket.getModData().getInteger("count") == 0)
            {
                LoggingHandler.felog.debug(world.provider.getDimension() + " was removed fron the force loaded list.");
                ForgeChunkManager.unforceChunk(ticket, new ChunkPos(0, 0));
                ForgeChunkManager.releaseTicket(ticket);
                map.remove(world.provider.getDimension());
                return true;
            }
            else
            {
                LoggingHandler.felog.debug(world.provider.getDimension() + " is still force loaded. " + ticket.getModData().getInteger("count") + " requests remain.");
                return false;
            }
        }
        else
        {
            LoggingHandler.felog.debug(world.provider.getDimension() + " was not force loaded.");
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
