package com.forgeessentials.serverNetwork.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet10SharedCommandSending;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandNetworking extends ForgeEssentialsCommandBuilder
{

    public CommandNetworking(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "fenetworking";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("start")
                        .then(Commands.literal("server")
                                .executes(CommandContext -> execute(CommandContext, "startserver"))
                                )
                        .then(Commands.literal("client")
                                .executes(CommandContext -> execute(CommandContext, "startclient"))
                                )
                        .then(Commands.literal("both")
                                .executes(CommandContext -> execute(CommandContext, "startboth"))
                                )
                        )
                .then(Commands.literal("stop")
                        .then(Commands.literal("server")
                                .executes(CommandContext -> execute(CommandContext, "stopserver"))
                                )
                        .then(Commands.literal("client")
                                .executes(CommandContext -> execute(CommandContext, "stopclient"))
                                )
                        .then(Commands.literal("both")
                                .executes(CommandContext -> execute(CommandContext, "stopboth"))
                                )
                        )
                .then(Commands.literal("reloadNetwork")
                        .executes(CommandContext -> execute(CommandContext, "reload"))
                        )
                .then(Commands.literal("saveConnectionData")
                        .executes(CommandContext -> execute(CommandContext, "save"))
                        )
                .then(Commands.literal("loadConnectionData")
                        .executes(CommandContext -> execute(CommandContext, "load"))
                        )
                .then(Commands.literal("sendCommandToParentServer")
                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "commandtoparent")
                                        )
                                )
                        )
                .then(Commands.literal("sendCommandToAllClients")
                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "commandtoclients")
                                        )
                                )
                        )
                .then(Commands.literal("sendCommandToClient")
                        .then(Commands.argument("client", StringArgumentType.word())
                                .suggests(SUGGEST_clients)
                                .then(Commands.argument("command", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "commandtoclient")
                                                )
                                        )
                                )
                        );
    }
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_clients = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        for (Entry<String, ConnectedClientData> arg : ModuleNetworking.getClients().entrySet())
        {
            if(arg.getValue().isAuthenticated()) {
                listArgs.add(arg.getKey());
            }
        }
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };
    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if(params.equals("startboth")){
            if(ModuleNetworking.getInstance().startServer()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to start server or server is already running!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started server!");
            if(ModuleNetworking.getInstance().startClient()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to start client, connect to sever, or client is already running!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started client!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopboth")){
            if(ModuleNetworking.getInstance().stopClient()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to stop client or client is already stopped!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped client!");
            if(ModuleNetworking.getInstance().stopServer()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to stop server or server is already stopped!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped server!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("startclient")){
            if(ModuleNetworking.getInstance().startClient()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to start client, connect to sever, or client is already running!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started client!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopclient")){
            if(ModuleNetworking.getInstance().stopClient()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to stop client or client is already stopped!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped client!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("startserver")){
            if(ModuleNetworking.getInstance().startServer()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to start server or server is already running!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Started server!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("stopserver")){
            if(ModuleNetworking.getInstance().stopServer()!=0){
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to stop server or server is already stopped!");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Stopped server!");
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("reload")){
            boolean serverRunning = false;
            boolean clientRunning = ModuleNetworking.getInstance().getClient().isChannelOpen();
            if(ModuleNetworking.getInstance().getServer().isChannelOpen()){serverRunning=true;}
            ModuleNetworking.getInstance().stopClient();
            ModuleNetworking.getInstance().stopServer();
            ModuleNetworking.getInstance().saveData();
            ModuleNetworking.getInstance().loadData();
            if(clientRunning) {ModuleNetworking.getInstance().startClient();}
            if(serverRunning) {ModuleNetworking.getInstance().startServer();}
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("save")){
            ModuleNetworking.getInstance().saveData();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Saved Networking data");

            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("load")){
            ModuleNetworking.getInstance().loadData();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Load Networking data");

            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("commandtoparent")){
            if(ModuleNetworking.getInstance().getClient()!=null &&ModuleNetworking.getInstance().getClient().isChannelOpen()) {
                ModuleNetworking.getInstance().getClient().sendPacket(new Packet10SharedCommandSending(StringArgumentType.getString(ctx, "command")));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Sent command to parent server");
            }
            else {
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to send command to parent server");
            }
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("commandtoclients")){
            if(ModuleNetworking.getInstance().getServer()!=null &&ModuleNetworking.getInstance().getServer().isChannelOpen()) {
                ModuleNetworking.getInstance().getServer().sendAllPacket(new Packet10SharedCommandSending(StringArgumentType.getString(ctx, "command")));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Sent command to clients");
            }
            else {
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to send command to clients");
            }
            return Command.SINGLE_SUCCESS;
        }
        if(params.equals("commandtoclient")){
            if(ModuleNetworking.getInstance().getServer()!=null &&ModuleNetworking.getInstance().getServer().isChannelOpen()) {
                boolean found = false;
                for(Entry<String, ConnectedClientData> data :ModuleNetworking.getClients().entrySet()) {
                    if(data.getKey().equals(StringArgumentType.getString(ctx, "client"))) {
                        ModuleNetworking.getInstance().getServer().sendPacketFor(data.getValue().getCurrentChannel(),new Packet10SharedCommandSending(StringArgumentType.getString(ctx, "command")));
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    ChatOutputHandler.chatError(ctx.getSource(), "Could not find clientId");
                    return Command.SINGLE_SUCCESS;
                }
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Sent command to client");
                return Command.SINGLE_SUCCESS;
            }
            else {
                ChatOutputHandler.chatError(ctx.getSource(), "Failed to send command to client");
            }
            return Command.SINGLE_SUCCESS;
        }
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
}
