package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandMotd extends ForgeEssentialsCommandBase
{

	public static String motd;

	@Override
	public String getCommandName()
	{
		return "motd";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length > 0)
		{
			motd = "";
			for (String arg : args)
				motd = motd + arg + " ";
			ForgeEssentials.instance.config.changeProperty("Miscellaneous", "motd", motd);
		} else
			player.addChatMessage(motd);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			motd = "";
			for (String arg : args)
				motd = motd + arg + " ";
			ForgeEssentials.instance.config.changeProperty("Miscellaneous", "motd", motd);
		} else
			sender.sendChatToPlayer(motd);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions
		return true;
	}

	@Override
	public String getSyntaxConsole()
	{
		return "/motd [new MOTD]";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/motd [new MOTD]";
	}

	@Override
	public String getInfoConsole()
	{
		return "Get/set the Message Of the Day";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Get/set the Message Of the Day";
	}
}
