package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.TextComponent;

/**
 * Expected syntax: <interval> (in seconds)
 */
public class EffectKick extends WorldBorderEffect
{

    private int timeout = 0;

    @Override
    public void provideArguments(CommandContext<CommandSourceStack> ctx) throws FECommandParsingException
    {
        timeout = IntegerArgumentType.getInteger(ctx, "timeout");
    }

    @Override
    public void activate(WorldBorder border, ServerPlayer player)
    {
        if (!player.getServer().isDedicatedServer())
        {
            LoggingHandler.felog.warn("[WorldBorder] Kick effect is not supported on integrated servers!");
            return;
        }
        ChatOutputHandler.chatError(player.createCommandSourceStack(), Translator
                .format("You have %d seconds to return inside the world border, or you will get kicked!", timeout));
        PlayerInfo pi = PlayerInfo.get(player);
        pi.startTimeout(this.getClass().getName(), timeout * 1000L);
    }

    @Override
    public void tick(WorldBorder border, ServerPlayer player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.connection.disconnect(new TextComponent("You left the world border"));
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
