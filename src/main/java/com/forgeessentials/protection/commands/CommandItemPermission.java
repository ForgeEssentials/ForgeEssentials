package com.forgeessentials.protection.commands;

import static com.forgeessentials.util.ServerUtil.getItemPermission;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandParserArgs;

public class CommandItemPermission extends ParserCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "itemperm";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/itemperm [break|place|inventory|exist] [allow|deny|clear]: Show / control item permissions";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.cmd.itemperm";
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
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        ItemStack stack = arguments.senderPlayer.getHeldItemMainhand();

        if (arguments.isEmpty())
        {
            if (stack == ItemStack.EMPTY)
                throw new TranslatedCommandException("No item equipped!");
            arguments.notify(getItemPermission(stack));
            return;
        }

        List<String> types = Arrays.asList("break", "place", "inventory", "exist");
        arguments.tabComplete(types);
        String type = arguments.remove().toLowerCase();
        if (!types.contains(type))
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, type);

        Boolean value;
        if (!arguments.isEmpty())
        {
            arguments.tabComplete("allow", "deny", "clear");
            switch (arguments.remove().toLowerCase())
            {
            case "allow":
                value = true;
                break;
            case "deny":
                value = false;
                break;
            case "clear":
                value = null;
                break;
            default:
                throw new TranslatedCommandException("Need to specify allow, deny or clear");
            }
        }
        else
            value = false;

        if (stack == ItemStack.EMPTY)
            throw new TranslatedCommandException("No item equipped!");

        String permStart = ModuleProtection.BASE_PERM + '.';
        String permEnd;
        if (!arguments.isEmpty())
        {
            arguments.tabComplete("all", "*");
            String arg = arguments.remove();
            if (!arg.equalsIgnoreCase("all") && !arg.equalsIgnoreCase("*"))
                throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, arg);
            permEnd = '.' + getItemPermission(stack, false) + ".*";
        }
        else
        {
            permEnd = '.' + getItemPermission(stack, true);
        }

        if (arguments.isTabCompletion)
            return;

        if (value == null)
            APIRegistry.perms.getServerZone().clearGroupPermission(Zone.GROUP_DEFAULT, permStart + type + permEnd);
        else
            APIRegistry.perms.getServerZone().setGroupPermission(Zone.GROUP_DEFAULT, permStart + type + permEnd, value);
        arguments.confirm(value == null ? "Cleared [%s] for item %s" : //
                (value ? "Allowed [%s] for item %s" : "Denied [%s] for item %s"), type, getItemPermission(stack, false));
    }
}
