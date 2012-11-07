package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandExtinguish extends CommandBase {

	@Override
	public String getCommandName() {
		return "extinguish";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"ex"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int radius = 40;
			if(var2.length==1) {
				radius = Integer.parseInt(var2[0]);
			}else if(var2.length==0) {
				
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Extinguish Command Failed(Try /extinguish (<radius>)");
				return;
			}
			FunctionHandler.instance.extinguishCommand(radius, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Extinguish Command Failed!(Unknown Reason)");
			e.printStackTrace();
		}
	}

}
