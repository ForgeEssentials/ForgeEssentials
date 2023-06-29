package com.forgeessentials.protection.commands;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandProtectionDebug extends ForgeEssentialsCommandBuilder {

	public CommandProtectionDebug(boolean enabled) {
		super(enabled);
	}

	@Override
	public String getPrimaryAlias() {
		return "protectdebug";
	}

	@Override
	public String getPermissionNode() {
		return "fe.protection.cmd.protectdebug";
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.ALL;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return baseBuilder.then(Commands.argument("command", StringArgumentType.greedyString())
				.executes(CommandContext -> execute(CommandContext, "setPass")));
	}

	@Override
	public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {

		ServerPlayerEntity player = getServerPlayer(ctx.getSource());
		if (player == null) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
			return Command.SINGLE_SUCCESS;
		}

		if (ModuleProtection.isDebugMode(player)) {
			ModuleProtection.setDebugMode(player, null);
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Disabled protection debug-mode");
		} else {
			String cmd = StringArgumentType.getString(ctx, "command");
			if (cmd.isEmpty())
				cmd = "global deny";
			cmd = "/p " + cmd + " ";

			ModuleProtection.setDebugMode(player, cmd);
			if (!ModuleProtection.isDebugMode(player))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "Enabled protection debug-mode");
			ChatOutputHandler.chatNotification(ctx.getSource(), "Command: " + cmd + "<perm>");
		}
		return Command.SINGLE_SUCCESS;
	}
}
