package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandCut extends CommandBase {

	@Override
	public String getCommandName() {
		return "cut";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int id = 0;
			if(var2.length==1) {
				id = Integer.parseInt(var2[0]);
			}else if(var2.length==0) {
				
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Cut Command Failed(Try /cut (<id>))");
				return;
			}
			FunctionHandler.instance.cutCommand(getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Cut Command Failed!(Unknown Reason)");
		}
	}

}
