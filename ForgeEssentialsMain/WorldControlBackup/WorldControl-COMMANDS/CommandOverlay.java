package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandOverlay extends CommandBase {

	@Override
	public String getCommandName() {
		return "overlay";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo begin = null;
			int radius=0;
			if(var2.length==2) {
				begin = CommandProcesser.processIDMetaCombo(var2[0]);
				radius = Integer.parseInt(var2[1]);
			}else{
				 this.getCommandSenderAsPlayer(var1).addChatMessage("Overlay Command Failed(Try /overlay <id(:meta)> <radius>)");
				 return;
			}
			FunctionHandler.instance.overlayCommand(begin, radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Overlay Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
