package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandReplaceBelow extends CommandBase {

	@Override
	public String getCommandName() {
		return "replacebelow";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo before = null;
			CommandInfo after = null;
			int radius=0;
			if(var2.length==3) {
				before = CommandProcesser.processIDMetaCombo(var2[0]);
				after = CommandProcesser.processIDMetaCombo(var2[1]);
				radius = Integer.parseInt(var2[2]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("ReplaceBelow Command Failed(Try /replacebelow <id(:meta)> <id(:meta)> <radius>)");
				 return;
			}
			FunctionHandler.instance.replaceBelowCommand(radius, before, after, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("ReplaceBelow Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
