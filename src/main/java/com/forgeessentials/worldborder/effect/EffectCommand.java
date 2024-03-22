package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

/**
 * Expected syntax: <interval> <command>
 */
public class EffectCommand extends WorldBorderEffect
{

    public String command = "say @player Go back while you still can!";

    public int interval = 0;

    @Override
    public void provideArguments(CommandContext<CommandSourceStack> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
        command = StringArgumentType.getString(ctx, "command");
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
