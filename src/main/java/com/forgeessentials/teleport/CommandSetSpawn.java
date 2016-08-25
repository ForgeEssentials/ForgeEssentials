package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.util.CommandParserArgs;

public class CommandSetSpawn extends ParserCommandBase
{

    public static final String PERM_SETSPAWN = "fe.perm.setspawn";

    @Override
    public String getCommandName()
    {
        return "setspawn";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/setspawn (here|x y z) | (bed enable|disable)";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_SETSPAWN;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        PermissionCommandParser.parseGroupSpawn(arguments, Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone());
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        PermissionCommandParser.parseGroupSpawn(new CommandParserArgs(this, args, sender), Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone());
    }

}
