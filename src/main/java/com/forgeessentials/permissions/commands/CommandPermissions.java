package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPermissions extends ForgeEssentialsCommandBase {
    // Variables for auto-complete
    String[] args2 = { "user", "group", "export", "promote", "test" };
    String[] groupargs = { "prefix", "suffix", "parent", "priority", "allow", "true", "deny", "false", "clear" };
    String[] playerargs = { "prefix", "suffix", "group", "set", "add", "remove", "allow", "true", "deny", "false", "clear" };
    String[] playergargs = { "set", "add", "remove" };

    @Override
    public final String getCommandName()
    {
        //return "feperm";
    	return "p";
    }

//    @Override
//    public List<String> getCommandAliases()
//    {
//        ArrayList<String> list = new ArrayList<String>();
//        list.add("perm");
//        list.add("fep");
//        list.add("p");
//        return list;
//    }

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
    	new PermissionCommandHandler(sender, args);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    	new PermissionCommandHandler(sender, args);
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.perm";
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
    	return PermissionsManager.checkPerm(player, getPermissionNode());
    	// TODO: Check why the old code did something differnt from ForgeEssentialsCommandBase
//        PermResult result = APIRegistry.perms.checkPermResult(player, getPermissionNode(), true);
//        return result.equals(PermResult.DENY) ? false : true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	// TODO: addTabCompletionOptions
    	return null;
//        if (args.length == 1)
//        {
//            return getListOfStringsMatchingLastWord(args, args2);
//        }
//        else
//        {
//
//        }
//        switch (args.length)
//        {
//        case 1:
//            return getListOfStringsMatchingLastWord(args, args2);
//        case 2:
//            if (args[0].equalsIgnoreCase("group"))
//            {
//                List<Group> groups = APIRegistry.perms.getGroupsInZone(APIRegistry.perms.getGlobalZone().getName());
//                ArrayList<String> groupnames = new ArrayList<String>();
//                for (int i = 0; i < groups.size(); i++)
//                {
//                    groupnames.add(groups.get(i).name);
//                }
//                groupnames.add("create");
//                return getListOfStringsFromIterableMatchingLastWord(args, groupnames);
//            }
//            break;
//        case 3:
//            if (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("player"))
//            {
//                return getListOfStringsMatchingLastWord(args, playerargs);
//            }
//            else if (args[0].equalsIgnoreCase("group") && !args[1].equalsIgnoreCase("create"))
//            {
//                return getListOfStringsMatchingLastWord(args, groupargs);
//            }
//            break;
//        case 4:
//            if ((args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("player")) && args[2].equalsIgnoreCase("group"))
//            {
//                return getListOfStringsMatchingLastWord(args, playergargs);
//            }
//            break;
//        case 5:
//            if ((args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("player")) && args[2].equalsIgnoreCase("group"))
//            {
//                List<Group> groups = APIRegistry.perms.getGroupsInZone(APIRegistry.perms.getGlobalZone().getName());
//                ArrayList<String> groupnames = new ArrayList<String>();
//                for (int i = 0; i < groups.size(); i++)
//                {
//                    groupnames.add(groups.get(i).name);
//                }
//                groupnames.add("create");
//                return getListOfStringsFromIterableMatchingLastWord(args, groupnames);
//            }
//            break;
//        }
//        return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
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
