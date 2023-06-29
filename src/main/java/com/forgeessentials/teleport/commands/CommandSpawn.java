package com.forgeessentials.teleport.commands;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandSpawn extends ForgeEssentialsCommandBuilder {

	public CommandSpawn(boolean enabled) {
		super(enabled);
	}

	@Override
	public String getPrimaryAlias() {
		return "spawn";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.ALL;
	}

	@Override
	public String getPermissionNode() {
		return TeleportModule.PERM_SPAWN;
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return baseBuilder
				.then(Commands.argument("player", EntityArgument.player())
						.executes(CommandContext -> execute(CommandContext, "player")))
				.executes(CommandContext -> execute(CommandContext, "me"));
	}

	@Override
	public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		if (params.equals("player")) {
			if (!hasPermission(ctx.getSource(), TeleportModule.PERM_SPAWN_OTHERS)) {
				ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
				return Command.SINGLE_SUCCESS;
			}
			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			;
			if (player.hasDisconnected()) {
				ChatOutputHandler.chatError(ctx.getSource(), Translator
						.format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
				return Command.SINGLE_SUCCESS;
			}

			WarpPoint point = RespawnHandler.getSpawn(player, null);
			if (point == null) {
				ChatOutputHandler.chatError(ctx.getSource(), "There is no spawnpoint set for that player.");
				return Command.SINGLE_SUCCESS;
			}

			TeleportHelper.teleport(player, point);
		}
		if (params.equals("me")) {
			ServerPlayerEntity player = getServerPlayer(ctx.getSource());

			WarpPoint point = RespawnHandler.getSpawn(player, null);
			if (point == null) {
				ChatOutputHandler.chatError(ctx.getSource(), "You have no spawnpoint");
				return Command.SINGLE_SUCCESS;
			}

			PlayerInfo.get(player.getUUID()).setLastTeleportOrigin(new WarpPoint(player));
			ChatOutputHandler.chatConfirmation(player, "Teleporting to spawn.");
			TeleportHelper.teleport(player, point);
		}
		return Command.SINGLE_SUCCESS;
	}

	@Override
	public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		if (params.equals("me")) {
			ChatOutputHandler.chatError(ctx.getSource(), "You need to specify a player");
			return Command.SINGLE_SUCCESS;
		}
		ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
		if (player.hasDisconnected()) {
			ChatOutputHandler.chatError(ctx.getSource(), Translator
					.format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
			return Command.SINGLE_SUCCESS;
		}

		WarpPoint point = RespawnHandler.getSpawn(player, null);
		if (point == null) {
			ChatOutputHandler.chatError(ctx.getSource(), "There is no spawnpoint set for that player.");
			return Command.SINGLE_SUCCESS;
		}

		TeleportHelper.teleport(player, point);
		return Command.SINGLE_SUCCESS;
	}

}
