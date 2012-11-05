package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandSetBelow extends CommandBase {

	@Override
	public String getCommandName() {
		return "setbelow";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo inf = null;
			int radius=0;
			if(var2.length==2) {
				inf = CommandProcesser.processIDMetaCombo(var2[0]);
				radius = Integer.parseInt(var2[1]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("SetBelow Command Failed(Try /setbelow <id(:meta)> <radius>)");
				 return;
			}
			FunctionHandler.instance.setBelowCommand(radius, inf, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("SetBelow Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
