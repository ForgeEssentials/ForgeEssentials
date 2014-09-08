package com.forgeessentials.commands.util;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.tasks.ITickTask;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.registry.GameData;

public class TickTaskBlockFinder implements ITickTask {
    World world;
    EntityPlayer player;
    String id;
    int meta;
    int targetRange;
    int targetAmount;
    int centerX, centerZ;
    ItemStack stack;
    int speed;

    int count;

    // (di, dj) is a vector - direction in which we move right now
    int di = 1;
    int dj = 0;
    // length of current segment
    int segment_length = 1;

    // current position (i, j) and how much of current segment we passed
    int i = 0;
    int j = 0;
    int segment_passed = 0;

    ArrayList<Point> results = new ArrayList<Point>();

    public TickTaskBlockFinder(EntityPlayer player, String id, int meta, int range, int amount, int speed)
    {
        this.player = player;
        this.world = player.worldObj;
        this.id = id;
        this.meta = meta;
        this.targetRange = range;
        this.targetAmount = amount;
        this.speed = speed;
        this.stack = new ItemStack(GameData.getBlockRegistry().getObject(id), 1, meta);
        this.centerX = (int) player.posX;
        this.centerZ = (int) player.posZ;
        if (stack == null)
        {
            msg("Error. " + id + ":" + meta + " unkown.");
        }
        else
        {
            msg("Start the hunt for " + stack.getDisplayName() + " " + speed);
            TaskRegistry.registerTask(this);
        }
    }

    @Override
    public void tick()
    {
        int speedcounter = 0;
        while (!isComplete() && speedcounter < speed)
        {
            speedcounter++;
            count++;

            int y = world.getActualHeight();
            while (!isComplete() && y >= 0)
            {
                if (world.getBlock(centerX + i, y, centerZ + j).getUnlocalizedName() == id && (meta == -1
                        || world.getBlockMetadata(centerX + i, y, centerZ + j) == meta))
                {
                    Point p = new Point(centerX + i, y, centerZ + j);
                    results.add(p);
                    msg("Found " + stack.getDisplayName() + " at " + p.x + ";" + p.y + ";" + p.z);

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
    }

    private void msg(String string)
    {
        ChatUtils.sendMessage(player, EnumChatFormatting.AQUA + string);
    }

    @Override
    public void onComplete()
    {
        if (results.isEmpty())
        {
            msg("Found nothing withing target range.");
        }
        else
        {
            msg("Stoped looking for " + stack.getDisplayName());
        }
    }

    @Override
    public boolean isComplete()
    {
        return results.size() >= targetAmount || segment_length > targetRange;
    }

    @Override
    public boolean editsBlocks()
    {
        return false;
    }
}