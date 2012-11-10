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
		if (args.length > 1)
		{
			try
			{
				String newRule = "";
				for (int i = 1; i < args.length; i++)
				{
					newRule = newRule + " " + args[i];
				}
				ForgeEssentials.instance.config.changeConfig("rules", "rule" + new Integer(args[0]), newRule);
			} catch (Exception e)
			{
			}
		} else
		{
			for (int i = 1; i <= 5; i++)
			{
				String rule = ForgeEssentials.instance.config.getSetting("rules", "rule" + i).toString().trim();
				if (!rule.equals(""))
					player.addChatMessage(rule);
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 1)
		{
			try
			{
				String newRule = "";
				for (int i = 1; i < args.length; i++)
				{
					newRule = newRule + " " + args[i];
				}
				ForgeEssentials.instance.config.changeConfig("rules", "rule" + new Integer(args[0]), newRule);
			} catch (Exception e)
			{
			}
		} else
		{
			for (int i = 1; i <= 5; i++)
			{
				String rule = ForgeEssentials.instance.config.getSetting("rules", "rule" + i).toString().trim();
				if (!rule.equals(""))
					OutputHandler.SOP(rule);
			}
		}
	}

	@Override
	public String getSyntaxConsole()
	{
		return "/rules [<number> <new rule>]";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/rules [<number> <new rule>]";
	}

	@Override
	public String getInfoConsole()
	{
		return "Get/set the rules of the server";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Get/set the rules of the server";
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
