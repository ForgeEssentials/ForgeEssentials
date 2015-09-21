package com.forgeessentials.permissions.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPromote extends ParserCommandBase
{

    public static final String PERM_NODE = "fe.perm.promote";

    @Override
    public String getCommandName()
    {
        return "promote";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/promote <player> <group>: Promote a user to another group";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_NODE;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(arguments.sender, "/promote <player> <group>");
            return;
        }

        UserIdent ident = arguments.parsePlayer(false, false);
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Wrong syntax. Use \"/promote <player> <group>\"");

        if (arguments.isTabCompletion)
        {
            if (arguments.args.size() == 1)
            {
                arguments.tabCompletion = new ArrayList<String>();
                for (String group : APIRegistry.perms.getServerZone().getGroups())
                    if (CommandBase.doesStringStartWith(arguments.args.peek(), group))
                        arguments.tabCompletion.add(group);
            }
            return;
        }

        String groupName = arguments.remove();
        if (!arguments.isEmpty())
            throw new TranslatedCommandException("Wrong syntax. Use Syntax is \"/promote <player> <group>\"");

        if (!APIRegistry.perms.groupExists(groupName))
            throw new TranslatedCommandException("Group %s does not exist", groupName);

        if (!Zone.PERMISSION_TRUE.equals(APIRegistry.perms.getServerZone().getGroupPermission(groupName, FEPermissions.GROUP_PROMOTION)))
            throw new TranslatedCommandException("Group %s is not available for promotion. Allow %s on the group first.", groupName,
                    FEPermissions.GROUP_PROMOTION);

        for (GroupEntry group : APIRegistry.perms.getServerZone().getStoredPlayerGroupEntries(ident))
            if (!Zone.PERMISSION_TRUE.equals(APIRegistry.perms.getServerZone().getGroupPermission(group.getGroup(), FEPermissions.GROUP_PROMOTION)))
            {
                APIRegistry.perms.removePlayerFromGroup(ident, group.getGroup());
                ChatOutputHandler.chatConfirmation(arguments.sender, Translator.format("Removed %s from group %s", ident.getUsernameOrUuid(), group));
                if (ident.hasPlayer())
                    ChatOutputHandler.chatConfirmation(ident.getPlayer(), Translator.format("You have been removed from the %s group", group));
            }
        APIRegistry.perms.addPlayerToGroup(ident, groupName);
        ChatOutputHandler.chatConfirmation(arguments.sender, Translator.format("Added %s to group %s", ident.getUsernameOrUuid(), groupName));
        if (ident.hasPlayer())
            ChatOutputHandler.chatConfirmation(ident.getPlayer(), Translator.format("You have been added to the %s group", groupName));
    }

}
