package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author HoBoS_TaCo
 */
public class CommandAddToWallet extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "addtowallet";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("walletadd");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[0]);
			int amountToAdd = Integer.parseInt(args[1]);

			if (player == null)
			{
				ChatUtils.sendMessage(sender, "Player %s does not exist, or is not online.");
			}
			else
			{
				APIRegistry.wallet.addToWallet(amountToAdd, player.username);

				ChatUtils.sendMessage(sender, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + " added to wallet.");
				ChatUtils.sendMessage(player, amountToAdd + " " + APIRegistry.wallet.currency(amountToAdd) + " added to your wallet.");
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player> <amounttoadd>");
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
		return "fe.economy." + getCommandName();
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/walletadd <player> <amounttoadd> Add an amount to a wallet.";
	}
	
	@Override
	public RegGroup getReggroup() {
		// TODO Auto-generated method stub
		return RegGroup.OWNERS;
	}

}
