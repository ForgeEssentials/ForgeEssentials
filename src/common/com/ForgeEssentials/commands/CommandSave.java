package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandSave extends CommandBase {

	@Override
	public String getCommandName() {
		return "save";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			String name = "";
			if(var2.length==1) {
				name = var2[0];
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Save Command Failed(Try /save <name>)");
				return;
			}
			if(name!="") {
			FunctionHandler.instance.saveCommand(name, this.getCommandSenderAsPlayer(var1));
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Save Command Failed!(Unknown Reason)");
		}
	}

}
