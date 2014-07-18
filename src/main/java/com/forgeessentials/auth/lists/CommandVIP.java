package com.forgeessentials.auth.lists;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import net.minecraft.command.ICommandSender;

public class CommandVIP extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "vip";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length >= 2 && args[0].equalsIgnoreCase("add"))
        {
            APIRegistry.perms.setPlayerPermission(FunctionHelper.getPlayerID(args[1]), "fe.auth.vip", true, "_GLOBAL_");
        }
        else if (args.length >= 2 && args[0].equalsIgnoreCase("remove"))
        {
            APIRegistry.perms.setPlayerPermission(FunctionHelper.getPlayerID(args[1]), "fe.auth.vip", false, "_GLOBAL_");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.auth.vipcmd";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/vip [add|remove} <player> Adds or removes a player from the VIP list";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }

}
