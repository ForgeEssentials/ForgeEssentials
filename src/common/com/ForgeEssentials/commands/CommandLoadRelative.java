package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandLoadRelative extends CommandBase {

	@Override
	public String getCommandName() {
		return "loadrelative";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			String name = "";
			boolean clear=false;
			if(var2.length==1) {
				name = var2[0];
			}else if(var2.length==2) {
				name = var2[0];
				String temp = var2[1];
				if(temp.equals("clear")||temp.equals("true")) {
					clear=true;
				}
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("LoadRelative Command Failed(Try /loadrelative <name> (<clear>))");
				return;
			}
			if(name!="") {
			FunctionHandler.instance.loadRelativeCommand(name, this.getCommandSenderAsPlayer(var1), clear);
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("LoadRelative Command Failed!(Unknown Reason)");
		}
	}

}
