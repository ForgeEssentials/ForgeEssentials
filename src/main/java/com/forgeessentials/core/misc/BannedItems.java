package com.forgeessentials.core.misc;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BannedItems {
    List<String> noCraft = new ArrayList<String>();

    @SuppressWarnings("unchecked")
    public void postLoad(FMLPostInitializationEvent e)
    {
        Configuration config = new Configuration(new File(ForgeEssentials.FEDIR, "banneditems.cfg"));

        config.addCustomCategoryComment("NoCraft", "Configuration options to remove an item's crafting recipe.");
        //config.addCustomCategoryComment("NoUse", "Configuration options to make an item unusable.");

        noCraft = Arrays
                .asList(config.get("NoCraft", "List", new String[] { }, "Use this format: \"id:meta\". Use meta -1 to ban ALL variants of an item/block.")
                        .getStringList());

        config.save();
        int id;
        int meta;

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
                        OutputHandler.felog.finer("Recipes removed for item " + bannedItem.itemID);
                        --i;
                    }
                }
            }
        }
    }
}
