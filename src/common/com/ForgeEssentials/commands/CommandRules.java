package com.ForgeEssentials.commands;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandRules extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "rules";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length > 0)
			ForgeEssentials.instance.config.changeConfig("basic", "rules", args[0]);
		else
		{
			String rulesLine = ForgeEssentials.instance.config.getSetting("basic", "rules").toString() + "\\n";
			ArrayList<String> rules = new ArrayList<String>();
			int lastBreak = 0;
			do
			{
				rules.add(rulesLine.substring(lastBreak, rulesLine.indexOf("\\n", lastBreak)));
				lastBreak = rulesLine.indexOf("\\n", lastBreak);
			} while (rulesLine.indexOf("\\n", lastBreak) != -1);
			for (String rule : rules)
				player.addChatMessage(rule);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
			ForgeEssentials.instance.config.changeConfig("basic", "rules", args[0]);
		else
		{
			String rulesLine = ForgeEssentials.instance.config.getSetting("basic", "rules").toString();
			ArrayList<String> rules = new ArrayList<String>();
			int lastBreak = 0;
			do
			{
				rules.add(rulesLine.substring(lastBreak, lastBreak = rulesLine.indexOf("\\n", lastBreak)));
			} while (rulesLine.indexOf("\\n", lastBreak) != -1);
			for (String rule : rules)
				OutputHandler.SOP(rule);
		}
	}

	@Override
	public String getSyntaxConsole()
	{
		return "/rules";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/rules";
	}

	@Override
	public String getInfoConsole()
	{
		return "Get the rules of the server";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Get the rules of the server";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

}
