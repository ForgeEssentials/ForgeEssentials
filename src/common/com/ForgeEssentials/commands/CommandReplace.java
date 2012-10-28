package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandReplace extends CommandBase {

	@Override
	public String getCommandName() {
		return "replace";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo begin = null;
			CommandInfo end = null;
			if(var2.length==2) {
				begin = CommandProcesser.processIDMetaCombo(var2[0]);
				end = CommandProcesser.processIDMetaCombo(var2[1]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("Replace Command Failed(Try /replace <id(:meta)> <id(:meta)>)");
				 return;
			}
			FunctionHandler.instance.replaceCommand(begin, end, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Replace Command Failed!(Unknown Reason)");
		}
	}

}
