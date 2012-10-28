package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandSetNear extends CommandBase {

	@Override
	public String getCommandName() {
		return "setnear";
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
				 this.getCommandSenderAsPlayer(var1).addChatMessage("SetNear Command Failed(Try /setnear <id(:meta)> <radius>)");
				 return;
			}
			FunctionHandler.instance.setNearCommand(inf, radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("SetNear Command Failed!(Unknown Reason)");
		}
	}

}
