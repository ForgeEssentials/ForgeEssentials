package com.forgeessentials.playerlogger.event;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.Action01Block.ActionBlockType;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.world.ExplosionEvent;

public class LogEventExplosion extends PlayerLoggerEvent<ExplosionEvent.Detonate>
{

    public List<CachedBlockData> blocks = new ArrayList<>();

    public LogEventExplosion(ExplosionEvent.Detonate event)
    {
        super(event);
        for (BlockPos blockPos : event.getAffectedBlocks())
            blocks.add(new CachedBlockData(event.getWorld(), blockPos));
    }

    @Override
    public void process(EntityManager em)
    {
        for (CachedBlockData blockData : blocks)
        {
            if (blockData.block != Blocks.AIR)
            {
                Action01Block action = new Action01Block();
                action.time = date;
                action.world = event.getWorld().dimension().location().toString();
                action.block = getBlock(blockData.block);
                action.entity = blockData.tileEntityBlob;
                action.type = ActionBlockType.DETONATE;
                action.x = blockData.pos.getX();
                action.y = blockData.pos.getY();
                action.z = blockData.pos.getZ();
                em.persist(action);
            }
        }
    }

}