package com.forgeessentials.util.events;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class ForgeEssentialsEventFactory extends ServerEventHandler {

    private HashMap<UUID, WarpPoint> lastPlayerPosition = new HashMap<>();

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent e)
    {
        if (e.side != Side.SERVER)
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
                FunctionHelper.teleportPlayer(player, before);
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
