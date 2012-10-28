package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;

public class CommandHPos1 extends CommandBase {

	@Override
	public String getCommandName() {
		return "hpos1";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"hp1"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			MovingObjectPosition mop = FunctionHandler.instance.rayTrace(Minecraft.getMinecraft().renderViewEntity);
			if(mop!=null) {
				FunctionHandler.instance.point1X.put(ep.username, mop.blockX);
				FunctionHandler.instance.point1Y.put(ep.username, mop.blockY);
				FunctionHandler.instance.point1Z.put(ep.username, mop.blockZ);
				this.getCommandSenderAsPlayer(var1).addChatMessage("HPos1 set to: "+FunctionHandler.instance.point1X.get(ep.username)+", "+FunctionHandler.instance.point1Y.get(ep.username)+", "+FunctionHandler.instance.point1Z.get(ep.username));
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("HPos1 Command Failed!(No Block Selected)");
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("HPos1 Command Failed!(Unknown Reason)");
		}
	}

}
