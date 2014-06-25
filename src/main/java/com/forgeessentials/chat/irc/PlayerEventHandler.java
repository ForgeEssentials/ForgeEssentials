package com.forgeessentials.chat.irc;

import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class PlayerEventHandler implements IPlayerTracker {

    @Override
    public void onPlayerChangedDimension(EntityPlayer arg0)
    {
    }

    @Override
    public void onPlayerLogin(EntityPlayer arg0)
    {
        IRCHelper.postIRC(arg0.username + " joined the server.");
    }

    @Override
    public void onPlayerLogout(EntityPlayer arg0)
    {
        IRCHelper.postIRC(arg0.username + " left the server.");
    }

    @Override
    public void onPlayerRespawn(EntityPlayer arg0)
    {
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (e.entityLiving instanceof EntityPlayer)
        {
            IRCHelper.postIRC(e.source.getDeathMessage(e.entityLiving).toString());
        }
    }

}
