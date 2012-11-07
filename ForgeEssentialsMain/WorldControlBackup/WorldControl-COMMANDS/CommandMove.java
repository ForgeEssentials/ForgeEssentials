package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandMove extends CommandBase {

	@Override
	public String getCommandName() {
		return "move";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int x = 0;
			int y = 0;
			int z = 0;
			if(var2.length==3) {
				x = Integer.parseInt(var2[0]);
				y = Integer.parseInt(var2[1]);
				z = Integer.parseInt(var2[2]);
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Move Command Failed(Try /move <x> <y> <z>)");
				return;
			}
			FunctionHandler.instance.moveCommand(x, y, z, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Move Command Failed!(Unknown Reason)");
		}
	}

}
