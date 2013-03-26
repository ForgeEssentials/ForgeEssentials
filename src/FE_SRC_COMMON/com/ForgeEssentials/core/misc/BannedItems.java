package com.ForgeEssentials.core.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerZone;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.google.common.collect.HashMultimap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class BannedItems
{
	private static final String		BYPASS	= "ForgeEssentials.BannedItems.override";

	HashMultimap<Integer, Integer>	noUse	= HashMultimap.create();
	List<String>					noCraft	= new ArrayList<String>();

	@PermRegister
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(BYPASS, RegGroup.OWNERS);
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void click(PlayerInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		if (PermissionsAPI.checkPermAllowed(new PermQueryPlayerZone(e.entityPlayer, BYPASS, ZoneManager.getWhichZoneIn(new WorldPoint(e.entityPlayer)))))
			return;

		ItemStack is = e.entityPlayer.inventory.getCurrentItem();
		if (is != null)
		{
			if (noUse.containsKey(is.itemID))
			{
				if (noUse.get(is.itemID).contains(is.getItemDamage()))
				{
					if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerZone(e.entityPlayer, BYPASS + "." + is.itemID + ":" + is.getItemDamage(), ZoneManager.getWhichZoneIn(new WorldPoint(e.entityPlayer)))))
					{
						e.entityPlayer.sendChatToPlayer("That item is banned.");
						e.setCanceled(true);
					}
				}
				if (noUse.get(is.itemID).contains(-1))
				{
					if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerZone(e.entityPlayer, BYPASS + "." + is.itemID + ":-1", ZoneManager.getWhichZoneIn(new WorldPoint(e.entityPlayer)))))
					{
						e.entityPlayer.sendChatToPlayer("That item is banned.");
						e.setCanceled(true);
					}
				}
			}
		}
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		Configuration config = new Configuration(new File(ForgeEssentials.FEDIR, "banneditems.cfg"));

		config.addCustomCategoryComment("NoCraft", "Configuration options to remove an item's crafting recipe.");
		config.addCustomCategoryComment("NoUse", "Configuration options to make an item unusable.");

		noCraft = Arrays.asList(config.get("NoCraft", "List", new String[] {}, "Use this format: \"id:meta\". Use meta -1 to ban ALL variants of an item/block.").valueList);
		List<String> temp = Arrays.asList(config.get("NoUse", "List", new String[] {}, "Use this format: \"id:meta\". Use meta -1 to ban ALL variants of an item/block.").valueList);

		config.save();
		int id;
		int meta;

		for (String s : temp)
		{
			id = meta = 0;
			String[] tmp = s.split(":");
			if (tmp != null && tmp.length > 0)
			{
				try
				{
					id = Integer.parseInt(tmp[0]);
					if (tmp.length > 1)
					{
						try
						{
							meta = Integer.parseInt(tmp[1]);
						}
						catch (Exception ex)
						{
							meta = 0;
						}
					}
				}
				catch (Exception ex)
				{
					id = 0;
				}
			}

			if (id != 0)
			{
				noUse.put(id, meta);
			}
		}

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		// Decompose list into (item ID, Meta) pairs.
		for (String s : noCraft)
		{
			id = meta = 0;
			String[] tmp = s.split(":");
			if (tmp != null && tmp.length > 0)
			{
				try
				{
					id = Integer.parseInt(tmp[0]);
					if (tmp.length > 1)
					{
						try
						{
							meta = Integer.parseInt(tmp[1]);
						}
						catch (Exception ex)
						{
							meta = 0;
						}
					}
				}
				catch (Exception ex)
				{
					id = 0;
				}
			}

			if (id != 0)
			{
				items.add(new ItemStack(id, 1, meta));
			}
		}

		// Iterate over recipe list, and remove a recipe when its output matches
		// one of our ItemStacks.
		List<IRecipe> minecraftRecipes = CraftingManager.getInstance().getRecipeList();
		ItemStack result;
		for (int i = 0; i < minecraftRecipes.size(); ++i)
		{
			IRecipe tmp = minecraftRecipes.get(i);
			result = tmp.getRecipeOutput();

			if (result != null)
			{
				for (ItemStack bannedItem : items)
				{
					// Remove the item if the ID & meta match, OR if the IDs
					// match, and banned meta is -1.
					if (result.itemID == bannedItem.itemID && (bannedItem.getItemDamage() == -1 || result.getItemDamage() == bannedItem.getItemDamage()))
					{
						minecraftRecipes.remove(i);
						OutputHandler.finer("Recipes removed for item " + bannedItem.itemID);
						--i;
					}
				}
			}
		}
	}
}
