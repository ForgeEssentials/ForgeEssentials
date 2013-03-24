package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandI extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "i";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "item" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int id = 1;
		int amount = 64;
		int dam = 0;
		EntityPlayer receiver = sender;

		if (args.length == 2)
		{
			amount = parseInt(sender, args[1]);
		}

		if (args.length > 0)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[0], false);
			id = idAndMeta[0];
			if (idAndMeta[1] == -1)
			{
				dam = 0;
			}
			else
			{
				dam = idAndMeta[1];
			}

			ItemStack stack = new ItemStack(id, amount, dam);

			String name = Item.itemsList[id].func_77653_i(stack);
			OutputHandler.chatConfirmation(sender, Localization.format("command.i.given", amount, name));
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
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
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
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

}
