package com.ForgeEssentials.auth.lists;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandWhiteList extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "whitelist";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		if(!PlayerTracker.whitelist) {
            ChatUtils.sendMessage(sender, "The whitelist is not enabled. You can enable it in server.properties or your auth config file.");
            ChatUtils.sendMessage(sender, "Note that server.properties will take precedent over the auth config.");

        }
		else if (args.length >= 2 && args[0].equalsIgnoreCase("add")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isWhiteListed", true, "_GLOBAL_");
		}else if (args.length >= 2 && args[0].equalsIgnoreCase("remove")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isWhiteListed", false, "_GLOBAL_");
		}else if (args.length >= 1 && args[0].equalsIgnoreCase("enable")){
			PlayerTracker.whitelist = true;
		}else if (args.length >= 1 && args[0].equalsIgnoreCase("disable")){
			PlayerTracker.whitelist = false;
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		if(!PlayerTracker.whitelist) {
            ChatUtils.sendMessage(sender, "The whitelist is not enabled. You can enable it in server.properties or your auth config file.");
            ChatUtils.sendMessage(sender, "Note that server.properties will take precedent over the auth config.");

        }
		else if (args.length >= 2 && args[0].equalsIgnoreCase("add")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isWhiteListed", true, "_GLOBAL_");
		}else if (args.length >= 2 && args[0].equalsIgnoreCase("remove")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isWhiteListed", false, "_GLOBAL_");
		}else if (args.length >= 1 && args[0].equalsIgnoreCase("enable")){
			PlayerTracker.whitelist = true;
		}else if (args.length >= 1 && args[0].equalsIgnoreCase("disable")){
			PlayerTracker.whitelist = false;
		}
	}

	@Override
	public boolean canConsoleUseCommand() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandPerm() {
		// TODO Auto-generated method stub
		return "ForgeEssentials.Auth.whitelist";
	}

}
