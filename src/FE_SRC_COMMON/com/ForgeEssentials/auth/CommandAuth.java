package com.ForgeEssentials.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandAuth extends ForgeEssentialsCommandBase
{
	private static String[] playerCommands = new String[] {"help", "login", "register", "changepass", "kick", "setpass", "unregister"};
	private static String[] serverCommands = new String[] {"help", "kick", "setpass", "unregister"};

	public CommandAuth()
	{

		// nothing
	}

	@Override
	public String getCommandName()
	{
		return "auth";
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList();
		list.add("AUTH");
		return list;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
			throw new WrongUsageException("commands.auth.usage");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
			throw new WrongUsageException("commands.auth.usage");

		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				sender.sendChatToPlayer(" - /auth kick <player>  - forces the player to login again");
				sender.sendChatToPlayer(" - /auth setpass <player>  - resets the player password");
				sender.sendChatToPlayer(" - /auth unregister <player>  - forces the player to register again");
				return;
			}
			else
				throw new WrongUsageException("commands.auth.usage");
		}
		else if (args.length == 2)
		{
			String name = args[1];
			
			EntityPlayer player = PlayerSelector.matchOnePlayer(sender, name);
			if (player == null)
				sender.sendChatToPlayer("A player of that name is not on the server. Doing the action anyways.");
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		ArrayList<String> list = new ArrayList();
		if (sender instanceof EntityPlayer)
		{
			list.addAll(Arrays.asList(playerCommands));
		}
		else
		{
			list.addAll(Arrays.asList(serverCommands));
		}
		return null;
	}

	@Override
	public String getCommandPerm()
	{
		return "FormeEssentials.ModuleAuth";
	}

}
