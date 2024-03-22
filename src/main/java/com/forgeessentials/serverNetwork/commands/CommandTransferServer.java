package com.forgeessentials.serverNetwork.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.utils.ConnectionData.ConnectedClientData;
import com.forgeessentials.serverNetwork.utils.ServerType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTransferServer extends ForgeEssentialsCommandBuilder
{

    public CommandTransferServer(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    protected @NotNull String getPrimaryAlias()
    {
        return "servertransfer";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
        		.then(Commands.argument("player", EntityArgument.player())
        				.then(Commands.argument("serverId", StringArgumentType.word())
                                .suggests(SUGGEST_servers)
                                .executes(CommandContext -> execute(CommandContext, "connectToServer")
                                        )
                                )
        				);
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_servers = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        if(ModuleNetworking.getInstance().getServerType()==ServerType.ROOTSERVER) {
            for (Entry<String, ConnectedClientData> arg : ModuleNetworking.getClients().entrySet())
            {
                if(arg.getValue().isAuthenticated()) {
                    listArgs.add(arg.getKey());
                }
            }
        }
        if(ModuleNetworking.getInstance().getServerType()==ServerType.CLIENTSERVER) {
            if(ModuleNetworking.getLocalClient().isAuthenticated()) {
                listArgs.add(ModuleNetworking.getLocalClient().getRemoteServerId());
            }
        }
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
    	ModuleNetworking.getInstance().getTranferManager().sendPlayerToServer(EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "serverId"));
    	return Command.SINGLE_SUCCESS;
    }

}
