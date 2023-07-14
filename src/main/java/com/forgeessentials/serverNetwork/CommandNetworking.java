package com.forgeessentials.serverNetwork;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
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
                        .executes(CommandContext -> execute(CommandContext, "stopserver")))
                .then(Commands.literal("clientmessage")
                        .executes(CommandContext -> execute(CommandContext, "clientmessage")))
                .then(Commands.literal("servermessage")
                        .executes(CommandContext -> execute(CommandContext, "servermessage")));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if(params.equals("startclient")){
            if(ModuleNetworking.instance.startClient()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to start client or client is already running!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started client!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopclient")){
            if(ModuleNetworking.instance.stopClient()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to stop client or client is already stopped!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped client!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("startserver")){
            if(ModuleNetworking.instance.startServer()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to start server or server is already running!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started server!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopserver")){
            if(ModuleNetworking.instance.stopServer()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to stop server or server is already stopped!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped server!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("clientmessage")){
            //ModuleNetworking.instance.getClient().sendPacket(new Packet2ClientPassword("Client Message"));
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("servermessage")){
            //ModuleNetworking.instance.getServer().sendPacket(new Packet2ClientPassword("Client Message"));
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
