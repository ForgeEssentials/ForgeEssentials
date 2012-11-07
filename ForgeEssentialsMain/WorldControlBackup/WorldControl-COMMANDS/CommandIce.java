package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandIce extends CommandBase {

	@Override
	public String getCommandName() {
		return "ice";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int radius=0;
			if(var2.length==1) {
				radius = Integer.parseInt(var2[0]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("Ice Command Failed(Try /ice <radius>)");
				 return;
			}
			FunctionHandler.instance.iceCommand(radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Ice Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
