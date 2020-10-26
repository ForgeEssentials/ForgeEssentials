package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.util.CommandParserArgs;

public class CommandSetSpawn extends ParserCommandBase
{

    public static final String PERM_SETSPAWN = "fe.perm.setspawn";

    @Override
    public String getPrimaryAlias()
    {
        return "setspawn";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/setspawn (here|x y z) | (bed enable|disable)";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
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
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true, server);
        try
        {
            PermissionCommandParser.parseGroupSpawn(arguments, Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone());
        }
        catch (CommandException e)
        {
            return arguments.tabCompletion;
        }
        return arguments.tabCompletion;

    }

}
