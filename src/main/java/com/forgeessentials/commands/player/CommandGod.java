package com.forgeessentials.commands.player;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandGod extends ForgeEssentialsCommandBuilder
{

    public CommandGod(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "god";
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".god";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
        		.then(Commands.literal("me")
        				.then(Commands.argument("toggle", BoolArgumentType.bool())
        						.executes(CommandContext -> execute(CommandContext, "me"))))
        		.then(Commands.literal("others")
        				.then(Commands.argument("player", EntityArgument.player())
        						.then(Commands.argument("toggle", BoolArgumentType.bool())
                						.executes(CommandContext -> execute(CommandContext, "others")))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("me"))
        {
        	setGod(getServerPlayer(ctx.getSource()), BoolArgumentType.getBool(ctx, "toggle"));
        	return Command.SINGLE_SUCCESS;
        }
        if (params.equals("others"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
            	setGod(player, BoolArgumentType.getBool(ctx, "toggle"));
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), String
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public void setGod(ServerPlayerEntity player, boolean enabled)
    {
        APIRegistry.perms.setPlayerPermission(UserIdent.get(player), "fe.protection.damageby.*", enabled ? false : true);
        if (enabled)
        {
        	ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), "feheal " + player.getDisplayName().getString());
        }
        ChatOutputHandler.chatConfirmation(player, "God Mode " + (enabled ? "En" : "Dis") + "abled");
    }
}
