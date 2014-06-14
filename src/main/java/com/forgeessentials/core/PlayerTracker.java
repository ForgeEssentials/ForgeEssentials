package com.forgeessentials.core;

import com.forgeessentials.core.misc.LoginMessage;
import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerTracker implements IPlayerTracker {

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        PlayerInfo.getPlayerInfo(player.username);
        LoginMessage.sendLoginMessage(player);
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        PlayerInfo.discardInfo(player.username);
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        // Not sure if we need to do anything here.
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        // Not sure if we need to do anything here.
    }
}
