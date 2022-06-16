package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
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
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        interval = args.parseInt();

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing message argument");
        message = args.toString();
    }

    @Override
    public void activate(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
            doEffect(player);
    }

    @Override
    public void tick(WorldBorder border, ServerPlayerEntity player)
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

    public void doEffect(ServerPlayerEntity player)
    {
        ChatOutputHandler.chatError(player.createCommandSourceStack(), ModuleChat.processChatReplacements(player.createCommandSourceStack(), message));
    }

    @Override
    public String toString()
    {
        return "message trigger: " + triggerDistance + "interval: " + interval + " message: " + message;
    }

    @Override
    public String getSyntax()
    {
        return "<interval> <message>";
    }

}
