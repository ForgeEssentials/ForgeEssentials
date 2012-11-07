package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandDistr extends CommandBase {

	@Override
	public String getCommandName() {
		return "distr";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			FunctionHandler.instance.distrCommand(this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Distr Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
