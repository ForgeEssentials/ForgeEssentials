package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;

public class CommandShift extends CommandBase {

	@Override
	public String getCommandName() {
		return "shift";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			if(var2.length==3) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
				int x = Integer.parseInt(var2[0]);
				int y = Integer.parseInt(var2[1]);
				int z = Integer.parseInt(var2[2]);
				FunctionHandler.instance.point1X.put(ep.username, FunctionHandler.instance.point1X.get(ep.username)+x);
				FunctionHandler.instance.point1Y.put(ep.username, FunctionHandler.instance.point1Y.get(ep.username)+y);
				FunctionHandler.instance.point1Z.put(ep.username, FunctionHandler.instance.point1Z.get(ep.username)+z);
				FunctionHandler.instance.point2X.put(ep.username, FunctionHandler.instance.point2X.get(ep.username)+x);
				FunctionHandler.instance.point2Y.put(ep.username, FunctionHandler.instance.point2Y.get(ep.username)+y);
				FunctionHandler.instance.point2Z.put(ep.username, FunctionHandler.instance.point2Z.get(ep.username)+z);
				ep.addChatMessage("Shifted by: "+x+", "+y+", "+z);
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Shift Command Failed(Try /shift <X> <Y> <Z>");
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Shift Command Failed!(Unknown Reason)");
		}
	}

}
