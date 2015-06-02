package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectMessage extends WorldBorderEffect
{

    public String message = "You left the worldborder. Please return!";

    public int interval = 6000;

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            doEffect(player);
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            doEffect(player);
            pi.startTimeout(this.getClass().getName(), interval);
        }
    }

    public void doEffect(EntityPlayerMP player)
    {
        OutputHandler.chatError(player, ModuleChat.processChatReplacements(player, message));
    }

}
