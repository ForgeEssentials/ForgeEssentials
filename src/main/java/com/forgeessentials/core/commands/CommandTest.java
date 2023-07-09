package com.forgeessentials.core.commands;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandTest extends ForgeEssentialsCommandBuilder
{

    public CommandTest(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "fetesting";
    }

    @Override
    public String getPermissionNode()
    {
        return ForgeEssentials.PERM_CORE + ".testing";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands()
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
