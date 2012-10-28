package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandDimension extends CommandBase {

	@Override
	public String getCommandName() {
		return "dimension";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"dim","dimen","measure"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			int dimX = Math.abs(FunctionHandler.instance.point1X.get(ep.username) - FunctionHandler.instance.point2X.get(ep.username))+1;
			int dimY = Math.abs(FunctionHandler.instance.point1Y.get(ep.username) - FunctionHandler.instance.point2Y.get(ep.username))+1;
			int dimZ = Math.abs(FunctionHandler.instance.point1Z.get(ep.username) - FunctionHandler.instance.point2Z.get(ep.username))+1;
			this.getCommandSenderAsPlayer(var1).addChatMessage("Selection Region's Dimensions Are: "+dimX+"X"+dimY+"X"+dimZ);
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Dimension Command Failed!(Unknown Reason)");
		}
	}

}
