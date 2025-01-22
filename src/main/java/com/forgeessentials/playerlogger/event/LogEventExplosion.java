package com.forgeessentials.playerlogger.event;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
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
        for (BlockPos blockPos : event.getAffectedBlocks())
            blocks.add(new CachedBlockData(event.world, blockPos));
    }

    @Override
    public void process(EntityManager em)
    {
        WorldData worldData = getWorld(event.world.provider.getDimensionId());
        for (CachedBlockData blockData : blocks)
        {
            if (!blockData.block.getMaterial().equals(Material.air))
            {
                Action01Block action = new Action01Block();
                action.time = date;
                action.world = worldData;
                action.block = getBlock(blockData.block);
                action.metadata = blockData.metadata;
                action.entity = blockData.tileEntityBlob;
                action.type = ActionBlockType.DETONATE;
                action.x = blockData.pos.getX();
                action.y = blockData.pos.getX();
                action.z = blockData.pos.getZ();
                em.persist(action);
            }
        }
    }

}