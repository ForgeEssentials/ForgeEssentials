package com.forgeessentials.core.commands;

import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTest extends ForgeEssentialsCommandBuilder
{

    public CommandTest(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "fetesting";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        CommandDispatcher<CommandSourceStack> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands()
                .getDispatcher();
        String[] result = dispatcher.getAllUsage(dispatcher.getRoot(), ctx.getSource(), false);
        int num = 0;
        for (String node : result)
        {
            LoggingHandler.felog.info("Node " + num + ": " + node);
            num += 1;
        }
        return Command.SINGLE_SUCCESS;
    }
}
