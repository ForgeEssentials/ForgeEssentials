package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.LoginMessage;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
        	} else if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getPermissionNode() + ".others"))) {
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
        APIRegistry.permReg.registerPermissionLevel(getPermissionNode() + ".edit", RegGroup.OWNERS);
    }
    
    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.GUESTS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/motd [reload|<message>] Get or set the server message of the day.";
    }
}
