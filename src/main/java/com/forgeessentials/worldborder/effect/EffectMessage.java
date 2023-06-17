package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

/**
 * Expected syntax: <interval> <message>
 */
public class EffectMessage extends WorldBorderEffect
{

    public String message = "You left the worldborder. Please return!";

    public int interval = 6000;

    @Override
    public void provideArguments(CommandContext<CommandSource> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
        message = StringArgumentType.getString(ctx, "message");
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
        ChatOutputHandler.chatError(player, ModuleChat.processChatReplacements(player.createCommandSourceStack(), message));
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
