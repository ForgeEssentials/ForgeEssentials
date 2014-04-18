package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;

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
		ItemStack var6 = sender.getCurrentEquippedItem();
		if (var6 == null)
		{
			OutputHandler.chatError(sender, "You are not holding a valid item.");
			return;
		}

		if (args.length == 0)
		{
			String msg = "";
			for (Enchantment ench : Enchantment.enchantmentsList)
			{
				if (ench != null && ench.canApplyAtEnchantingTable(var6))
				{
					msg += StatCollector.translateToLocal(ench.getName()).replaceAll(" ", "") + ", ";
				}
			}
			msg = msg.substring(0, msg.length() - 2);
			Item held = var6.getItem();
			if(held instanceof ItemAxe)
			{
				msg += ", Sharpness, Smite, BaneofArthropods, Looting";
			}
			if(held instanceof ItemArmor)
			{
				if(!msg.contains("Thorns"))
					msg += ", Thorns";
				msg += ", Unbreaking";
			}
			if(held instanceof ItemBow)
			{
				msg += ", Unbreaking";
			}
			if(held instanceof ItemCarrotOnAStick || held instanceof ItemHoe ||
					held instanceof ItemFishingRod || held instanceof ItemFlintAndSteel)
			{
				msg = "Unbreaking";
			}
			if(held instanceof ItemShears)
			{
				msg = "Efficiency, SilkTouch, Unbreaking";
			}
			ChatUtils.sendMessage(sender, msg);
			return;
		}

		if(args[0].equalsIgnoreCase("listall"))
		{
			String msg = "";
			for (Enchantment ench : Enchantment.enchantmentsList)
			{
				if (ench != null)
				{
					msg += StatCollector.translateToLocal(ench.getName()).replaceAll(" ", "") + ", ";
				}
				if(msg.length() > 100)
				{
					msg = msg.substring(0, msg.length() - 2);
					ChatUtils.sendMessage(sender, msg);
					msg = "";
				}
			}
			msg = msg.substring(0, msg.length() - 2);
			ChatUtils.sendMessage(sender, msg);
			return;
		}

		Enchantment ench = null;

		for (Enchantment enchL : Enchantment.enchantmentsList)
		{
			if (enchL != null)
			{
				try
				{
					if (StatCollector.translateToLocal(enchL.getName()).replaceAll(" ", "").equalsIgnoreCase(args[0]))
					{
						Map map = EnchantmentHelper.getEnchantments(var6);
						if(map.containsKey(enchL.effectId))
						{
							map.remove(enchL.effectId);
							EnchantmentHelper.setEnchantments(map, var6);
						}
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
			OutputHandler.chatError(sender, String.format("'%s' is not a recognized enchantment.", args[0]));
			return;
		}

		int lvl = ench.getMaxLevel();
		if (args.length >= 2)
		{
			lvl = parseIntWithMin(sender, args[1], ench.getMinLevel());
		}

		var6.addEnchantment(ench, lvl);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
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
						temp.add(StatCollector.translateToLocal(ench.getName().replace(" ", "")));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/enchant [ench name] [lvl] Enchants the item you are currently holding.";
	}
}
