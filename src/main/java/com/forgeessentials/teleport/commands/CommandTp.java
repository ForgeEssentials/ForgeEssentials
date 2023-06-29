package com.forgeessentials.teleport.commands;

import java.util.HashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
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
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandTp extends ForgeEssentialsCommandBuilder {

	public CommandTp(boolean enabled) {
		super(enabled);
	}

	/**
	 * Spawn point for each dimension
	 */
	public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return baseBuilder.then(Commands.argument("player", EntityArgument.player())
				.then(Commands.literal("toPlayer")
						.then(Commands.argument("toplayer", EntityArgument.player())
								.executes(CommandContext -> execute(CommandContext, "others"))))
				.then(Commands.literal("toPosition")
						.then(Commands.argument("position", BlockPosArgument.blockPos())
								.executes(CommandContext -> execute(CommandContext, "pos"))))
				.executes(CommandContext -> execute(CommandContext, "to")));
	}

	@Override
	public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		ServerPlayerEntity sender = getServerPlayer(ctx.getSource());
		if (params.equals("to")) {
			ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "player");

			if (target.hasDisconnected()) {
				ChatOutputHandler.chatError(ctx.getSource(), Translator
						.format("Player %s does not exist, or is not online.", target.getDisplayName().getString()));
				return Command.SINGLE_SUCCESS;
			}
			TeleportHelper.teleport(sender, new WarpPoint(target));
		} else if (params.equals("others")
				&& APIRegistry.perms.checkPermission(sender, TeleportModule.PERM_TP_OTHERS)) {

			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			if (!player.hasDisconnected()) {
				ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "toplayer");

				if (!target.hasDisconnected()) {
					PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
					playerInfo.setLastTeleportOrigin(new WarpPoint(player));
					WarpPoint point = new WarpPoint(target);
					TeleportHelper.teleport(player, point);
				} else {
					ChatOutputHandler.chatError(ctx.getSource(), Translator.format(
							"Player %s does not exist, or is not online.", target.getDisplayName().getString()));
					return Command.SINGLE_SUCCESS;
				}
			} else {
				ChatOutputHandler.chatError(ctx.getSource(), Translator
						.format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
				return Command.SINGLE_SUCCESS;
			}
		} else if (params.equals("pos")) {
			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			BlockPos pos = BlockPosArgument.getOrLoadBlockPos(ctx, "position");
			PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
			playerInfo.setLastTeleportOrigin(new WarpPoint(player));
			TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), pos, player.xRot, player.yRot));
		}
		return Command.SINGLE_SUCCESS;
	}

	@Override
	public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		CommandSource source = ctx.getSource();
		if (params.equals("others")) {

			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			if (!player.hasDisconnected()) {
				ServerPlayerEntity target = EntityArgument.getPlayer(ctx, "toplayer");

				if (!target.hasDisconnected()) {
					PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
					playerInfo.setLastTeleportOrigin(new WarpPoint(player));
					WarpPoint point = new WarpPoint(target);
					TeleportHelper.teleport(player, point);
				} else {
					ChatOutputHandler.chatError(ctx.getSource(), Translator.format(
							"Player %s does not exist, or is not online.", target.getDisplayName().getString()));
					return Command.SINGLE_SUCCESS;
				}
			} else {
				ChatOutputHandler.chatError(ctx.getSource(), Translator
						.format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
				return Command.SINGLE_SUCCESS;
			}
		} else if (params.equals("pos")) {
			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			;
			BlockPos pos = BlockPosArgument.getOrLoadBlockPos(ctx, "position");
			PlayerInfo playerInfo = PlayerInfo.get(player.getUUID());
			playerInfo.setLastTeleportOrigin(new WarpPoint(player));
			TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), pos, player.xRot, player.yRot));
		} else {
			ChatOutputHandler.chatError(source, Translator.translate("Console must use a subcommand!"));
		}
		return Command.SINGLE_SUCCESS;
	}

	@Override
	public String getPrimaryAlias() {
		return "tp";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getPermissionNode() {
		return TeleportModule.PERM_TP;
	}

	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.OP;
	}

}
