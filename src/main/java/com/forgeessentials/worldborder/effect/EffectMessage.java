package com.forgeessentials.worldborder.effect;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

/**
 * Expected syntax: <interval> <message>
 */
public class EffectMessage extends WorldBorderEffect
{

    public String message = "You left the worldborder. Please return!";

    public int interval = 6000;

    @Override
    public void provideArguments(CommandContext<CommandSourceStack> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
        message = StringArgumentType.getString(ctx, "message");
    }

    @Override
    public void activate(WorldBorder border, ServerPlayer player)
    {
        if (interval <= 0)
            doEffect(player);
    }

    @Override
    public void tick(WorldBorder border, ServerPlayer player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            doEffect(player);
            pi.startTimeout(this.getClass().getName(), interval * 1000L);
        }
    }

    public void doEffect(ServerPlayer player)
    {
    	if(ModuleLauncher.getModuleList().contains("Chat")) {
    		ChatOutputHandler.chatError(player,
                    ModuleChat.processChatReplacements(player.createCommandSourceStack(), message));
    		return;
    	}
        ChatOutputHandler.chatError(player, message);
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
