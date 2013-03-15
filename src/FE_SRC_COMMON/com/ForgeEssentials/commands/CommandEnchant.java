package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandEnchant extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "enchant";
	}
	
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length == 0)
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
		
		Enchantment var7 = null;

		for (Enchantment ench : Enchantment.enchantmentsList)
		{
			if (ench != null)
			{
				try
				{
					if (ench.getName().replaceAll("enchantment.", "").equalsIgnoreCase(args[0]))
					{
						var7 = ench;
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		if (var7 == null)
		{
			int var4 = parseIntBounded(sender, args[0], 0, Enchantment.enchantmentsList.length - 1);
			var7 = Enchantment.enchantmentsList[var4];
		}
		int var5 = 1;
		ItemStack var6 = sender.getCurrentEquippedItem();
		if (var6 == null)
		{
			OutputHandler.chatError(sender, Localization.get("commands.enchant.noItem"));
			return;
		}
		if (var7 == null)
		{
			OutputHandler.chatError(sender, Localization.get("commands.enchant.notFound"));
			return;
		}
		if (!var7.type.canEnchantItem(var6.getItem()))
		{
			OutputHandler.chatError(sender, Localization.get("commands.enchant.cantEnchant"));
			return;
		}
		
		if (args.length >= 3)
		{
			var5 = parseIntWithMin(sender, args[1], var7.getMinLevel());
			if (var5 > var7.getMaxLevel())
			{
				var5 = var7.getMaxLevel();
			}
		}

		if (var6.hasTagCompound())
		{
			NBTTagList var8 = var6.getEnchantmentTagList();

			if (var8 != null)
			{
				for (int var9 = 0; var9 < var8.tagCount(); ++var9)
				{
					short var10 = ((NBTTagCompound) var8.tagAt(var9)).getShort("id");

					if (Enchantment.enchantmentsList[var10] != null)
					{
						Enchantment var11 = Enchantment.enchantmentsList[var10];

						if (!var11.canApplyTogether(var7))
						{
							notifyAdmins(sender, "commands.enchant.cantCombine", new Object[]
							{ var7.getTranslatedName(var5), var11.getTranslatedName(((NBTTagCompound) var8.tagAt(var9)).getShort("lvl")) });
							return;
						}
					}
				}
			}
		}
		
		var6.addEnchantment(var7, var5);
		OutputHandler.chatConfirmation(sender, Localization.get("commands.enchant.success"));
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
}
