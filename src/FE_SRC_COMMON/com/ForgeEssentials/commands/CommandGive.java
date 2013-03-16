package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGive extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "give";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length < 2 || args.length > 3)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
		
		int id = 1;
		int amount = 64;
		int dam = 0;
		
		// Amount is specified
		if(args.length == 3)
		{
			amount = parseIntBounded(sender, args[2], 0, 64);
		}
		
		// Parse the item
		int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
		id = idAndMeta[0];
		dam = idAndMeta[1];
		
		List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
		if (PlayerSelector.hasArguments(args[0]))
		{
			players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
		}
		if (players.size() != 0)
		{
			for (EntityPlayer player : players)
			{
				ItemStack stack = new ItemStack(id, amount, dam);
				
				String name = Item.itemsList[id].func_77653_i(stack);
				sender.sendChatToPlayer("Giving " + player.username + " " + amount + " " + name);
				player.inventory.addItemStackToInventory(stack);
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length < 2 || args.length > 3)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
		
		int id = 1;
		int amount = 64;
		int dam = 0;
		
		// Amount is specified
		if(args.length == 3)
		{
			amount = parseIntBounded(sender, args[2], 0, 64);
		}
		
		// Parse the item
		int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
		id = idAndMeta[0];
		dam = idAndMeta[1];
		
		List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
		if (PlayerSelector.hasArguments(args[0]))
		{
			players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
		}
		if (players.size() != 0)
		{
			for (EntityPlayer player : players)
			{
				ItemStack stack = new ItemStack(id, amount, dam);
				
				String name = Item.itemsList[id].func_77653_i(stack);
				sender.sendChatToPlayer("Giving " + player.username + " " + amount + " " + name);
				player.inventory.addItemStackToInventory(stack);
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, ItemList.instance().getItemList());
		else if (args.length == 3)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}
}
