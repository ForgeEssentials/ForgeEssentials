package com.forgeessentials.playerlogger.event;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.world.ExplosionEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;
import com.forgeessentials.playerlogger.entity.WorldData;

public class LogEventExplosion extends PlayerLoggerEvent<ExplosionEvent.Detonate>
{

    public List<CachedBlockData> blocks = new ArrayList<>();

    public LogEventExplosion(ExplosionEvent.Detonate event)
    {
        super(event);
        for (ChunkPosition blockPos : event.getAffectedBlocks())
            blocks.add(new CachedBlockData(event.world, blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ));
    }

    @Override
    public void process(EntityManager em)
    {
        WorldData worldData = getWorld(event.world.provider.dimensionId);
        for (CachedBlockData blockData : blocks)
        {
            Action01Block action = new Action01Block();
            action.time = date;
            action.world = worldData;
            action.block = getBlock(blockData.block);
            action.metadata = blockData.metadata;
            action.entity = getTileEntityBlob(blockData.tileEntity);
            action.type = ActionBlockType.DETONATE;
            action.x = blockData.x;
            action.y = blockData.y;
            action.z = blockData.z;
            em.persist(action);
        }
    }

}