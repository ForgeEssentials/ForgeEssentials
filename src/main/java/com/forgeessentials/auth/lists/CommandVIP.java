package com.forgeessentials.auth.lists;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandVIP extends ForgeEssentialsCommandBase{

	@Override
	public String getCommandName() {
		return "vip";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		if(args.length >= 2 && args[0].equalsIgnoreCase("add")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isVIP", true, "_GLOBAL_");
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("remove")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isVIP", false, "_GLOBAL_");
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		if(args.length >= 2 && args[0].equalsIgnoreCase("add")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isVIP", true, "_GLOBAL_");
		}else if(args.length >= 2 && args[0].equalsIgnoreCase("remove")){
			APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isVIP", false, "_GLOBAL_");
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
		return "ForgeEssentials.Auth.vipcmd";
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
