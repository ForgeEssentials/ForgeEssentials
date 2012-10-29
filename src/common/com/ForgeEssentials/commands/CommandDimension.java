package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.AreaSelector.Selection;
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
	public void processCommand(ICommandSender var1, String[] var2)
	{
		EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
		Selection select = Selection.getPlayerSelection(ep);
		int[] dims = select.getDimensions();
		this.getCommandSenderAsPlayer(var1).addChatMessage("Selection Region's Dimensions Are: " + dims[0] + "X" + dims[1] + "X" + dims[2]);
	}

}
