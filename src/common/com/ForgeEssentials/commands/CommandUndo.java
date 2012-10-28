package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandUndo extends CommandBase {

	@Override
	public String getCommandName() {
		return "undo";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			FunctionHandler.instance.undoCommand(this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Undo Command Failed!(Unknown Reason)");
		}
	}

}
