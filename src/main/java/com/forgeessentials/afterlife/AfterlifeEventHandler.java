package com.forgeessentials.afterlife;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class AfterlifeEventHandler extends ServerEventHandler
{

    public List<Grave> newGraves = new ArrayList<>();

    @SubscribeEvent
    public void playerDeathDropEvent(PlayerDropsEvent e)
    {
        Grave grave = Grave.createGrave(e.entityPlayer, e.drops);
        if (grave != null)
        {
            newGraves.add(grave);
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        for (Grave grave : newGraves)
            grave.updateBlocks();
        newGraves.clear();
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent e)
    {
        if (e.entity.worldObj.isRemote)
            return;
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK)
            return;

        WorldPoint point = new WorldPoint(e.entity.worldObj, e.x, e.y, e.z);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        // Block block = e.entity.worldObj.getBlock(e.x, e.y, e.z);
        // if (block != Blocks.skull && block != Blocks.chest && block != Blocks.fence)
        // return;

        grave.interact((EntityPlayerMP) e.entityPlayer);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void blockBreakEvent(BreakEvent e)
    {
        if (e.world.isRemote)
            return;

        WorldPoint point = new WorldPoint(e.world, e.x, e.y, e.z);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
        {
            // Check for fence post
            point.setY(e.y + 1);
            grave = Grave.graves.get(point);
            if (grave == null || !grave.hasFencePost)
                return;
        }

        grave.update();
        if (grave.isProtected)
        {
            e.setCanceled(true);
            ChatOutputHandler.chatError(e.getPlayer(), Translator.translate("You may not defile the grave of a player"));
            return;
        }
        grave.remove(true);
    }

}
