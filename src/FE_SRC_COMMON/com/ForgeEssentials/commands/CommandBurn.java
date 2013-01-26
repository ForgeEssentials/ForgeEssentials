package com.ForgeEssentials.commands;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

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
			if (args[0].toLowerCase().equals("me") && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".me")))
			{
				sender.setFire(15);
				OutputHandler.chatError(sender, Localization.get(Localization.BURN_SELF));
			}
			else
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
				if (victim != null && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0])))
				{
					victim.setFire(15);
					OutputHandler.chatConfirmation(sender, Localization.get(Localization.BURN_PLAYER));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
		}
		else if (args.length == 2)
		{
			if (args[0].toLowerCase().equals("me") && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".me")))
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
			else
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
				if (victim != null && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0])))
				{
					try
					{
						victim.setFire(Integer.parseInt(args[1]));
						OutputHandler.chatConfirmation(sender, Localization.get(Localization.BURN_PLAYER));
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
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
			EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				victim = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (victim != null)
			{
				victim.setFire(Integer.parseInt(args[1]));
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
