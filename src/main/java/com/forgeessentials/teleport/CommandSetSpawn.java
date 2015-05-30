package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.util.CommandParserArgs;

public class CommandSetSpawn extends ForgeEssentialsCommandBase
{

    public static final String PERM_SETSPAWN = "fe.perm.setspawn";

    @Override
    public String getCommandName()
    {
        return "setspawn";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        PermissionCommandParser.parseGroupSpawn(new CommandParserArgs(this, args, sender), Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone());
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
        PermissionCommandParser.parseGroupSpawn(arguments, Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone());
        return arguments.tabCompletion;
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        PermissionCommandParser.parseGroupSpawn(new CommandParserArgs(this, args, sender), Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone());
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_SETSPAWN;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/setspawn (here|x y z) | (bed enable|disable)";
    }

}
