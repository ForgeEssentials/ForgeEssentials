package com.forgeessentials.chat.irc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        IRCHelper.postIRC(e.player.getDisplayName() + " joined the server.");
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e)
    {
        IRCHelper.postIRC(e.player.getDisplayName() + " left the server.");
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (e.entityLiving instanceof EntityPlayer)
        {
            IRCHelper.postIRC(e.source.func_151519_b(e.entityLiving).getUnformattedTextForChat());
        }
    }

}
