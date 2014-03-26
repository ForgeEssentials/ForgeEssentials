package com.forgeessentials.scripting;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;

public class CommandScript extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName() {
		return "script";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		if (args[0].equalsIgnoreCase("run")){
			EventType e = EventType.getEventTypeForName(args[1]);
			if (args[2] != null){
				EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[2]);
				EventType.run(player, e);
			}else{
				EventType.run(sender, e);
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		if ( args[0].equalsIgnoreCase("run")){
			EventType e = EventType.getEventTypeForName(args[1]);
				EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[2]);
				EventType.run(player, e);
			
		}
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandPerm() {
		return "ForgeEssentials.Scripting.script";
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/script run [login|respawn] <player> Manually trigger scripts for a player.";
	}

}
