package com.ForgeEssentials.economy.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.economy.Wallet;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGetWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return Localization.get(Localization.WALLET_GET);
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			
			if (player == null)
			{
				OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_NOPLAYER)));
			}
			else
			{			
				int wallet = Wallet.getWallet(player);
				
				if (sender != player)
				{
					sender.sendChatToPlayer(player.username + Localization.get(Localization.WALLET_GET_TARGET) + wallet + " " + Wallet.currency(wallet));
				}
				player.sendChatToPlayer(Localization.get(Localization.WALLET_GET_SELF) + wallet + " " + Wallet.currency(wallet));
			}
		} 
		else
		{
			OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			
			if (player == null)
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_NOPLAYER));
			}
			else
			{
				int wallet = Wallet.getWallet(player);
				sender.sendChatToPlayer(player.username + Localization.get(Localization.WALLET_GET_TARGET) + wallet + " " + Wallet.currency(wallet));
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
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Economy." + getCommandName();
	}
}
