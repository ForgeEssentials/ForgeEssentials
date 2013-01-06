package com.ForgeEssentials.core.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class BannedItems
{

	public void postLoad(FMLPostInitializationEvent e)
	{
		Configuration config = new Configuration(new File(
				ForgeEssentials.fedirloc, "banneditems.cfg"));

		config.addCustomCategoryComment("BannedItems",
				"Configuration options to remove an item's crafting recipe.");
		Property p = config.get("BannedItems", "itemList", "");
		p.comment = "List of items that are banned, in the format \"id[:meta];id[:meta]\" Use a meta value of -1 to ban ALL variants of an item/block.";

		config.save();

		String[] list = p.value.split(";");
		ArrayList<ItemStack> items = new ArrayList();
		int id;
		int meta;
		// Decompose list into (item ID, Meta) pairs.
		for (String s : list)
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
						} catch (Exception ex)
						{
							meta = 0;
						}
					}
				} catch (Exception ex)
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
		List<IRecipe> minecraftRecipes = CraftingManager.getInstance()
				.getRecipeList();
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
					if (result.itemID == bannedItem.itemID
							&& (bannedItem.getItemDamage() == -1 || result
									.getItemDamage() == bannedItem
									.getItemDamage()))
					{
						minecraftRecipes.remove(i);
						--i;
					}
				}
			}
		}
	}

}
