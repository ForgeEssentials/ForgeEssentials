package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> (in seconds)
 */
public class EffectKick extends WorldBorderEffect
{

    private int timeout = 0;

    @Override
    public void provideArguments(List<String> args) throws FECommandParsingException
    {
        if (args.isEmpty())
            throw new FECommandParsingException("Missing interval argument");
        timeout = CommandUtils.parseInt(args.remove(0));
    }

    @Override
    public void activate(WorldBorder border, ServerPlayerEntity player)
    {
        if (!player.getServer().isDedicatedServer())
        {
            LoggingHandler.felog.warn("[WorldBorder] Kick effect is not supported on integrated servers!");
            return;
        }
        ChatOutputHandler.chatError(player.createCommandSourceStack(),
                Translator.format("You have %d seconds to return inside the world border, or you will get kicked!", timeout));
        PlayerInfo pi = PlayerInfo.get(player);
        pi.startTimeout(this.getClass().getName(), timeout * 1000);
    }

    @Override
    public void tick(WorldBorder border, ServerPlayerEntity player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.connection.disconnect(new TranslationTextComponent("You left the world border"));
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
