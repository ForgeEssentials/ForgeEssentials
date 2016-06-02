package com.forgeessentials.worldborder.effect;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> <message>
 */
public class EffectMessage extends WorldBorderEffect
{

    public String message = "You left the worldborder. Please return!";

    public int interval = 6000;

    @Override
    public boolean provideArguments(String[] args)
    {
        if (args.length < 2)
            return false;
        interval = Integer.parseInt(args[0]);
        message = StringUtils.join(ServerUtil.dropFirst(args), " ");
        return true;
    }

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
            pi.startTimeout(this.getClass().getName(), interval * 1000);
        }
    }

    public void doEffect(EntityPlayerMP player)
    {
        ChatOutputHandler.chatError(player, ModuleChat.processChatReplacements(player, message));
    }

    public String toString()
    {
        return "message trigger: " + triggerDistance + "interval: " + interval + " message: " + message;
    }

    public String getSyntax()
    {
        return "<interval> <message>";
    }

}
