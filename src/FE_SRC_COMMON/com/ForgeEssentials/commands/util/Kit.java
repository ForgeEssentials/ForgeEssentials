package com.ForgeEssentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class Kit
{
	@UniqueLoadingKey
	@SaveableField
	private String		name;

	@SaveableField
	private Integer		cooldown;

	@SaveableField
	private ItemStack[]	items;

	@SaveableField
	private ItemStack[]	armor;

	public Kit(EntityPlayer player, String name, int cooldown)
	{
		this.cooldown = cooldown;
		this.name = name;

		List<ItemStack> items = new ArrayList<ItemStack>();

		for (int i = 0; i < player.inventory.mainInventory.length; i++)
		{
			if (player.inventory.mainInventory[i] != null)
				items.add(player.inventory.mainInventory[i]);
		}

		this.items = new ItemStack[items.size()];
		this.armor = new ItemStack[player.inventory.armorInventory.length];

		for (int i = 0; i < items.size(); i++)
		{
			this.items[i] = items.get(i);
		}

		for (int i = 0; i < 4; i++)
		{
			if (player.inventory.armorInventory[i] != null)
				armor[i] = player.inventory.armorInventory[i].copy();
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
		return items.clone();
	}

	public ItemStack[] getArmor()
	{
		return armor.clone();
	}
}
