package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBurn extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "burn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			if (args[0].toLowerCase().equals("me"))
			{
				sender.setFire(15);
				OutputHandler.chatError(sender, Localization.get(Localization.BURN_SELF));
			}
			else if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
				if (PlayerSelector.hasArguments(args[0]))
				{
					players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
				}
				if (players.size() != 0)
				{
					OutputHandler.chatConfirmation(sender, Localization.get(Localization.BURN_PLAYER));
					for (EntityPlayer player : players)
					{
						player.setFire(15);
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
		}
		else if (args.length == 2)
		{
			if (args[0].toLowerCase().equals("me"))
			{
				try
				{
					sender.setFire(Integer.parseInt(args[1]));
					OutputHandler.chatError(sender, Localization.get(Localization.BURN_SELF));
				}
				catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
				}
			}
			else if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
				if (PlayerSelector.hasArguments(args[0]))
				{
					players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
				}
				if (players.size() != 0)
				{
					for (EntityPlayer player : players)
					{
						try
						{
							player.setFire(Integer.parseInt(args[1]));
							OutputHandler.chatConfirmation(sender, Localization.get(Localization.BURN_PLAYER));
						}
						catch (NumberFormatException e)
						{
							OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
							break;
						}
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer player : players)
				{
					player.setFire(Integer.parseInt(args[1]));
				}
				sender.sendChatToPlayer(Localization.get(Localization.BURN_PLAYER));
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
