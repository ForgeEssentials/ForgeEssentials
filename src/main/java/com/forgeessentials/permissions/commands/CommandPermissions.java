package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

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
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("perm");
        list.add("fep");
        list.add("p");
        return list;
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        PermissionCommandParser.parseMain(arguments);
    }

}
