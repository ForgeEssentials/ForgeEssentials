package com.forgeessentials.commands.util;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.List;

@SaveableObject
public class Kit {
	
	@UniqueLoadingKey
	@SaveableField
	private String name;

	@SaveableField
	private Integer cooldown;

	@SaveableField
	private ItemStack[] items;

	@SaveableField
	private ItemStack[] armor;

	public Kit(EntityPlayer player, String name, int cooldown)
	{
		this.cooldown = cooldown;
		this.name = name;

		List<ItemStack> items = new ArrayList<ItemStack>();

		for (int i = 0; i < player.inventory.mainInventory.length; i++)
		{
			if (player.inventory.mainInventory[i] != null)
			{
				items.add(player.inventory.mainInventory[i]);
			}
		}

		this.items = new ItemStack[items.size()];

		for (int i = 0; i < items.size(); i++)
		{
			this.items[i] = items.get(i);
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

	private Kit(Object name, Object cooldown, Object items, Object armor)
	{
		this.name = (String) name;
		this.cooldown = (Integer) cooldown;

		this.items = new ItemStack[((Object[]) items).length];
		this.armor = new ItemStack[4];

		for (ItemStack is : (ItemStack[]) items)
		{
			for (int i = 0; i < ((ItemStack[]) items).length; i++)
			{
				this.items[i] = is;
			}
		}
        for (ItemStack is : (ItemStack[]) armor)
        {
            if(is != null) {
                // Armor slots seemed to be inverted
                int atype = 3-((ItemArmor) is.getItem()).armorType;
                this.armor[atype] = is;
            }
        }
	}

	@Reconstructor
	private static Kit reconstruct(IReconstructData tag)
	{
		return new Kit(tag.getFieldValue("name"), tag.getFieldValue("cooldown"), tag.getFieldValue("items"), tag.getFieldValue("armor"));
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
