package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

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
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
    	new PermissionCommandParser(sender, args, false);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    	new PermissionCommandParser(sender, args, false);
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	return new PermissionCommandParser(sender, args, true).getTabCompleteList();
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
