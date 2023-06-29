package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandPm extends ForgeEssentialsCommandBuilder {

	public CommandPm(boolean enabled) {
		super(enabled);
	}

	public static Map<PlayerEntity, WeakReference<PlayerEntity>> targetMap = new WeakHashMap<>();

	public static void setTarget(PlayerEntity sender, PlayerEntity target) {
		targetMap.put(sender, new WeakReference<PlayerEntity>(target));
	}

	public static void clearTarget(PlayerEntity sender) {
		targetMap.remove(sender);
	}

	public static PlayerEntity getTarget(PlayerEntity sender) {
		WeakReference<PlayerEntity> target = targetMap.get(sender);
		if (target == null)
			return null;
		return target.get();
	}

	/* ------------------------------------------------------------ */

	@Override
	public String getPrimaryAlias() {
		return "pm";
	}

	@Override
	public String getPermissionNode() {
		return "fe.chat.pm";
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.ALL;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return baseBuilder
				.then(Commands.argument("message-or-target", StringArgumentType.greedyString())
						.executes(CommandContext -> execute(CommandContext, "message")))
				.then(Commands.literal("clear").executes(CommandContext -> execute(CommandContext, "clear")))
				.executes(CommandContext -> execute(CommandContext, "get"));
	}

	@Override
	public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		if (params.equals("clear")) {
			clearTarget(getServerPlayer(ctx.getSource()));
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared PM target");
			return Command.SINGLE_SUCCESS;
		}
		PlayerEntity target = getTarget(getServerPlayer(ctx.getSource()));
		if (params.equals("get")) {
			if (target != null) {
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "Current PM target is %s",
						target.getDisplayName().getString());
				return Command.SINGLE_SUCCESS;
			}
			ChatOutputHandler.chatWarning(ctx.getSource(), "You don't have a PM target set");
			return Command.SINGLE_SUCCESS;
		}
		if (target == null) {
			String[] name = StringArgumentType.getString(ctx, "message-or-target").split(" ");
			if (name.length != 1) {
				ChatOutputHandler.chatError(ctx.getSource(), "You must first select a target with /pm <player>");
				return Command.SINGLE_SUCCESS;
			}
			UserIdent player;
			try {
				player = parsePlayer(name[0], null, true, true);
			} catch (FECommandParsingException e) {
				ChatOutputHandler.chatError(ctx.getSource(), e.error);
				return Command.SINGLE_SUCCESS;
			}
			if (getServerPlayer(ctx.getSource()) == player.getPlayer()) {
				ChatOutputHandler.chatError(ctx.getSource(), "Cant send a pm to yourself");
				return Command.SINGLE_SUCCESS;
			}
			setTarget(getServerPlayer(ctx.getSource()), player.getPlayer());
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set PM target to %s", player.getUsernameOrUuid());
			return Command.SINGLE_SUCCESS;
		} else {
			TextComponent message = new StringTextComponent(StringArgumentType.getString(ctx, "message-or-target"));
			ModuleChat.tell(ctx.getSource(), message, target.createCommandSourceStack());
			return Command.SINGLE_SUCCESS;
		}
	}
}
