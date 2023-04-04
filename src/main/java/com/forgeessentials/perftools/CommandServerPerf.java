package com.forgeessentials.perftools;

import java.text.DecimalFormat;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandServerPerf extends ForgeEssentialsCommandBuilder
{

    public CommandServerPerf(boolean enabled)
    {
        super(enabled);
    }


    private static final DecimalFormat formatNumbers = new DecimalFormat("########0.000");

    @Override
    public String getPrimaryAlias()
    {
        return "perfstats";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, null)
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), "Memory usage:");
        ChatOutputHandler.chatNotification(ctx.getSource(), "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(ctx.getSource(), "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(ctx.getSource(), "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ChatOutputHandler.chatNotification(ctx.getSource(), "Used: " + (used / 1024 / 1024) + " MiB");
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ChatOutputHandler.chatNotification(ctx.getSource(),
                "Average tick time: " + formatNumbers.format(this.func_120035_a(server.tickTimes) * 1.0E-6D) + " ms");
        ChatOutputHandler.chatNotification(ctx.getSource(), "For TPS information, run /forge tps.");
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.perftools.perfstats";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }


    private double func_120035_a(long[] p_120035_1_)
    {
        long i = 0L;

        for (int j = 0; j < p_120035_1_.length; ++j)
        {
            i += p_120035_1_[j];
        }

        return (double) i / (double) p_120035_1_.length;
    }
}
