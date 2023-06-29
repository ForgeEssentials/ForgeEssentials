package com.forgeessentials.factions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerException.QuestionerStillActiveException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandFaction extends ForgeEssentialsCommandBuilder {

	public CommandFaction(boolean enabled) {
		super(enabled);
	}

	public static final String MSG_FACTION_REQUIRED = "You need to be in a faction to use this command";
	public static final String MSG_UNKNOWN_FACTION = "Faction %s does not exist";
	public static final String MSG_FACTION_EXISTS = "Faction %s already exists";
	public static final String MSG_JOINED_FACTION = "Joined faction \"%s\"";
	public static final String MSG_LEFT_FACTION = "Left faction \"%s\"";

	@Override
	public String getPrimaryAlias() {
		return "faction";
	}

	@Override
	public String[] getDefaultSecondaryAliases() {
		return new String[] { "f", "factions" };
	}

	@Override
	public String getPermissionNode() {
		return ModuleFactions.PERM;
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
		return baseBuilder.then(Commands.literal("list").executes(CommandContext -> execute(CommandContext, "list")))
				.then(Commands.literal("create")
						.then(Commands.argument("faction", StringArgumentType.word())
								.then(Commands.argument("name", StringArgumentType.word())
										.executes(context -> execute(context, "create-name")))
								.executes(context -> execute(context, "create-faction")))
						.executes(CommandContext -> execute(CommandContext, "create-help")))
				.then(Commands.literal("join")
						.then(Commands.argument("faction", StringArgumentType.word()).suggests(SUGGEST_FACTIONS)
								.executes(context -> execute(context, "join")))
						.executes(CommandContext -> execute(CommandContext, "join-help")))
				.then(Commands.literal("leave").executes(CommandContext -> execute(CommandContext, "leave")))
				.then(Commands.literal("invite").executes(CommandContext -> execute(CommandContext, "invite-help")))
				.then(Commands.literal("ally").executes(CommandContext -> execute(CommandContext, "ally-help")))
				.then(Commands.literal("unally").executes(CommandContext -> execute(CommandContext, "unally-help")))
				.then(Commands.literal("members").executes(CommandContext -> execute(CommandContext, "members-help")))
				.then(Commands.literal("ff").executes(CommandContext -> execute(CommandContext, "ff-help")))
				.then(Commands.literal("bonus").executes(CommandContext -> execute(CommandContext, "bonus-help")))
				.then(Commands.literal("delete").executes(CommandContext -> execute(CommandContext, "delete")))
				.then(Commands.literal("admin")
						.then(Commands.argument("faction", StringArgumentType.word()).suggests(SUGGEST_FACTIONS)
								.then(Commands.literal("invite")
										.executes(CommandContext -> execute(CommandContext, "admin-invite-help")))
								.then(Commands.literal("ally")
										.executes(CommandContext -> execute(CommandContext, "admin-ally-help")))
								.then(Commands.literal("unally")
										.executes(CommandContext -> execute(CommandContext, "admin-unally-help")))
								.then(Commands.literal("members")
										.executes(CommandContext -> execute(CommandContext, "admin-members-help")))
								.then(Commands.literal("ff")
										.executes(CommandContext -> execute(CommandContext, "admin-ff-help")))
								.then(Commands.literal("bonus")
										.executes(CommandContext -> execute(CommandContext, "admin-bonus-help")))
								.then(Commands.literal("delete")
										.executes(CommandContext -> execute(CommandContext, "admin-delete")))))
				.executes(CommandContext -> execute(CommandContext, "help"));
	}

	public static final SuggestionProvider<CommandSource> SUGGEST_FACTIONS = (ctx, builder) -> {
		List<String> factions = new ArrayList<>();
		for (String f : ModuleFactions.getFactions()) {
			factions.add(f);
		}
		return ISuggestionProvider.suggest(factions, builder);
	};

	@Override
	public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		if (params.equals("help")) {
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction create <factionName> <optionalName>");
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction join <factionName>");
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction leave");
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction members");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_LIST))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction list");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_INVITE))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction invite <player>");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_ALLY)) {
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction ally <faction>");
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction unally <faction>");
			}
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_BONUS))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction bonus");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_FF))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction ff");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_DELETE))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction delete");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_ADMIN))
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction admin");
			return Command.SINGLE_SUCCESS;
		}
		String faction = null;
		if (getServerPlayer(ctx.getSource()) != null) {
			List<String> factions = ModuleFactions.getFaction(getIdent(ctx.getSource()));
			if (!factions.isEmpty())
				faction = factions.get(0);
		}

		if (params.startsWith("admin")) {
			params = params.substring(6);
			faction = StringArgumentType.getString(ctx, "faction");
			if (!ModuleFactions.isFaction(faction)) {
				ChatOutputHandler.chatError(ctx.getSource(), MSG_UNKNOWN_FACTION, faction);
				return Command.SINGLE_SUCCESS;
			}
			if (!ModuleFactions.isInFaction(getIdent(ctx.getSource()), faction)) {
				if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_ADMIN)) {
					ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
					return Command.SINGLE_SUCCESS;
				}
			}
		}

		switch (params) {
		case "list":
			parseList(ctx, params);
			break;
		case "create-help":
		case "create-faction":
		case "create-name":
			parseCreate(ctx, params);
			break;
		case "join":
		case "join-help":
			parseJoin(ctx, params);
			break;
		case "leave":
			parseLeave(ctx, faction);
			break;
		case "invite-help":
			parseInvite(ctx, params, faction);
			break;
		case "ally-help":
			parseAlly(ctx, params, faction, true);
			break;
		case "unally-help":
			parseAlly(ctx, params, faction, false);
			break;
		case "members-help":
			parseMembers(ctx, params, faction);
			break;
		case "ff-help":
			parseFrindlyFire(ctx, params, faction);
			break;
		case "bonus-help":
			parseBonus(ctx, params, faction);
			break;
		case "delete":
			if (faction == null) {
				ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
				return Command.SINGLE_SUCCESS;
			}
			parseDelete(ctx, faction);
			break;
		default:
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params);
			return Command.SINGLE_SUCCESS;
		}
		return Command.SINGLE_SUCCESS;
	}

	public static void parseList(CommandContext<CommandSource> ctx, String params) throws CommandException {
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_LIST)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}

		// TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
	}

	public static void parseCreate(CommandContext<CommandSource> ctx, String params) throws CommandException {
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_CREATE)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("create-help")) {
			subCommandHelp(ctx, "create <id> [name...]: Create a new faction");
			return;
		}

		String faction = StringArgumentType.getString(ctx, "faction");
		String name;
		if (params.equals("create-name")) {
			name = StringArgumentType.getString(ctx, "name");
		} else {
			name = faction;
		}

		if (ModuleFactions.isFaction(faction)) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_EXISTS, faction);
			return;
		}
		if (getServerPlayer(ctx.getSource()) != null) {
			if (!ModuleFactions.getFaction(getIdent(ctx.getSource())).isEmpty()) {
				ChatOutputHandler.chatError(ctx.getSource(), "You are already in a faction!");
				return;
			}
			APIRegistry.perms.getServerZone().addPlayerToGroup(getIdent(ctx.getSource()),
					ModuleFactions.getFactionGroup(faction));
			ModuleFactions.setRank(getIdent(ctx.getSource()), ModuleFactions.RANK_OWNER);
		}
		ModuleFactions.setFactionName(faction, name);
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "Created faction [%s] \"%s\"", faction, name);
	}

	public static void parseJoin(CommandContext<CommandSource> ctx, String params) throws CommandException {
		if (getServerPlayer(ctx.getSource()) == null) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_JOIN)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("join-help")) {
			subCommandHelp(ctx, "join <faction>: Request to join a faction");
			return;
		}

		final String faction = StringArgumentType.getString(ctx, "faction");
		if (!ModuleFactions.isFaction(faction)) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_UNKNOWN_FACTION, faction);
			return;
		}

		// Check if the player is allowed to join
		boolean locked = ModuleFactions.isLockedFaction(faction);
		if (locked && hasPermission(ctx.getSource(), ModuleFactions.PERM_JOIN_ANY))
			locked = false;

		if (!locked) {
			APIRegistry.perms.getServerZone().addPlayerToGroup(getIdent(ctx.getSource()),
					ModuleFactions.getFactionGroup(faction));
			ChatOutputHandler.chatConfirmation(ctx.getSource(), MSG_JOINED_FACTION,
					ModuleFactions.getFactionName(faction));
			return;
		}

		String message = Translator.format("Allow %s to join the faction %s?",
				getIdent(ctx.getSource()).getUsernameOrUuid(), faction);
		QuestionerCallback callback = new QuestionerCallback() {
			@Override
			public void respond(Boolean response) {
				if (response == null)
					ChatOutputHandler.chatError(ctx.getSource(), "Join request timed out");
				else if (!response)
					ChatOutputHandler.chatError(ctx.getSource(), "Join request denied");
				else {
					APIRegistry.perms.getServerZone().addPlayerToGroup(getIdent(ctx.getSource()),
							ModuleFactions.getFactionGroup(faction));
					ChatOutputHandler.chatConfirmation(ctx.getSource(), MSG_JOINED_FACTION,
							ModuleFactions.getFactionName(faction));
				}
			}
		};

		for (ServerPlayerEntity player : ServerUtil.getPlayerList()) {
			UserIdent playerIdent = UserIdent.get(player);
			if (ModuleFactions.isInFaction(playerIdent, faction)
					&& playerIdent.checkPermission(ModuleFactions.PERM_INVITE)) {
				try {
					Questioner.add(player, message, callback);
					ChatOutputHandler.chatConfirmation(ctx.getSource(), "Requested %s to accept your join request",
							player.getDisplayName().getString());
					return;
				} catch (QuestionerStillActiveException e) {
					ChatOutputHandler.chatError(ctx.getSource(),
							"Cannot run command because player is still answering a question. Please wait a moment");
					return;
				}
			}
		}
		ChatOutputHandler.chatError(ctx.getSource(), "No player found to accept join request");
	}

	public static void parseLeave(CommandContext<CommandSource> ctx, String faction) throws CommandException {
		if (faction == null) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
			return;
		}
		if (ModuleFactions.hasFactionRank(getIdent(ctx.getSource()), ModuleFactions.RANK_OWNER)) {
			ChatOutputHandler.chatError(ctx.getSource(), "Owners cannot leave factions (use delete instead)");
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_LEAVE)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		APIRegistry.perms.getServerZone().removePlayerFromGroup(getIdent(ctx.getSource()),
				ModuleFactions.getFactionGroup(faction));
		ChatOutputHandler.chatConfirmation(ctx.getSource(), MSG_LEFT_FACTION, ModuleFactions.getFactionName(faction));
	}

	public static void parseInvite(CommandContext<CommandSource> ctx, String params, String faction)
			throws CommandException {
		if (faction == null) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_INVITE)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("invite-help")) {
			subCommandHelp(ctx, "invite <player>: Invite a player to your faction");
			// return;
		}

		// TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
	}

	public static void parseAlly(CommandContext<CommandSource> ctx, String params, String faction, boolean ally)
			throws CommandException {
		if (faction == null) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_ALLY)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("ally-help") || params.equals("unally-help")) {
			subCommandHelp(ctx, (ally ? "ally" : "unally") + " <faction>: Ally with other factions");
			// return;
		}

		// TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
	}

	public static void parseMembers(CommandContext<CommandSource> ctx, String params, String faction)
			throws CommandException {
		if (faction == null) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_MEMBERS)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("members-help")) {
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction members kick|promote|deomote");
			if (hasPermission(ctx.getSource(), ModuleFactions.PERM_MEMBERS_ADD)) {
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction members add|owner");
			}
			// return;
		}

		// TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
	}

	public static void parseFrindlyFire(CommandContext<CommandSource> ctx, String params, String faction)
			throws CommandException {
		if (faction == null) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_FF)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("ff-help")) {
			subCommandHelp(ctx, "ff on|off: Toggle in-faction PvP");
			// return;
		}

		// TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
	}

	public static void parseBonus(CommandContext<CommandSource> ctx, String params, String faction)
			throws CommandException {
		if (faction == null) {
			ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
			return;
		}
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_BONUS)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (params.equals("bonus-help")) {
			subCommandHelp(ctx, "bonus <id> <duration>: Control faction bonuses");
			// return;
		}

		// TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
	}

	public static void parseDelete(CommandContext<CommandSource> ctx, final String faction) throws CommandException {
		if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_DELETE)) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
			return;
		}
		if (getServerPlayer(ctx.getSource()) == null) {
			ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
			return;
		}
		QuestionerCallback callback = new QuestionerCallback() {
			@Override
			public void respond(Boolean response) {
				if (response == null) {
					return;
				} else if (!response) {

				}
				String factionGroup = ModuleFactions.getFactionGroup(faction);
				for (Entry<UserIdent, Set<String>> player : APIRegistry.perms.getServerZone().getPlayerGroups()
						.entrySet())
					for (Iterator<String> it = player.getValue().iterator(); it.hasNext();)
						if (factionGroup.equals(it.next()))
							it.remove();
				for (Entry<UserIdent, Set<String>> player : APIRegistry.perms.getServerZone().getPlayerGroups()
						.entrySet()) {
					if (player.getValue().remove(factionGroup) && player.getKey().hasPlayer())
						ChatOutputHandler.chatNotification(player.getKey().getPlayer().createCommandSourceStack(),
								Translator.format("Faction %s has been deleted", faction));
					for (Iterator<String> it = player.getValue().iterator(); it.hasNext();)
						if (it.next().startsWith(ModuleFactions.RANK_PREFIX))
							it.remove();
				}
				APIRegistry.perms.getServerZone().getGroupPermissions().remove(factionGroup);
				ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted faction %s", faction);
			}
		};
		try {
			Questioner.add(getServerPlayer(ctx.getSource()), "Really delete faction %s?", callback);
		} catch (QuestionerStillActiveException e) {
			ChatOutputHandler.chatError(ctx.getSource(),
					"Cannot run command because player is still answering a question. Please wait a moment");
			return;
		}
	}

	public static void subCommandHelp(CommandContext<CommandSource> ctx, String msg) throws CommandException {
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction " + msg);
		return;
	}
}
