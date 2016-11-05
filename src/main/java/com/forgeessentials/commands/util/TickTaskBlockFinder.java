package com.forgeessentials.commands.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.TaskRegistry;
import com.forgeessentials.util.TaskRegistry.TickTask;

import cpw.mods.fml.common.registry.GameData;

public class TickTaskBlockFinder implements TickTask
{

    private World world;
    private EntityPlayer player;
    private Block block;
    private String blockName;

    private int meta;
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
        this.meta = meta;
        this.targetRange = range;
        this.targetAmount = amount;
        this.speed = speed;
        this.centerX = (int) player.posX;
        this.centerZ = (int) player.posZ;
        world = player.worldObj;

        block = GameData.getBlockRegistry().getObject(id);
        if (block == null)
        {
            try
            {
                int intId = Integer.parseInt(id);
                block = GameData.getBlockRegistry().getRaw(intId);
            }
            catch (NumberFormatException e)
            {
                /* ignore */
            }
        }
        if (block == null)
        {
            msg("Error: " + id + ":" + meta + " unkown.");
            return;
        }

        stack = new ItemStack(block, 1, meta);
        blockName = stack.getItem() != null ? stack.getDisplayName() : GameData.getBlockRegistry().getNameForObject(block);

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
            while (results.size() >= targetAmount && y >= 0)
            {
                Block b = world.getBlock(centerX + i, y, centerZ + j);
                if (b.equals(block) && (meta == -1 || world.getBlockMetadata(centerX + i, y, centerZ + j) == meta))
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
                msg("Stoped looking for " + blockName);
            }
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
        ChatUtil.chatNotification(player, string);
    }

}