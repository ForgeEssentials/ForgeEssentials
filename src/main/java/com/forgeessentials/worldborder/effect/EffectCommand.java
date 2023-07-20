package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Expected syntax: <interval> <command>
 */
public class EffectCommand extends WorldBorderEffect
{

    public String command = "say @player Go back while you still can!";

    public int interval = 0;

    @Override
    public void provideArguments(CommandContext<CommandSource> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
        command = StringArgumentType.getString(ctx, "command");
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
            pi.startTimeout(this.getClass().getName(), interval * 1000L);
        }
    }

    public void doEffect(ServerPlayerEntity player)
    {
        String cmd = ScriptArguments.processSafe(command, player.createCommandSourceStack());
        ServerLifecycleHooks.getCurrentServer().getCommands()
                .performCommand(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), cmd);
    }

    public String toString()
    {
        return "command trigger: " + triggerDistance + "interval: " + interval + " command: " + command;
    }

    public String getSyntax()
    {
        return "<interval> <command>";
    }
}
