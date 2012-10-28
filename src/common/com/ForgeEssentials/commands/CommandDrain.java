package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.Block;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandDrain extends CommandBase {

	@Override
	public String getCommandName() {
		return "drain";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int radius=0;
			if(var2.length==1) {
				String temp = var2[0];
				radius = Integer.parseInt(temp);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("Drain Command Failed(Try /drain <radius>");
				 return;
			}
			FunctionHandler.instance.drainCommand(radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Drain Command Failed!(Unknown Reason)");
		}
	}

}
