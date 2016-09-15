package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> (in seconds)
 */
public class EffectKick extends WorldBorderEffect
{

    private int timeout = 0;

    @Override
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        timeout = args.parseInt();
    }

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        if (!MinecraftServer.getServer().isDedicatedServer())
        {
            LoggingHandler.felog.warn("[WorldBorder] Kick effect is not supported on integrated servers!");
            return;
        }
        ChatOutputHandler.chatError(player, Translator.format("You have %d seconds to return inside the world border, or you will get kicked!", timeout));
        PlayerInfo pi = PlayerInfo.get(player);
        pi.startTimeout(this.getClass().getName(), timeout * 1000);
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.playerNetServerHandler.kickPlayerFromServer("You left the world border");
            // For safety restart the timeout
            pi.startTimeout(this.getClass().getName(), timeout);
        }
    }

    public String toString()
    {
        return "kick trigger: " + triggerDistance + "interval: " + timeout;
    }

    public String getSyntax()
    {
        return "<interval>";
    }

}
