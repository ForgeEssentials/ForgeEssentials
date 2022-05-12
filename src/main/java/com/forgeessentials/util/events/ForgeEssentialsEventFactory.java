package com.forgeessentials.util.events;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ServerTickEvent;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ForgeEssentialsEventFactory extends ServerEventHandler
{

    private HashMap<UUID, WarpPoint> lastPlayerPosition = new HashMap<>();

    @SubscribeEvent
    public void playerTickEvent(ServerTickEvent.PlayerTickEvent e)
    {
        if (e.side != LogicalSide.SERVER || e.phase == ServerTickEvent.Phase.START)
            return;
        PlayerEntity player = (PlayerEntity) e.player;
        WarpPoint before = lastPlayerPosition.get(player.getUUID());
        WarpPoint current = new WarpPoint(e.player);

        if (before != null && !player.isDeadOrDying() && player.level != null && !before.equals(current))
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
        lastPlayerPosition.put(player.getUUID(), new WarpPoint(e.player));
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        lastPlayerPosition.remove(e.player.getPersistentID());
    }

}
