package com.forgeessentials.auth.lists;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;

public class CommandWhiteList extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "whitelist";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(!PlayerTracker.whitelist){
			ChatUtils.sendMessage(sender, "The whitelist is not enabled. You can enable it in server.properties or your auth config file.");
			ChatUtils.sendMessage(sender, "Note that server.properties will take precedent over the auth config.");

		}
		
		else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")){
			if (PlayerTracker.whitelist)
			{ 
				PlayerTracker.whitelist = false;
				ChatUtils.sendMessage(sender, "FE Whitelist was on, it is now turned off.");
			}
			else {
				PlayerTracker.whitelist = true;
				ChatUtils.sendMessage(sender, "FE Whitelist was off, it is now turned on.");
			}
		}
		
		else if (args.length == 2){
			if (args[0].equalsIgnoreCase("add"))
			{
				APIRegistry.perms.setPlayerPermission(args[1], "fe.auth.whitelist", true, "_GLOBAL_");
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				APIRegistry.perms.setPlayerPermission(args[1], "fe.auth.whitelist", false, "_GLOBAL_");
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getCommandPerm() {
		return "fe.auth.whitelist.admin";
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/whitelist ";
	}

	@Override
	public RegGroup getReggroup() {
		// TODO Auto-generated method stub
		return RegGroup.ZONE_ADMINS;
	}

}
