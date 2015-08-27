package com.forgeessentials.playerlogger.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameData;

public class RollbackInfo
{

    EntityPlayerMP player;

    private Selection area;

    private Date time;

    List<ActionBlock> changes;

    private int timeStep = -60;

    public PlaybackTask task;

    public RollbackInfo(EntityPlayerMP player, Selection area)
    {
        this.player = player;
        this.area = area;
        this.setTime(new Date());
    }

    @SuppressWarnings("deprecation")
    public void stepBackward()
    {
        timeStep *= timeStep < 0 ? 1.25 : -0.25;
        timeStep -= 1;
        getTime().setSeconds(getTime().getSeconds() + timeStep);
    }

    @SuppressWarnings("deprecation")
    public void stepForward()
    {
        timeStep *= timeStep > 0 ? 1.25 : -0.25;
        timeStep += 1;
        getTime().setSeconds(getTime().getSeconds() + timeStep);
    }

    public void previewChanges()
    {
        List<ActionBlock> lastChanges = changes;
        if (lastChanges == null)
            lastChanges = new ArrayList<>();

        changes = ModulePlayerLogger.getLogger().getLoggedBlockChanges(area, getTime(), null, 0);
        if (lastChanges.size() < changes.size())
        {
            for (int i = lastChanges.size(); i < changes.size(); i++)
            {
                ActionBlock change = changes.get(i);
                if (change.type == ActionBlockType.PLACE)
                {
                    sendBlockChange(player, change, Blocks.air, 0);
                    // System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time) + " REMOVED " +
                    // change.block.name);
                }
                else if (change.type == ActionBlockType.BREAK || change.type == ActionBlockType.DETONATE)
                {
                    sendBlockChange(player, change, GameData.getBlockRegistry().getObject(change.block.name), change.metadata);
                    // System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time) + " RESTORED " +
                    // change.block.name + ":" + change.metadata);
                }
            }
        }
        else if (lastChanges.size() > changes.size())
        {
            for (int i = lastChanges.size() - 1; i >= changes.size(); i--)
            {
                ActionBlock change = lastChanges.get(i);
                if (change.type == ActionBlockType.PLACE)
                {
                    sendBlockChange(player, change, GameData.getBlockRegistry().getObject(change.block.name), change.metadata);
                    // System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time) + " REPLACED " +
                    // change.block.name);
                }
                else if (change.type == ActionBlockType.BREAK || change.type == ActionBlockType.DETONATE)
                {
                    sendBlockChange(player, change, Blocks.air, 0);
                    // System.out.println(FEConfig.FORMAT_DATE_TIME_SECONDS.format(change.time) + " REBROKE " +
                    // change.block.name + ":" + change.metadata);
                }
            }
        }
    }

    public void confirm()
    {
        if (task != null)
            task.cancel();
        for (ActionBlock change : changes)
        {
            if (change.type == ActionBlockType.PLACE)
            {
                WorldServer world = DimensionManager.getWorld(change.world.id);
                world.setBlockToAir(change.x, change.y, change.z);
                System.out.println(change.time + " REMOVED " + change.block.name);
            }
            else if (change.type == ActionBlockType.BREAK || change.type == ActionBlockType.DETONATE)
            {
                WorldServer world = DimensionManager.getWorld(change.world.id);
                world.setBlock(change.x, change.y, change.z, GameData.getBlockRegistry().getObject(change.block.name), change.metadata, 3);
                world.setTileEntity(change.x, change.y, change.z, PlayerLogger.blobToTileEntity(change.entity));
                System.out.println(change.time + " RESTORED " + change.block.name + ":" + change.metadata);
            }
        }
    }

    public void cancel()
    {
        if (task != null)
            task.cancel();
        for (ActionBlock change : Lists.reverse(changes))
            player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(change.x, change.y, change.z, DimensionManager.getWorld(change.world.id)));
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    /**
     * Send a faked block-update to a player
     * 
     * @param player
     * @param change
     * @param newBlock
     * @param newMeta
     */
    public static void sendBlockChange(EntityPlayerMP player, ActionBlock change, Block newBlock, int newMeta)
    {
        S23PacketBlockChange packet = new S23PacketBlockChange(change.x, change.y, change.z, DimensionManager.getWorld(change.world.id));
        packet.field_148883_d = newBlock;
        packet.field_148884_e = newMeta;
        player.playerNetServerHandler.sendPacket(packet);
    }

    public static class PlaybackTask extends TimerTask
    {
        private RollbackInfo rb;
        private int speed;

        public PlaybackTask(RollbackInfo rb, int speed)
        {
            this.rb = rb;
            this.speed = speed;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void run()
        {
            rb.getTime().setSeconds(rb.getTime().getSeconds() - speed);
            rb.previewChanges();
        }

    }

}