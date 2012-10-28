package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MathHelper;

public class CommandPos2 extends CommandBase {

	@Override
	public String getCommandName() {
		return "pos2";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"p2"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			FunctionHandler.instance.point2X.put(ep.username, MathHelper.floor_double(ep.posX));
			FunctionHandler.instance.point2Y.put(ep.username, MathHelper.floor_double(ep.posY));
			FunctionHandler.instance.point2Z.put(ep.username, MathHelper.floor_double(ep.posZ));
			this.getCommandSenderAsPlayer(var1).addChatMessage("Pos2 set to: "+FunctionHandler.instance.point2X.get(ep.username)+", "+FunctionHandler.instance.point2Y.get(ep.username)+", "+FunctionHandler.instance.point2Z.get(ep.username));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Pos2 Command Failed!(Unknown Reason)");
		}
	}

}
