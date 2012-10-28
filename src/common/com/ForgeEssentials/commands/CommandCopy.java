package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandCopy extends CommandBase {

	@Override
	public String getCommandName() {
		return "copy";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int id = 0;
			if(var2.length==1) {
				id = Integer.parseInt(var2[0]);
			}else if(var2.length==0) {
				
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Copy Command Failed(Try /copy (<id>))");
				return;
			}
			FunctionHandler.instance.copyCommand(id, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Copy Command Failed!(Unknown Reason)");
		}
	}

}
