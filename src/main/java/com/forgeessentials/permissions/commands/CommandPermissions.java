package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;

public class CommandPermissions extends ParserCommandBase
{

    @Override
    public final String getName()
    {
        return "feperm";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "perm", "fep", "p" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/feperm Configure FE permissions.";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        PermissionCommandParser.parseMain(arguments);
    }

}
