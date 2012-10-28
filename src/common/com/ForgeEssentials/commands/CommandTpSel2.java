package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandTpSel2 extends CommandBase {

	@Override
	public String getCommandName() {
		return "tpsel2";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			if(var2.length==0) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
				ep.setPositionAndUpdate(FunctionHandler.instance.point2X.get(ep.username), FunctionHandler.instance.point2Y.get(ep.username), FunctionHandler.instance.point2Z.get(ep.username));
				ep.addChatMessage("Teleported to Selection2");
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("TpSel2 Command Failed(Try /tpsel1)");
				return;
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("TpSel2 Command Failed!(Unknown Reason)");
		}
	}

}
