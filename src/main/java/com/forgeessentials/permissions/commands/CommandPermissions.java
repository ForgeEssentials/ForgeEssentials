package com.forgeessentials.permissions.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.FeCommandParserArgs;

public class CommandPermissions extends ParserCommandBase
{

    @Override
    public final String getCommandName()
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
    public String getCommandUsage(ICommandSender sender)
    {
        return "/feperm Configure FE permissions.";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public void parse(FeCommandParserArgs arguments)
    {
        PermissionCommandParser.parseMain(arguments);
    }

}
