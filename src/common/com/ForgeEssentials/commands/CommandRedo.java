package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandRedo extends CommandBase {

	@Override
	public String getCommandName() {
		return "redo";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			FunctionHandler.instance.redoCommand(this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Redo Command Failed!(Unknown Reason)");
		}
	}

}
