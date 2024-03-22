package com.forgeessentials.perftools;

import java.text.DecimalFormat;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandServerPerf extends ForgeEssentialsCommandBuilder
{

    public CommandServerPerf(boolean enabled)
    {
        super(enabled);
    }

    private static final DecimalFormat formatNumbers = new DecimalFormat("########0.000");

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "perfstats";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), "Memory usage:");
        ChatOutputHandler.chatNotification(ctx.getSource(),
                "Max Allowed: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(ctx.getSource(),
                "Total Allocated: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(ctx.getSource(), "Amount Used: "
                + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " MiB");
        ChatOutputHandler.chatNotification(ctx.getSource(),
                "Amount Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ChatOutputHandler.chatNotification(ctx.getSource(),
                "Average tick time: " + formatNumbers.format(this.getAverage(server.tickTimes) * 1.0E-6D) + " ms");
        ChatOutputHandler.chatNotification(ctx.getSource(), "For Better TPS information, run /forge tps.");
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    private double getAverage(long[] p_120035_1_)
    {
        long i = 0L;

        for (long l : p_120035_1_) {
            i += l;
        }

        return (double) i / (double) p_120035_1_.length;
    }
}
