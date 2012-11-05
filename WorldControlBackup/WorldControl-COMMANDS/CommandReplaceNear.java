package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandReplaceNear extends CommandBase {

	@Override
	public String getCommandName() {
		return "replacenear";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo begin = null;
			CommandInfo end = null;
			int radius=0;
			if(var2.length==3) {
				begin = CommandProcesser.processIDMetaCombo(var2[0]);
				end = CommandProcesser.processIDMetaCombo(var2[1]);
				radius = Integer.parseInt(var2[2]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("ReplaceNear Command Failed(Try /replacenear <id(:meta)> <id(:meta)> <radius>)");
				 return;
			}
			FunctionHandler.instance.replaceNearCommand(begin, end, radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("ReplaceNear Command Failed!(Unknown Reason)");
		}
	}

}
