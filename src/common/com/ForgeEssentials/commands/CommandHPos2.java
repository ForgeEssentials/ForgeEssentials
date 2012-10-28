package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;

public class CommandHPos2 extends CommandBase {

	@Override
	public String getCommandName() {
		return "hpos2";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"hp2"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			MovingObjectPosition mop = FunctionHandler.instance.rayTrace(Minecraft.getMinecraft().renderViewEntity);
			if(mop!=null) {
				FunctionHandler.instance.point2X.put(ep.username, mop.blockX);
				FunctionHandler.instance.point2Y.put(ep.username, mop.blockY);
				FunctionHandler.instance.point2Z.put(ep.username, mop.blockZ);
				this.getCommandSenderAsPlayer(var1).addChatMessage("HPos2 set to: "+FunctionHandler.instance.point2X.get(ep.username)+", "+FunctionHandler.instance.point2Y.get(ep.username)+", "+FunctionHandler.instance.point2Z.get(ep.username));
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("HPos2 Command Failed!(No Block Selected)");
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("HPos2 Command Failed!(Unknown Reason)");
		}
	}

}
