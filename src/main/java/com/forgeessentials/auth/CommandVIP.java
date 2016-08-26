package com.forgeessentials.auth;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandVIP extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "vip";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 2 && args[0].equalsIgnoreCase("add"))
        {
            APIRegistry.perms.setPlayerPermission(UserIdent.get(args[1], sender), "fe.auth.vip", true);
        }
        else if (args.length >= 2 && args[0].equalsIgnoreCase("remove"))
        {
            APIRegistry.perms.setPlayerPermission(UserIdent.get(args[1], sender), "fe.auth.vip", false);
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
        return "fe.auth.vipcmd";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/vip [add|remove} <player> Adds or removes a player from the VIP list";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

}
