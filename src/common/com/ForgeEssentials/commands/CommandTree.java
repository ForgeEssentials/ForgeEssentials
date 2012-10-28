package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;

public class CommandTree extends CommandBase {

	@Override
	public String getCommandName() {
		return "tree";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			if(var2.length==1) {
				String temp = var2[0];
				FunctionHandler.instance.treeCommand(temp, ep);
			}else if(var2.length==0){
				FunctionHandler.instance.treeCommand("oak", ep);
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Tree Command Failed!(Try /tree (<type>))");
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Tree Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
