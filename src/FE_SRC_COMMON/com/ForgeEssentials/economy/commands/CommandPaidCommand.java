package com.ForgeEssentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandPaidCommand extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "paidcommand";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("pc", "pcmd");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
	}

	/*
	 * Expected structure: "/paidcommand <player> <amount> <command [args]>"
	 */
	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		System.out.print(sender);
		if (args.length >= 3)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				int amount = parseIntWithMin(sender, args[1], 0);
				if (APIRegistry.wallet.getWallet(player.username) >= amount)
				{
					APIRegistry.wallet.removeFromWallet(amount, player.username);
					// Do command in name of player

					StringBuilder cmd = new StringBuilder(args.toString().length());
					for (int i = 2; i < args.length; i++)
					{
						cmd.append(args[i]);
						cmd.append(" ");
					}

					MinecraftServer.getServer().executeCommand(cmd.toString());
					OutputHandler.chatConfirmation(player, "That cost you " + amount + " " + APIRegistry.wallet.currency(amount));
				}
				else
				{
					OutputHandler.chatError(player, "You can't afford that!!");
				}
			}
			else
			{
				//this should be removed
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			//this should be removed
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
		return null;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return false;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

}
