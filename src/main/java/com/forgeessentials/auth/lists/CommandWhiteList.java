package com.forgeessentials.auth.lists;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.auth.AuthEventHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandWhiteList extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {

        return "whitelist";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (!AuthEventHandler.whitelist)
        {
            ChatOutputHandler.chatWarning(sender, "The whitelist is not enabled. You can enable it in server.properties or your auth config file.");
            ChatOutputHandler.chatWarning(sender, "Note that server.properties will take precedent over the auth config.");

        }

        else if (args.length == 1 && args[0].equalsIgnoreCase("toggle"))
        {
            if (AuthEventHandler.whitelist)
            {
                AuthEventHandler.whitelist = false;
                ChatOutputHandler.chatConfirmation(sender, "FE Whitelist was on, it is now turned off.");
            }
            else
            {
                AuthEventHandler.whitelist = true;
                ChatOutputHandler.chatConfirmation(sender, "FE Whitelist was off, it is now turned on.");
            }
        }

        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                APIRegistry.perms.getServerZone().setPlayerPermission(UserIdent.get(args[1], sender), "fe.auth.whitelist", true);
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                APIRegistry.perms.getServerZone().setPlayerPermission(UserIdent.get(args[1], sender), "fe.auth.whitelist", false);
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {

        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.auth.whitelist.admin";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/whitelist ";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }

}
