package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandPaste extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "paste";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args)
	{
		boolean point2 = false;
		boolean clear = false;
		try
		{
			if (args.length >= 1)
			{
				point2 = new Boolean(args[0]);
			}
			if (args.length >= 2)
			{
				clear = args[1].equals("true");
			}
		} catch (Exception e)
		{
			getCommandSenderAsPlayer(commandSender).addChatMessage("Pasting with default(s)");
		}
		FunctionHandler.instance.pasteCommand(getCommandSenderAsPlayer(commandSender), point2, clear);
	}
}
