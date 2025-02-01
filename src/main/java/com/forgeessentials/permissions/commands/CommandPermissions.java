package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;

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
    public void parse(CommandParserArgs arguments) throws CommandException
    {
    	try {
    		PermissionCommandParser.parseMain(arguments);
    	}catch(NullPointerException e) {
    		e.printStackTrace();
    		arguments.error("Null error while parsing arguments");
    	}
    }

}
