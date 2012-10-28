package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandStack extends CommandBase {

	@Override
	public String getCommandName() {
		return "stack";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int id = 0;
			int times = 0;
			if(var2.length==1) {
				times = Integer.parseInt(var2[0]);
			}else if(var2.length==2) {
				id = Integer.parseInt(var2[1]);
				times = Integer.parseInt(var2[0]);
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Stack Command Failed(Try /stack <times> (<id>))");
				return;
			}
			FunctionHandler.instance.stackCommand(id, this.getCommandSenderAsPlayer(var1), times);
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Stack Command Failed!(Unknown Reason)");
		}
	}

}
