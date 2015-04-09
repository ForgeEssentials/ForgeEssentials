package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.CommandParserArgs;

public class CommandPermissions extends ForgeEssentialsCommandBase {

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
    public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
    {
        // You have to be OP to change the cmd anyways.
        return true;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        // Always allow - command checks permissions itself
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        PermissionCommandParser.parseMain(new CommandParserArgs(this, args, sender));
    }
    
    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        try
        {
            CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
            PermissionCommandParser.parseMain(arguments);
            return arguments.tabCompletion;
        }
        catch (CommandException e)
        {
            return null;
        }
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

}
