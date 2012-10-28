package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandPaste extends CommandBase {

	@Override
	public String getCommandName() {
		return "paste";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int id = 0;
			boolean clear = true;
			if(var2.length==1) {
				id = Integer.parseInt(var2[0]);
			}else if(var2.length==2) {
				id = Integer.parseInt(var2[0]);
				String temp = var2[1];
				if(temp.equals("false")||temp.equals("noclear")) {
					clear=false;
				}
			}else if(var2.length==0) {
				
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Paste Command Failed(Try /paste (<id>) (<clear>))");
				return;
			}
			FunctionHandler.instance.pasteCommand(id, this.getCommandSenderAsPlayer(var1), clear);
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Paste Command Failed!(Unknown Reason)");
		}
	}

}
