package com.forgeessentials.afterlife;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

public class AfterlifeEventHandler extends ServerEventHandler
{

    public List<Grave> newGraves = new ArrayList<>();

    @SubscribeEvent
    public void playerDeathDropEvent(PlayerDropsEvent event)
    {
        Grave grave = Grave.createGrave(event.entityPlayer, event.drops);
        if (grave != null)
        {
            newGraves.add(grave);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event)
    {
        for (Grave grave : newGraves)
            grave.updateBlocks();
        newGraves.clear();
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (event.entity.worldObj.isRemote)
            return;
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK)
            return;

        WorldPoint point = new WorldPoint(event.entity.worldObj, event.pos.getX(), event.pos.getY(), event.pos.getZ());
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        grave.interact((EntityPlayerMP) event.entityPlayer);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void blockBreakEvent(BreakEvent event)
    {
        if (event.world.isRemote)
            return;

        WorldPoint point = new WorldPoint(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ());
        Grave grave = Grave.graves.get(point);
        if (grave == null)
        {
            // Check for fence post
            point.setY(event.pos.getY() + 1);
            grave = Grave.graves.get(point);
            if (grave == null || !grave.hasFencePost)
                return;
        }

        grave.update();
        if (grave.isProtected)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer(), Translator.translate("You may not defile the grave of a player"));
            return;
        }
        grave.remove(true);
    }

}
