package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandEnchant extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "enchant";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			String msg = "";
			for (Enchantment ench : Enchantment.enchantmentsList)
			{
				if (ench != null)
				{
					msg = ench.getName().replaceAll("enchantment.", "") + ", " + msg;
				}
			}
			sender.sendChatToPlayer(msg);
			return;
		}

		Enchantment ench = null;

		for (Enchantment enchL : Enchantment.enchantmentsList)
		{
			if (enchL != null)
			{
				try
				{
					if (enchL.getName().replaceAll("enchantment.", "").equalsIgnoreCase(args[0]))
					{
						ench = enchL;
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		if (ench == null)
		{
			int var4 = parseIntBounded(sender, args[0], 0, Enchantment.enchantmentsList.length - 1);
			ench = Enchantment.enchantmentsList[var4];
		}
		if (ench == null)
		{
			OutputHandler.chatError(sender, Localization.format("commands.enchant.notFound", args[0]));
			return;
		}

		ItemStack var6 = sender.getCurrentEquippedItem();
		if (var6 == null)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOITEMPLAYER));
			return;
		}

		int lvl = ench.getMaxLevel();
		if (args.length >= 3)
		{
			lvl = parseIntWithMin(sender, args[1], ench.getMinLevel());
		}

		var6.addEnchantment(ench, lvl);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			ArrayList<String> temp = new ArrayList<String>();
			for (Enchantment ench : Enchantment.enchantmentsList)
			{
				if (ench != null)
				{
					try
					{
						temp.add(ench.getName().replaceAll("enchantment.", ""));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return getListOfStringsFromIterableMatchingLastWord(args, temp);
		}
		return null;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
