package com.forgeessentials.afterlife;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AfterlifeEventHandler extends ServerEventHandler {

    @SubscribeEvent
    public void playerDeathDropEvent(PlayerDropsEvent e)
    {
        Grave.createGrave(e.entityPlayer, e.drops);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent e)
    {
        if (e.entity.worldObj.isRemote)
            return;
        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
            return;
        
        WorldPoint point = new WorldPoint(e.entity.worldObj, e.x, e.y, e.z);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;
        
        //Block block = e.entity.worldObj.getBlock(e.x, e.y, e.z);
        //if (block != Blocks.skull && block != Blocks.chest && block != Blocks.fence)
        //    return;

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
        e.setCanceled(true);

        grave.update();
        if (grave.isProtected)
        {
            OutputHandler.chatError(e.getPlayer(), "You may not defile the grave of a player.");
            return;
        }
        grave.remove(true);
    }

}
