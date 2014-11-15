package com.forgeessentials.commands.util;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.BlockSaveable;
import com.forgeessentials.util.tasks.ITickTask;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class TickTaskPulseHelper implements ITickTask {
    // stuff needed
    private final World WorldObj;
    private final Point BlockCord;
    // actually used
    private final int timeToLive;
    private int ticks = 0;
    private BackupArea back = new BackupArea();

    public TickTaskPulseHelper(World worlObj, Point Cordniates, int ticks)
    {
        WorldObj = worlObj;
        BlockCord = Cordniates;
        timeToLive = ticks;
    }

    @Override
    public void tick()
    {
        if (ticks == 0)
        {
            //place block
            back.before.add(new BlockSaveable(WorldObj, BlockCord.getX(), BlockCord.getY(), BlockCord.getZ()));
            WorldObj.setBlock(BlockCord.getX(), BlockCord.getY(), BlockCord.getZ(), Blocks.redstone_block);
            back.after.add(new BlockSaveable(WorldObj, BlockCord.getX(), BlockCord.getY(), BlockCord.getZ()));

        }
        else if (ticks == timeToLive)
        {
            back.before.get(0).setinWorld(WorldObj);
            ticks = -1;
            return;
        }
        ticks++;
    }

    @Override
    public void onComplete()
    {

    }

    @Override
    public boolean isComplete()
    {

        return ticks == -1;
    }

    @Override
    public boolean editsBlocks()
    {

        return false;
    }

}
