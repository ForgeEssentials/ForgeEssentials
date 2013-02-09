package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandEnchant extends ForgeEssentialsCommandBase
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return PermissionsAPI.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, getCommandPerm()));
		}
		else
		{
			return true;
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		if (args.length == 2)
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

	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public String getCommandName()
	{
		return "enchant";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length < 2)
		{
			throw new WrongUsageException("commands.enchant.usage", new Object[0]);
		}
		else
		{
			EntityPlayerMP var3 = func_82359_c(sender, args[0]);
			Enchantment var7 = null;

			for (Enchantment ench : Enchantment.enchantmentsList)
			{
				if (ench != null)
				{
					try
					{
						if (ench.getName().replaceAll("enchantment.", "").equalsIgnoreCase(args[1]))
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
				int var4 = parseIntBounded(sender, args[1], 0, Enchantment.enchantmentsList.length - 1);
				var7 = Enchantment.enchantmentsList[var4];
			}
			int var5 = 1;
			ItemStack var6 = var3.getCurrentEquippedItem();

			if (var6 == null)
			{
				notifyAdmins(sender, "commands.enchant.noItem", new Object[0]);
			}
			else
			{
				if (var7 == null)
				{
					throw new NumberInvalidException("commands.enchant.notFound", new Object[] {});
				}
				else if (!var7.func_92089_a(var6))
				{
					notifyAdmins(sender, "commands.enchant.cantEnchant", new Object[0]);
				}
				else
				{
					if (args.length >= 3)
					{
						var5 = parseIntBounded(sender, args[2], var7.getMinLevel(), var7.getMaxLevel());
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
					notifyAdmins(sender, "commands.enchant.success", new Object[0]);
				}
			}
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
}
