package com.forgeessentials.factions;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.CommandParserArgs.CancelParsingException;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

public class CommandFaction extends ParserCommandBase
{

    public static final String MSG_FACTION_REQUIRED = "You need to be in a faction to use this command";
    public static final String MSG_UNKNOWN_FACTION = "Faction %s does not exist";
    public static final String MSG_FACTION_EXISTS = "Faction %s already exists";
    public static final String MSG_JOINED_FACTION = "Joined faction \"%s\"";
    public static final String MSG_LEFT_FACTION = "Left faction \"%s\"";

    @Override
    public String getCommandName()
    {
        return "fefaction";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "faction", "factions" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleFactions.PERM;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/faction: Manage factions";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(ModuleFactions.PERM_LIST))
                arguments.confirm("/faction list");
            return;
        }

        String faction = null;
        if (arguments.hasPlayer())
        {
            List<String> factions = ModuleFactions.getFaction(arguments.ident);
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
            parseList(arguments);
            break;
        case "create":
            parseCreate(arguments);
            break;
        case "join":
            parseJoin(arguments);
            break;
        case "leave":
            parseLeave(arguments, faction);
            break;
        case "invite":
            parseInvite(arguments, faction);
            break;
        case "ally":
            parseAlly(arguments, faction, true);
            break;
        case "unally":
            parseAlly(arguments, faction, false);
            break;
        case "members":
            parseMembers(arguments, faction);
            break;
        case "ff":
            parseFrindlyFire(arguments, faction);
            break;
        case "bonus":
            parseBonus(arguments, faction);
            break;
        case "delete":
            if (faction == null)
                throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
            parseDelete(arguments, faction);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subcmd);
        }
    }

    public static void parseList(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(ModuleFactions.PERM_LIST);
        if (arguments.isTabCompletion)
            return;

        // TODO
        throw new TranslatedCommandException("Not yet implemented");
    }

    public static void parseCreate(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isTabCompletion)
            return;
        arguments.checkPermission(ModuleFactions.PERM_CREATE);
        if (arguments.isEmpty())
            subCommandHelp(arguments, "create <id> [name...]: Create a new faction");

        String faction = arguments.remove();
        String name = arguments.toString();
        if (name.isEmpty())
            name = faction;

        if (ModuleFactions.isFaction(faction))
            throw new TranslatedCommandException(MSG_FACTION_EXISTS, faction);
        if (arguments.senderPlayer != null)
        {
            if (!ModuleFactions.getFaction(arguments.ident).isEmpty())
                throw new TranslatedCommandException("You are already in a faction!");
            APIRegistry.perms.getServerZone().addPlayerToGroup(arguments.ident, ModuleFactions.getFactionGroup(faction));
            ModuleFactions.setRank(arguments.ident, ModuleFactions.RANK_OWNER);
        }
        ModuleFactions.setFactionName(faction, name);
        arguments.confirm("Created faction [%s] \"%s\"", faction, name);
    }

    public static void parseJoin(final CommandParserArgs arguments) throws CommandException
    {
        arguments.needsPlayer();
        arguments.checkPermission(ModuleFactions.PERM_JOIN);
        if (arguments.isEmpty())
            subCommandHelp(arguments, "join <faction>: Request to join a faction");

        arguments.tabComplete(ModuleFactions.getFactions());
        final String faction = arguments.remove();
        if (!ModuleFactions.isFaction(faction))
            throw new TranslatedCommandException(MSG_UNKNOWN_FACTION, faction);
        if (arguments.isTabCompletion)
            return;

        // Check if the player is allowed to join
        boolean locked = ModuleFactions.isLockedFaction(faction);
        if (locked && arguments.hasPermission(ModuleFactions.PERM_JOIN_ANY))
            locked = false;

        if (!locked)
        {
            APIRegistry.perms.getServerZone().addPlayerToGroup(arguments.ident, ModuleFactions.getFactionGroup(faction));
            arguments.confirm(MSG_JOINED_FACTION, ModuleFactions.getFactionName(faction));
            return;
        }

        String message = Translator.format("Allow %s to join the faction %s?", arguments.ident.getUsernameOrUuid(), faction);
        QuestionerCallback callback = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                    arguments.error("Join request timed out");
                else if (!response)
                    arguments.error("Join request denied");
                else
                {
                    APIRegistry.perms.getServerZone().addPlayerToGroup(arguments.ident, ModuleFactions.getFactionGroup(faction));
                    arguments.confirm(MSG_JOINED_FACTION, ModuleFactions.getFactionName(faction));
                }
            }
        };

        for (EntityPlayerMP player : ServerUtil.getPlayerList())
        {
            UserIdent playerIdent = UserIdent.get(player);
            if (ModuleFactions.isInFaction(playerIdent, faction) && playerIdent.checkPermission(ModuleFactions.PERM_INVITE))
            {
                try
                {
                    Questioner.add(player, message, callback);
                    arguments.confirm("Requested %s to accept your join request", player.getName());
                    return;
                }
                catch (QuestionerStillActiveException e)
                {
                    /* do nothing */
                }
            }
        }
        arguments.error("No player found to accept join request");
    }

    public static void parseLeave(CommandParserArgs arguments, String faction) throws CommandException
    {
        if (arguments.isTabCompletion)
            return;
        if (faction == null)
            throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
        if (ModuleFactions.hasFactionRank(arguments.ident, ModuleFactions.RANK_OWNER))
            throw new TranslatedCommandException("Owners cannot leave factions (use delete instead)");
        arguments.checkPermission(ModuleFactions.PERM_LEAVE);
        APIRegistry.perms.getServerZone().removePlayerFromGroup(arguments.ident, ModuleFactions.getFactionGroup(faction));
        arguments.confirm(MSG_LEFT_FACTION, ModuleFactions.getFactionName(faction));
    }

    public static void parseInvite(CommandParserArgs arguments, String faction) throws CommandException
    {
        if (faction == null)
            throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
        arguments.checkPermission(ModuleFactions.PERM_INVITE);
        if (arguments.isEmpty())
            subCommandHelp(arguments, "invite <player>: Invite a player to your faction");

        if (arguments.isTabCompletion)
            return;

        // TODO
        throw new TranslatedCommandException("Not yet implemented");
    }

    public static void parseAlly(CommandParserArgs arguments, String faction, boolean ally) throws CommandException
    {
        if (faction == null)
            throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
        arguments.checkPermission(ModuleFactions.PERM_ALLY);
        if (arguments.isEmpty())
            subCommandHelp(arguments, (ally ? "ally" : "unally") + " <faction>: Ally with other factions");

        if (arguments.isTabCompletion)
            return;

        // TODO
        throw new TranslatedCommandException("Not yet implemented");
    }

    public static void parseMembers(CommandParserArgs arguments, String faction) throws CommandException
    {
        if (faction == null)
            throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
        arguments.checkPermission(ModuleFactions.PERM_MEMBERS);
        if (arguments.isEmpty())
        {
            arguments.confirm("/faction members kick|promote|deomote");
            if (arguments.hasPermission(ModuleFactions.PERM_MEMBERS_ADD))
                arguments.confirm("/faction members add|owner");
            return;
        }

        if (arguments.isTabCompletion)
            return;

        // TODO
        throw new TranslatedCommandException("Not yet implemented");
    }

    public static void parseFrindlyFire(CommandParserArgs arguments, String faction) throws CommandException
    {
        if (faction == null)
            throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
        arguments.checkPermission(ModuleFactions.PERM_FF);
        if (arguments.isEmpty())
            subCommandHelp(arguments, "ff on|off: Toggle in-faction PvP");

        if (arguments.isTabCompletion)
            return;

        // TODO
        throw new TranslatedCommandException("Not yet implemented");
    }

    public static void parseBonus(CommandParserArgs arguments, String faction) throws CommandException
    {
        if (faction == null)
            throw new TranslatedCommandException(MSG_FACTION_REQUIRED);
        arguments.checkPermission(ModuleFactions.PERM_BONUS);
        if (arguments.isEmpty())
            subCommandHelp(arguments, "bonus <id> <duration>: Control faction bonuses");

        if (arguments.isTabCompletion)
            return;

        // TODO
        throw new TranslatedCommandException("Not yet implemented");
    }

    public static void parseDelete(final CommandParserArgs arguments, final String faction) throws CommandException
    {
        if (arguments.isTabCompletion)
            return;
        arguments.checkPermission(ModuleFactions.PERM_DELETE);

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
                        ChatOutputHandler.chatNotification(player.getKey().getPlayer(), Translator.format("Faction %s has been deleted", faction));
                    for (Iterator<String> it = player.getValue().iterator(); it.hasNext();)
                        if (it.next().startsWith(ModuleFactions.RANK_PREFIX))
                            it.remove();
                }
                APIRegistry.perms.getServerZone().getGroupPermissions().remove(factionGroup);
                arguments.confirm("Deleted faction %s", faction);
            }
        };
        try
        {
            Questioner.add(arguments.sender, "Really delete faction %s?", callback);
        }
        catch (QuestionerStillActiveException e)
        {
            arguments.error(Questioner.MSG_STILL_ACTIVE);
        }
    }

    public static void subCommandHelp(CommandParserArgs arguments, String msg) throws CommandException
    {
        arguments.confirm("/faction " + msg);
        throw new CancelParsingException();
    }

}
