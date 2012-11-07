package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandDelete extends CommandBase {

	@Override
	public String getCommandName() {
		return "delete";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"del","rem","remove"});
    }

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo inf = (new CommandInfo());
			inf.setInfo(0, 0);
			FunctionHandler.instance.delCommand(inf, this.getCommandSenderAsPlayer(var1));
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Delete Command Failed!(Unknown Reason)");
		}
	}

}
