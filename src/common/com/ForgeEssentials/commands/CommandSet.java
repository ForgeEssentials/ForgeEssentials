package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandSet extends CommandBase {

	@Override
	public String getCommandName() {
		return "set";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo inf = null;
			if(var2.length==1) {
				inf = CommandProcesser.processIDMetaCombo(var2[0]);
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Set Command Failed(Try /set <id(:meta)>)");
				return;
			}
			if(inf!=null) {
			FunctionHandler.instance.setCommand(inf, this.getCommandSenderAsPlayer(var1));
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Set Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
