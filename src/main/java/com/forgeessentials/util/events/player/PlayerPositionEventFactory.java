package com.forgeessentials.util.events.player;

import java.util.HashMap;
import java.util.UUID;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.util.events.ServerEventHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class PlayerPositionEventFactory extends ServerEventHandler
{

    private HashMap<UUID, WarpPoint> lastPlayerPosition = new HashMap<>();

    @SubscribeEvent
    public void playerTickEvent(ServerTickEvent.PlayerTickEvent e)
    {
        if (e.side != LogicalSide.SERVER || e.phase == ServerTickEvent.Phase.START)
            return;
        Player player = (Player) e.player;
        WarpPoint before = lastPlayerPosition.get(player.getGameProfile().getId());
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
        lastPlayerPosition.put(player.getGameProfile().getId(), new WarpPoint(e.player));
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        lastPlayerPosition.remove(e.getPlayer().getGameProfile().getId());
    }

}
