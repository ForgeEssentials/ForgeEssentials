package com.forgeessentials.playerlogger.event;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.world.ExplosionEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;
import com.forgeessentials.playerlogger.entity.WorldData;

public class LogEventExplosion extends PlayerLoggerEvent<ExplosionEvent.Detonate>
{

    public LogEventExplosion(ExplosionEvent.Detonate event)
    {
        super(event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(EntityManager em)
    {
        WorldData worldData = getWorld(event.world.provider.dimensionId);
        for (ChunkPosition blockPos : (List<ChunkPosition>) event.explosion.affectedBlockPositions)
        {
            ActionBlock action = new ActionBlock();
            action.time = new Date();
            action.world = worldData;
            action.block = getBlock(event.world.getBlock(blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ));
            action.metadata = event.world.getBlockMetadata(blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ);
            action.entity = getTileEntityBlob(event.world.getTileEntity(blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ));
            action.type = ActionBlockType.DETONATE;
            action.x = blockPos.chunkPosX;
            action.y = blockPos.chunkPosY;
            action.z = blockPos.chunkPosZ;
            em.persist(action);
        }
    }
    
}