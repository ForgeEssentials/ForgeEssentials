package com.forgeessentials.playerlogger;

import java.sql.Blob;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import com.forgeessentials.playerlogger.entity.BlockData;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.WorldData;

import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class PlayerLoggerEvent<T extends Event>
{
    public Date date;

    public T event;

    public PlayerLoggerEvent(T event)
    {
        this.event = event;
        this.date = new Date();
    }

    public abstract void process(EntityManager em);

    public BlockData getBlock(Block block)
    {
        return ModulePlayerLogger.getLogger().getBlock(block);
    }

    public WorldData getWorld(int dimensionId)
    {
        return ModulePlayerLogger.getLogger().getWorld(dimensionId);
    }

    public PlayerData getPlayer(String name)
    {
        return ModulePlayerLogger.getLogger().getPlayer(name);
    }

    public PlayerData getPlayer(UUID uuid)
    {
        return ModulePlayerLogger.getLogger().getPlayer(uuid);
    }

    public Blob getTileEntityBlob(TileEntity tileEntity)
    {
        return ModulePlayerLogger.getLogger().tileEntityToBlob(tileEntity);
    }

}