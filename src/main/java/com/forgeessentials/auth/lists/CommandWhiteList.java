package com.forgeessentials.auth.lists;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.auth.AuthEventHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;

public class CommandWhiteList extends ForgeEssentialsCommandBase {

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
            ChatUtils.sendMessage(sender, "The whitelist is not enabled. You can enable it in server.properties or your auth config file.");
            ChatUtils.sendMessage(sender, "Note that server.properties will take precedent over the auth config.");

        }

        else if (args.length == 1 && args[0].equalsIgnoreCase("toggle"))
        {
            if (AuthEventHandler.whitelist)
            {
                AuthEventHandler.whitelist = false;
                ChatUtils.sendMessage(sender, "FE Whitelist was on, it is now turned off.");
            }
            else
            {
                AuthEventHandler.whitelist = true;
                ChatUtils.sendMessage(sender, "FE Whitelist was off, it is now turned on.");
            }
        }

        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                APIRegistry.perms.setPlayerPermission(FunctionHelper.getPlayerID(args[1]), "fe.auth.whitelist", true, "_GLOBAL_");
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                APIRegistry.perms.setPlayerPermission(FunctionHelper.getPlayerID(args[1]), "fe.auth.whitelist", false, "_GLOBAL_");
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
