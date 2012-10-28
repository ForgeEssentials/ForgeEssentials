package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandMiniChunk extends CommandBase {

	@Override
	public String getCommandName() {
		return "minichunk";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"mch","mchunk"});
    }
	
	public int toChunk(double num) {
		int nume = (int)num;
		int temp = (int)(nume/16);
		return temp*16;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			FunctionHandler.instance.point1X.put(ep.username, toChunk(ep.posX));
			FunctionHandler.instance.point1Y.put(ep.username, toChunk(ep.posY));
			FunctionHandler.instance.point1Z.put(ep.username, toChunk(ep.posZ));
			FunctionHandler.instance.point2X.put(ep.username, toChunk(ep.posX)+16);
			FunctionHandler.instance.point2Y.put(ep.username, toChunk(ep.posY)+16);
			FunctionHandler.instance.point2Z.put(ep.username, toChunk(ep.posZ)+16);
			this.getCommandSenderAsPlayer(var1).addChatMessage("Selected MiniChunk");
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("MiniChunk Command Failed!(Unknown Reason)");
		}
	}

}
