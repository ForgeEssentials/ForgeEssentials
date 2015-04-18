package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

public class Kit {
	
	private String name;

	private Integer cooldown;

	private ItemStack[] items;

	private ItemStack[] armor;

	public Kit(EntityPlayer player, String name, int cooldown)
	{
		this.cooldown = cooldown;
		this.name = name;

		List<ItemStack> inventory = new ArrayList<ItemStack>();

		for (int i = 0; i < player.inventory.mainInventory.length; i++)
		{
			if (player.inventory.mainInventory[i] != null)
			{
				inventory.add(player.inventory.mainInventory[i]);
			}
		}

		this.items = new ItemStack[inventory.size()];

		for (int i = 0; i < inventory.size(); i++)
		{
			this.items[i] = inventory.get(i);
		}

        armor = new ItemStack[player.inventory.armorInventory.length];

		for (int i = 0; i < 4; i++)
		{
			if (player.inventory.armorInventory[i] != null)
			{
				this.armor[i] = player.inventory.armorInventory[i].copy();
			}
		}

		CommandDataManager.addKit(this);
	}

	public String getName()
	{
		return name;
	}

	public Integer getCooldown()
	{
		return cooldown;
	}

	public ItemStack[] getItems()
	{
		return items;
	}

	public ItemStack[] getArmor()
	{
		return armor;
	}

	public void giveKit(EntityPlayer player)
	{
		if (PlayerInfo.getPlayerInfo(player.getPersistentID()).getKitCooldown().containsKey(getName()))
		{
			OutputHandler.chatWarning(
					player,
					"Kit cooldown active, %c seconds to go!".replaceAll("%c",
							"" + FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.getPersistentID()).getKitCooldown().get(getName()))));
		}
		else
		{
			if (!PermissionsManager.checkPermission(player, CommandsEventHandler.BYPASS_KIT_COOLDOWN))
			{
				PlayerInfo.getPlayerInfo(player.getPersistentID()).getKitCooldown().put(getName(), getCooldown());
			}

			/*
			 * Main inv.
			 */

			for (ItemStack stack : getItems())
			{
				if (player.inventory.addItemStackToInventory(ItemStack.copyItemStack(stack)))
				{
					System.out.println(stack.getDisplayName());
				}
				else
				{
					System.out.println("Couldn't give " + stack.getDisplayName());
				}
			}

			/*
			 * Armor
			 */
			for (int i = 0; i < 4; i++)
			{
				if (getArmor()[i] != null)
				{
					ItemStack stack = getArmor()[i];
					if (player.inventory.armorInventory[i] == null)
					{
						player.inventory.armorInventory[i] = stack;
					}
					else
					{
						player.inventory.addItemStackToInventory(ItemStack.copyItemStack(stack));
					}
				}
			}

			OutputHandler.chatConfirmation(player, "Kit dropped.");
		}
	}
}
