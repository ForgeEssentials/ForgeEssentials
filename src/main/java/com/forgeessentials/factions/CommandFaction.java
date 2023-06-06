package com.forgeessentials.factions;
/*
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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

public class CommandFaction extends ForgeEssentialsCommandBuilder
{

    public CommandFaction(boolean enabled)
    {
        super(enabled);
    }

    public static final String MSG_FACTION_REQUIRED = "You need to be in a faction to use this command";
    public static final String MSG_UNKNOWN_FACTION = "Faction %s does not exist";
    public static final String MSG_FACTION_EXISTS = "Faction %s already exists";
    public static final String MSG_JOINED_FACTION = "Joined faction \"%s\"";
    public static final String MSG_LEFT_FACTION = "Left faction \"%s\"";

    @Override
    public String getPrimaryAlias()
    {
        return "faction";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "f", "factions" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleFactions.PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("list")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("create")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("join")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("leave")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("invite")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("ally")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("unally")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("members")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("ff")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("bonus")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .then(Commands.literal("delete")
                        .executes(CommandContext -> execute(CommandContext, "single-")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            if (hasPermission(ctx.getSource(),ModuleFactions.PERM_LIST)) {
            	ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction list");
            }
            return Command.SINGLE_SUCCESS;
        }
        String faction = null;
        if (getServerPlayer(ctx.getSource()) != null)
        {
            List<String> factions = ModuleFactions.getFaction(getIdent(ctx.getSource()));
            if (!factions.isEmpty())
                faction = factions.get(0);
        }

        if (arguments.hasPermission(ModuleFactions.PERM_ADMIN))
            arguments.tabCompleteWord("id");
        if (arguments.peek().equalsIgnoreCase("id"))
        {
            arguments.remove();
            if (arguments.isEmpty())
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            arguments.tabComplete(ModuleFactions.getFactions());
            faction = arguments.remove();
            if (arguments.isEmpty())
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
            if (!ModuleFactions.isFaction(faction))
                throw new TranslatedCommandException(MSG_UNKNOWN_FACTION, faction);
            if (!ModuleFactions.isInFaction(arguments.ident, faction))
                if (!arguments.hasPermission(ModuleFactions.PERM_ADMIN))
                    throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        }

        if (arguments.hasPermission(ModuleFactions.PERM_INVITE))
            arguments.tabCompleteWord("invite");
        if (arguments.hasPermission(ModuleFactions.PERM_ALLY))
        {
            arguments.tabCompleteWord("ally");
            arguments.tabCompleteWord("unally");
        }
        if (arguments.hasPermission(ModuleFactions.PERM_BONUS))
            arguments.tabCompleteWord("bonus");
        if (arguments.hasPermission(ModuleFactions.PERM_FF))
            arguments.tabCompleteWord("ff");
        if (arguments.hasPermission(ModuleFactions.PERM_DELETE))
            arguments.tabCompleteWord("delete");
        arguments.tabComplete("list", "create", "join", "leave", "members");
        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "list":
            parseList(ctx, params);
            break;
        case "create":
            parseCreate(ctx, params);
            break;
        case "join":
            parseJoin(ctx, params);
            break;
        case "leave":
            parseLeave(ctx, params, faction);
            break;
        case "invite":
            parseInvite(ctx, params, faction);
            break;
        case "ally":
            parseAlly(ctx, params, faction, true);
            break;
        case "unally":
            parseAlly(ctx, params, faction, false);
            break;
        case "members":
            parseMembers(ctx, params, faction);
            break;
        case "ff":
            parseFrindlyFire(ctx, params, faction);
            break;
        case "bonus":
            parseBonus(ctx, params, faction);
            break;
        case "delete":
            if (faction == null) {
            	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
            	return Command.SINGLE_SUCCESS;
            }
            parseDelete(ctx, params, faction);
            break;
        default:
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subcmd);
        	return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseList(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
    	if(!hasPermission(ctx.getSource(),ModuleFactions.PERM_LIST)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        // TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
    }

    public static void parseCreate(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_CREATE)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty()) {
            subCommandHelp(ctx, "create <id> [name...]: Create a new faction");
            return;
        }

        String faction = arguments.remove();
        String name = arguments.toString();
        if (name.isEmpty())
            name = faction;

        if (ModuleFactions.isFaction(faction)) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_EXISTS, faction);
        	return;
        }
        if (getServerPlayer(ctx.getSource()) != null)
        {
            if (!ModuleFactions.getFaction(getIdent(ctx.getSource())).isEmpty()) {
            	ChatOutputHandler.chatError(ctx.getSource(), "You are already in a faction!");
            	return;
            }
            APIRegistry.perms.getServerZone().addPlayerToGroup(getIdent(ctx.getSource()), ModuleFactions.getFactionGroup(faction));
            ModuleFactions.setRank(getIdent(ctx.getSource()), ModuleFactions.RANK_OWNER);
        }
        ModuleFactions.setFactionName(faction, name);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Created faction [%s] \"%s\"", faction, name);
    }

    public static void parseJoin(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
    	if (getServerPlayer(ctx.getSource()) != null)
        {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
        	return;
        }
    	
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_JOIN)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty()) {
            subCommandHelp(ctx, "join <faction>: Request to join a faction");
            return;
        }

        arguments.tabComplete(ModuleFactions.getFactions());
        final String faction = arguments.remove();
        if (!ModuleFactions.isFaction(faction)) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_UNKNOWN_FACTION, faction);
        	return;
        }

        // Check if the player is allowed to join
        boolean locked = ModuleFactions.isLockedFaction(faction);
        if (locked && hasPermission(ctx.getSource(), ModuleFactions.PERM_JOIN_ANY))
            locked = false;

        if (!locked)
        {
            APIRegistry.perms.getServerZone().addPlayerToGroup(getIdent(ctx.getSource()), ModuleFactions.getFactionGroup(faction));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), MSG_JOINED_FACTION, ModuleFactions.getFactionName(faction));
            return;
        }

        String message = Translator.format("Allow %s to join the faction %s?", getIdent(ctx.getSource()).getUsernameOrUuid(), faction);
        QuestionerCallback callback = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                	ChatOutputHandler.chatError(ctx.getSource(), "Join request timed out");
                else if (!response)
                	ChatOutputHandler.chatError(ctx.getSource(), "Join request denied");
                else
                {
                    APIRegistry.perms.getServerZone().addPlayerToGroup(getIdent(ctx.getSource()), ModuleFactions.getFactionGroup(faction));
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), MSG_JOINED_FACTION, ModuleFactions.getFactionName(faction));
                }
            }
        };

        for (ServerPlayerEntity player : ServerUtil.getPlayerList())
        {
            UserIdent playerIdent = UserIdent.get(player);
            if (ModuleFactions.isInFaction(playerIdent, faction) && playerIdent.checkPermission(ModuleFactions.PERM_INVITE))
            {
                try
                {
                    Questioner.add(player, message, callback);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Requested %s to accept your join request", player.getDisplayName().getString());
                    return;
                }
                catch (QuestionerStillActiveException e)
                {
                }
            }
        }
        ChatOutputHandler.chatError(ctx.getSource(), "No player found to accept join request");
    }

    public static void parseLeave(CommandContext<CommandSource> ctx, String params, String faction) throws CommandException
    {
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
        APIRegistry.perms.getServerZone().removePlayerFromGroup(getIdent(ctx.getSource()), ModuleFactions.getFactionGroup(faction));
        ChatOutputHandler.chatConfirmation(ctx.getSource(), MSG_LEFT_FACTION, ModuleFactions.getFactionName(faction));
    }

    public static void parseInvite(CommandContext<CommandSource> ctx, String params, String faction) throws CommandException
    {
        if (faction == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
        	return;
        }
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_INVITE)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty()) {
            subCommandHelp(ctx, "invite <player>: Invite a player to your faction");
            return;
        }

        // TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
    }

    public static void parseAlly(CommandContext<CommandSource> ctx, String params, String faction, boolean ally) throws CommandException
    {
        if (faction == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
        	return;
        }
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_ALLY)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty()) {
            subCommandHelp(ctx, (ally ? "ally" : "unally") + " <faction>: Ally with other factions");
            return;
        }

        // TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
    }

    public static void parseMembers(CommandContext<CommandSource> ctx, String params, String faction) throws CommandException
    {
        if (faction == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
        	return;
        }
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_MEMBERS)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty())
        {
        	ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction members kick|promote|deomote");
            if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_MEMBERS_ADD)) {
            	ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction members add|owner");
            	return;
            }
            return;
        }

        // TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
    }

    public static void parseFrindlyFire(CommandContext<CommandSource> ctx, String params, String faction) throws CommandException
    {
        if (faction == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
        	return;
        }
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_FF)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty()) {
            subCommandHelp(ctx, "ff on|off: Toggle in-faction PvP");
            return;
        }

        // TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
    }

    public static void parseBonus(CommandContext<CommandSource> ctx, String params, String faction) throws CommandException
    {
        if (faction == null) {
        	ChatOutputHandler.chatError(ctx.getSource(), MSG_FACTION_REQUIRED);
        	return;
        }
        if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_BONUS)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }
        if (arguments.isEmpty()) {
            subCommandHelp(ctx, "bonus <id> <duration>: Control faction bonuses");
            return;
        }

        // TODO
		ChatOutputHandler.chatError(ctx.getSource(), "Not yet implemented");
    }

    public static void parseDelete(CommandContext<CommandSource> ctx, String params, final String faction) throws CommandException
    {
    	if (!hasPermission(ctx.getSource(), ModuleFactions.PERM_DELETE)) {
        	ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
        	return;
        }

        QuestionerCallback callback = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                {
                    return;
                }
                else if (!response)
                {

                }
                String factionGroup = ModuleFactions.getFactionGroup(faction);
                for (Entry<UserIdent, Set<String>> player : APIRegistry.perms.getServerZone().getPlayerGroups().entrySet())
                    for (Iterator<String> it = player.getValue().iterator(); it.hasNext();)
                        if (factionGroup.equals(it.next()))
                            it.remove();
                for (Entry<UserIdent, Set<String>> player : APIRegistry.perms.getServerZone().getPlayerGroups().entrySet())
                {
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
        try
        {
            Questioner.add(arguments.sender, "Really delete faction %s?", callback);
        }
        catch (QuestionerStillActiveException e)
        {
        	ChatOutputHandler.chatError(ctx.getSource(), Questioner.MSG_STILL_ACTIVE);
        }
    }

    public static void subCommandHelp(CommandContext<CommandSource> ctx, String msg) throws CommandException
    {
    	ChatOutputHandler.chatConfirmation(ctx.getSource(), "/faction " + msg);
    	return;
    }
}*/
