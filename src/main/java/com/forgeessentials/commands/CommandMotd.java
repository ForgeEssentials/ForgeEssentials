package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.LoginMessage;

public class CommandMotd extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "motd";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length > 0)
        {
        	if (args[0].equalsIgnoreCase("reload")) {
        		LoginMessage.loadFile();
        	} else if (PermissionsManager.checkPermission(sender, getPermissionNode() + ".others")) {
        		ArrayList<String> motd = new ArrayList<String>();
        		motd.add(StringUtils.join(args, " "));
        		LoginMessage.setMOTD(motd);
        	}
        }
        LoginMessage.sendLoginMessage(sender);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
        	if (args[0].equalsIgnoreCase("reload")) {
        		LoginMessage.loadFile();
        	} else {
        		ArrayList<String> motd = new ArrayList<String>();
        		motd.add(StringUtils.join(args, " "));
        		LoginMessage.setMOTD(motd);
        	}
        }
        LoginMessage.sendLoginMessage(sender);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "reload");
        }
        else
        {
            return null;
        }
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".edit", RegisteredPermValue.OP);
    }
    
    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/motd [reload|<message>] Get or set the server message of the day.";
    }
}
