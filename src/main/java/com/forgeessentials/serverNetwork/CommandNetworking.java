package com.forgeessentials.serverNetwork;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandNetworking extends ForgeEssentialsCommandBuilder
{

    public CommandNetworking(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "fenetworking";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("startclient")
                        .executes(CommandContext -> execute(CommandContext, "startclient")))
                .then(Commands.literal("stopclient")
                        .executes(CommandContext -> execute(CommandContext, "stopclient")))
                .then(Commands.literal("startserver")
                        .executes(CommandContext -> execute(CommandContext, "startserver")))
                .then(Commands.literal("stopserver")
                        .executes(CommandContext -> execute(CommandContext, "stopserver")));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if(params.equals("startclient")){
            ModuleNetworking.instance.startClient();
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopclient")){
            ModuleNetworking.instance.stopClient();;
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("startserver")){
            ModuleNetworking.instance.startServer();;
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopserver")){
            ModuleNetworking.instance.stopServer();;
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleNetworking.PERM;
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
}
