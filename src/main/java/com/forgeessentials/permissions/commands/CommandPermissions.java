package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandPermissions extends ForgeEssentialsCommandBase {
    // Variables for auto-complete
    String[] args2 = { "user", "group", "export", "promote", "test" };
    String[] groupargs = { "prefix", "suffix", "parent", "priority", "allow", "true", "deny", "false", "clear" };
    String[] playerargs = { "prefix", "suffix", "group", "set", "add", "remove", "allow", "true", "deny", "false", "clear" };
    String[] playergargs = { "set", "add", "remove" };

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
    public void processCommandPlayer(EntityPlayer sender, String[] args)
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
        return "fe.perm";
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
    	return PermissionsManager.checkPermission(player, getPermissionNode());
    	// TODO: Check why the old code did something differnt from ForgeEssentialsCommandBase
//        PermResult result = APIRegistry.perms.checkPermResult(player, getPermissionNode(), true);
//        return result.equals(PermResult.DENY) ? false : true;
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
        return RegisteredPermValue.OP;
    }

}
