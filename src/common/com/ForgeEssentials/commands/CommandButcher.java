package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandButcher extends CommandBase {

	@Override
	public String getCommandName() {
		return "butcher";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"butch","killall"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			boolean all=false;
			int radius=5;
			if(var2.length==1) {
				radius = Integer.parseInt(var2[0]);
			}else if(var2.length==0) {
				all=true;
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Butcher Command Failed(Try /butcher (<radius>)");
				return;
			}

			FunctionHandler.instance.butcherCommand(radius, all, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Butcher Command Failed!(Unknown Reason)");
		}
	}

}
