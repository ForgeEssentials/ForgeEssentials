package com.forgeessentials.util.events;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;

public class ForgeEssentialsEventFactory extends ServerEventHandler
{

    private HashMap<UUID, WarpPoint> lastPlayerPosition = new HashMap<>();

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent e)
    {
        if (e.side != Side.SERVER || e.phase == TickEvent.Phase.START)
            return;
        EntityPlayerMP player = (EntityPlayerMP) e.player;
        WarpPoint before = lastPlayerPosition.get(player.getPersistentID());
        WarpPoint current = new WarpPoint(e.player);

        if (before != null && !player.isDead && player.worldObj != null && !before.equals(current))
        {
            PlayerMoveEvent event = new PlayerMoveEvent(player, before, current);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled())
            {
                // Check, if the position was not changed by one of the event handlers
                if (current.equals(new WarpPoint(e.player)))
                    // Move the player to his last position
                    TeleportHelper.doTeleport(player, before);
            }
        }
        lastPlayerPosition.put(player.getPersistentID(), new WarpPoint(e.player));
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        lastPlayerPosition.remove(e.player.getPersistentID());
    }

}
