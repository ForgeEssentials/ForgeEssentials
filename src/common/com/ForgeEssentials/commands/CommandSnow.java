package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandSnow extends CommandBase {

	@Override
	public String getCommandName() {
		return "snow";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int radius=0;
			if(var2.length==1) {
				radius = Integer.parseInt(var2[0]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("Snow Command Failed(Try /snow <radius>)");
				 return;
			}
			FunctionHandler.instance.snowCommand(radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Overlay Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
