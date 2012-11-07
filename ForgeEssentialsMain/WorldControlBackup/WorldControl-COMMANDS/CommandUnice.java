package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandUnice extends CommandBase {

	@Override
	public String getCommandName() {
		return "unice";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int radius=0;
			if(var2.length==1) {
				radius = Integer.parseInt(var2[0]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("UnIce Command Failed(Try /unice <radius>)");
				 return;
			}
			FunctionHandler.instance.unIceCommand(radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("UnIce Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
