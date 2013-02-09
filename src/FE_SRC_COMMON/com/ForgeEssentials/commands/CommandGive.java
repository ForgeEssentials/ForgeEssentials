package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
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
		int id = 1;
		int amount = 64;
		int dam = 0;
		EntityPlayer receiver = sender;

		if (args.length == 4)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
			id = idAndMeta[0];
			try
			{
				dam = Integer.parseInt(args[3]);
			}
			catch(NumberFormatException e)
			{
				OutputHandler.chatError(sender, "Damage value is not a number: " + args[3]);
			}
		}
		if (args.length >= 3)
		{
			amount = parseIntBounded(sender, args[2], 0, 64);
		}

		if (args.length > 1)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
			id = idAndMeta[0];
			if (idAndMeta[1] == -1)
			{
				dam = 0;
			}
			else
			{
				dam = idAndMeta[1];
			}
			receiver = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				PlayerSelector.matchOnePlayer(sender, args[0]);
			}

			ItemStack stack = new ItemStack(id, amount, dam);

			String name = Item.itemsList[id].func_77653_i(stack);
			String uname = (receiver.getCommandSenderName().equalsIgnoreCase(sender.getCommandSenderName()) ? "you" : receiver.username);
			sender.sendChatToPlayer("Giving " + uname + " " + amount + " " + name);
			receiver.inventory.addItemStackToInventory(stack);
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int id = 1;
		int amount = 64;
		int dam = 0;
		EntityPlayer receiver;

		if (args.length == 4)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
			id = idAndMeta[0];
			try
			{
				dam = Integer.parseInt(args[4]);
			}
			catch(NumberFormatException e)
			{
				sender.sendChatToPlayer("Damage value is not a number: " + args[4]);
			}
		}
		if (args.length == 3)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
			id = idAndMeta[0];
			if (idAndMeta[1] == -1)
			{
				dam = 0;
			}
			else
			{
				dam = idAndMeta[1];
			}
		}
		if (args.length > 1)
		{
			receiver = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				receiver = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			
	
			amount = parseIntBounded(sender, args[2], 0, 64);
	
			ItemStack stack = new ItemStack(id, amount, dam);

			String name = Item.itemsList[id].func_77653_i(stack);
			String uname = (receiver.getCommandSenderName().equalsIgnoreCase(sender.getCommandSenderName()) ? "you" : receiver.username);
			sender.sendChatToPlayer("Giving " + uname + " " + amount + " " + name);
			receiver.inventory.addItemStackToInventory(stack);
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
			return getListOfStringsFromIterableMatchingLastWord(args, ItemList.instance().getItemList());
		}
		else if (args.length == 3)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
