package com.forgeessentials.commands.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.TickTask;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class TickTaskBlockFinder implements TickTask
{

    private World world;
    private EntityPlayer player;
    private Block block;
    private IBlockState blockState;
    private String blockName;

    private int targetRange;
    private int targetAmount;
    private int centerX, centerZ;
    private ItemStack stack;
    private int speed;

    // (di, dj) is a vector - direction in which we move right now
    private int di = 1;
    private int dj = 0;
    // length of current segment
    private int segment_length = 1;

    // current position (i, j) and how much of current segment we passed
    private int i = 0;
    private int j = 0;
    private int segment_passed = 0;

    ArrayList<Point> results = new ArrayList<Point>();

    public TickTaskBlockFinder(EntityPlayer player, String id, int meta, int range, int amount, int speed)
    {
        this.player = player;
        this.targetRange = range;
        this.targetAmount = amount;
        this.speed = speed;
        this.centerX = (int) player.posX;
        this.centerZ = (int) player.posZ;
        world = player.world;

        if (id.equalsIgnoreCase("air")) {
            block = Blocks.AIR;
        }
        else
        {
            int intId = -1;
            try
            {
                intId = Integer.parseInt(id);
                block = Block.REGISTRY.getObjectById(intId);
            }
            catch (NumberFormatException e)
            {
                block = Block.REGISTRY.getObject(new ResourceLocation(id));
            }

            if (block == Blocks.AIR && intId != 0)
            {
                msg("Error: " + id + ":" + meta + " unkown.");
                return;
            }
        }
        blockState = block.getStateFromMeta(meta);

        stack = new ItemStack(block, 1, meta);
        blockName = !stack.isEmpty() ? stack.getDisplayName() : ServerUtil.getBlockName(block);

        msg("Start the hunt for " + blockName);
        TaskRegistry.schedule(this);
    }

    @Override
    public boolean tick()
    {
        int speedcounter = 0;
        while (speedcounter < speed)
        {
            speedcounter++;

            int y = world.getActualHeight();
            while (results.size() < targetAmount && y >= 0)
            {
                BlockPos pos = new BlockPos(centerX + i, y, centerZ + j);
                IBlockState b = world.getBlockState(pos);
                if (blockState.equals(b))
                {
                    Point p = new Point(centerX + i, y, centerZ + j);
                    results.add(p);
                    msg("Found " + blockName + " at " + p.getX() + ";" + p.getY() + ";" + p.getZ());

                }
                y--;
            }

            // make a step, add 'direction' vector (di, dj) to current position (i, j)
            i += di;
            j += dj;
            ++segment_passed;

            if (segment_passed == segment_length)
            {
                // done with current segment
                segment_passed = 0;

                // 'rotate' directions
                int buffer = di;
                di = -dj;
                dj = buffer;

                // increase segment length if necessary
                if (dj == 0)
                {
                    ++segment_length;
                }
            }
        }
        if (results.size() >= targetAmount || segment_length > targetRange)
        {
            if (results.isEmpty())
            {
                msg("Found nothing withing target range.");
            }
            else
            {
                msg("Stopped looking for " + blockName);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean editsBlocks()
    {
        return false;
    }

    private void msg(String string)
    {
        ChatOutputHandler.chatNotification(player, string);
    }

}