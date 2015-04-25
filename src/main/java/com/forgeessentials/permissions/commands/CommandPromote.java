package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;

public class CommandPromote extends ForgeEssentialsCommandBase {

    public static final String PERM_NODE = "fe.perm.promote";

    @Override
    public String getCommandName()
    {
        return "promote";
    }

    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            OutputHandler.chatConfirmation(arguments.sender, "/promote <player> <group>");
            return;
        }

        UserIdent ident = arguments.parsePlayer();
        if (ident == null)
            return;

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
            throw new TranslatedCommandException("Group %s is not available for promotion. Allow %s on the group first.", groupName, FEPermissions.GROUP_PROMOTION);

        for (GroupEntry group : APIRegistry.perms.getServerZone().getStoredPlayerGroups(ident))
            if (!Zone.PERMISSION_TRUE.equals(APIRegistry.perms.getServerZone().getGroupPermission(group.getGroup(), FEPermissions.GROUP_PROMOTION)))
            {
                APIRegistry.perms.removePlayerFromGroup(ident, group.getGroup());
                OutputHandler.chatConfirmation(arguments.sender, Translator.format("Removed %s from group %s", ident.getUsernameOrUUID(), group));
                if (ident.hasPlayer())
                    OutputHandler.chatConfirmation(ident.getPlayer(), Translator.format("You have been removed from the %s group", group));
            }
        APIRegistry.perms.addPlayerToGroup(ident, groupName);
        OutputHandler.chatConfirmation(arguments.sender, Translator.format("Added %s to group %s", ident.getUsernameOrUUID(), groupName));
        if (ident.hasPlayer())
            OutputHandler.chatConfirmation(ident.getPlayer(), Translator.format("You have been added to the %s group", groupName));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        parse(new CommandParserArgs(this, args, sender));
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_NODE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
        parse(arguments);
        return arguments.tabCompletion;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/promote <player> <group>: Promote a user to another group";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
