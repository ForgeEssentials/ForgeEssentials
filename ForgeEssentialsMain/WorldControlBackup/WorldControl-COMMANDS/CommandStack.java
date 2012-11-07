package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandStack extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "stack";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args)
	{
		try
		{
			int id = 0;
			int times = 0;
			if (args.length == 1)
			{
				times = Integer.parseInt(args[0]);
			} else if (args.length == 2)
			{
				id = Integer.parseInt(args[1]);
				times = Integer.parseInt(args[0]);
			} else
			{
				this.getCommandSenderAsPlayer(commandSender).addChatMessage("Stack Command Failed(Try /stack <times> (<id>))");
				return;
			}
			FunctionHandler.instance.stackCommand(getCommandSenderAsPlayer(commandSender), times);
		} catch (Exception e)
		{
			this.getCommandSenderAsPlayer(commandSender).addChatMessage("Stack Command Failed!(Unknown Reason)");
		}
	}

}
