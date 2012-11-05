package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandCount extends CommandBase {

	@Override
	public String getCommandName() {
		return "count";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			boolean all=false;
			CommandInfo inf = null;
			if(var2.length==1) {
				inf = CommandProcesser.processIDMetaCombo(var2[0]);
			}else if(var2.length==0) {
				all=true;
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Count Command Failed(Try /count (<id(meta)>)");
				return;
			}

			FunctionHandler.instance.countCommand(inf, all, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Count Command Failed!(Unknown Reason)");
		}
	}

}
