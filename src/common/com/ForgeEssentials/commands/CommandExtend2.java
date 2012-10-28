package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandExtend2 extends CommandBase {

	@Override
	public String getCommandName() {
		return "extend2";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"ex2","ext2"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			if(var2.length==3) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
				int x = Integer.parseInt(var2[0]);
				int y = Integer.parseInt(var2[1]);
				int z = Integer.parseInt(var2[2]);
				FunctionHandler.instance.point2X.put(ep.username, FunctionHandler.instance.point2X.get(ep.username)+x);
				FunctionHandler.instance.point2Y.put(ep.username, FunctionHandler.instance.point2Y.get(ep.username)+y);
				FunctionHandler.instance.point2Z.put(ep.username, FunctionHandler.instance.point2Z.get(ep.username)+z);
				ep.addChatMessage("Selection 2 extended by: "+x+", "+y+", "+z+" to: "+FunctionHandler.instance.point2X.get(ep.username)+", "+FunctionHandler.instance.point2Y.get(ep.username)+", "+FunctionHandler.instance.point2Z.get(ep.username));
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Extend2 Command Failed(Try /extend2 <X> <Y> <Z>");
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Extend2 Command Failed!(Unknown Reason)");
		}
	}

}
