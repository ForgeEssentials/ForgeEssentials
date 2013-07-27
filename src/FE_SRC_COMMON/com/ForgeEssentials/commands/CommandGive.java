package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.misc.FriendlyItemList;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGive extends FEcmdModuleCommands
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
		if (args.length == 3)
		{
			amount = parseIntBounded(sender, args[2], 0, 64);
		}

		// Parse the item
		int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
		id = idAndMeta[0];
		dam = idAndMeta[1];
		if (dam == -1)
		{
			dam = 0;
		}
		EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player != null)
		{
			ItemStack stack = new ItemStack(id, amount, dam);
			player.inventory.addItemStackToInventory(stack.copy());
			String name = Item.itemsList[id].getItemStackDisplayName(stack);
			OutputHandler.chatConfirmation(sender, Localization.format("command.give.given", args[0], amount, name));
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
		if (args.length == 3)
		{
			amount = parseIntBounded(sender, args[2], 0, 64);
		}

		// Parse the item
		int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
		id = idAndMeta[0];
		dam = idAndMeta[1];

		if (dam == -1)
		{
			dam = 0;
		}

		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player != null)
		{
			ItemStack stack = new ItemStack(id, amount, dam);
			player.inventory.addItemStackToInventory(stack.copy());
			String name = Item.itemsList[id].getItemStackDisplayName(stack);
			OutputHandler.chatConfirmation(sender, Localization.format("command.give.given", args[0], amount, name));
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
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else if (args.length == 2)
			return getListOfStringsFromIterableMatchingLastWord(args, FriendlyItemList.instance().getItemList());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
