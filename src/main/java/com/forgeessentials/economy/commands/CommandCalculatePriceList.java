package com.forgeessentials.economy.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;

import cpw.mods.fml.common.registry.GameData;

public class CommandCalculatePriceList extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "calcpricelist";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".calcpricelist";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/initprices";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<ItemStack> castItemStackList(List itemStackList)
    {
        return itemStackList;
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        Map<Item, Double> priceMap = new TreeMap<>(new Comparator<Item>() {
            @Override
            public int compare(Item a, Item b)
            {
                try
                {
                    String aId = GameData.getItemRegistry().getNameForObject(a);
                    String bId = GameData.getItemRegistry().getNameForObject(b);
                    return aId.compareTo(bId);
                }
                catch (Exception e)
                {
                    return 0;
                }
            }
        });

        File priceFile = new File(ForgeEssentials.getFEDirectory(), "prices.txt");
        File allPricesFile = new File(ForgeEssentials.getFEDirectory(), "prices_all.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(priceFile)))
        {
            Pattern pattern = Pattern.compile("I:\"([^\"]+)\"\\s*=\\s*(.*)");
            while (reader.ready())
            {
                String line = reader.readLine();
                Matcher match = pattern.matcher(line);
                if (!match.matches())
                    continue;
                Item item = GameData.getItemRegistry().getObject(match.group(1));
                if (item == null)
                    continue;
                try
                {
                    priceMap.put(item, Double.parseDouble(match.group(2)));
                }
                catch (NumberFormatException e)
                {
                    /* do nothing */
                }
            }
        }
        catch (IOException e)
        {
            arguments.warn(String.format("Could not load %s. Using default values", priceFile.getName()));

            priceMap.put(Item.getItemFromBlock(Blocks.cobblestone), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.deadbush), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.dirt), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.grass), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.ice), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.leaves), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.leaves2), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.mycelium), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.netherrack), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.sand), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.snow), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.stone), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.tallgrass), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.end_stone), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.glass), 1.0);
            priceMap.put(Item.getItemFromBlock(Blocks.gravel), 4.0);
            priceMap.put(Item.getItemFromBlock(Blocks.log), 32.0);
            priceMap.put(Item.getItemFromBlock(Blocks.log2), 32.0);
            priceMap.put(Item.getItemFromBlock(Blocks.wool), 48.0);

            priceMap.put(Items.flint, 4.0);
            priceMap.put(Items.clay_ball, 16.0);
            priceMap.put(Items.coal, 32.0);
            priceMap.put(Items.reeds, 32.0);
            priceMap.put(Items.feather, 48.0);
            priceMap.put(Items.redstone, 64.0);
            priceMap.put(Items.gold_ingot, 227.0);
            priceMap.put(Items.iron_ingot, 256.0);
            priceMap.put(Items.glowstone_dust, 384.0);
            priceMap.put(Items.blaze_rod, 1536.0);
            priceMap.put(Items.diamond, 8192.0);
        }

        @SuppressWarnings("unchecked")
        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

        @SuppressWarnings("unchecked")
        Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.smelting().getSmeltingList();

        boolean changed;
        do
        {
            changed = false;
            for (IRecipe recipe : recipes)
            {
                if (recipe.getRecipeOutput() == null)
                    continue;

                double price = 0;
                if (recipe instanceof ShapelessRecipes)
                {
                    price = getRecipePrice((ShapelessRecipes) recipe, priceMap);
                }
                else if (recipe instanceof ShapedRecipes)
                {
                    price = getRecipePrice((ShapedRecipes) recipe, priceMap);
                }
                else if (recipe instanceof ShapelessOreRecipe)
                {
                    price = getRecipePrice((ShapelessOreRecipe) recipe, priceMap);
                }
                else if (recipe instanceof ShapedOreRecipe)
                {
                    price = getRecipePrice((ShapedOreRecipe) recipe, priceMap);
                }
                if (price <= 0)
                    continue;
                price /= recipe.getRecipeOutput().stackSize;

                Double resultPrice = priceMap.get(recipe.getRecipeOutput().getItem());
                if (resultPrice == null || price < resultPrice)
                {
                    priceMap.put(recipe.getRecipeOutput().getItem(), price);
                    changed = true;
                }
            }

            for (Entry<ItemStack, ItemStack> recipe : furnaceRecipes.entrySet())
            {
                Double inPrice = priceMap.get(recipe.getKey().getItem());
                if (inPrice == null)
                    continue;
                double outPrice = inPrice * recipe.getKey().stackSize / recipe.getValue().stackSize;

                Double resultPrice = priceMap.get(recipe.getValue().getItem());
                if (resultPrice == null || outPrice < resultPrice)
                {
                    priceMap.put(recipe.getValue().getItem(), outPrice);
                    changed = true;
                }
            }

        }
        while (changed);

        writeMap(priceMap, priceFile);
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            if (!priceMap.containsKey(item))
                priceMap.put(item, 0.0);
        }
        writeMap(priceMap, allPricesFile);
        
        arguments.confirm("Calculated new prices. Copy the prices you want to use from ./ForgeEssentials/prices.txt into Economy.cfg");
    }

    private static void writeMap(Map<Item, Double> priceMap, File file)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (Entry<Item, Double> entry : priceMap.entrySet())
            {
                String id = "I:\"" + GameData.getItemRegistry().getNameForObject(entry.getKey()) + "\"";
                while (id.length() < 40)
                    id = id + ' ';
                writer.write(id + "=" + Integer.toString((int) Math.floor(entry.getValue())) + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static double getRecipePrice(ShapelessRecipes recipe, Map<Item, Double> priceMap)
    {
        double price = 0;
        for (ItemStack stack : castItemStackList(recipe.recipeItems))
        {
            Double itemPrice = priceMap.get(stack.getItem());
            if (itemPrice == null)
                return -1;
            price += stack.stackSize * itemPrice;
        }
        return price;
    }

    public static double getRecipePrice(ShapedRecipes recipe, Map<Item, Double> priceMap)
    {
        double price = 0;
        for (ItemStack stack : recipe.recipeItems)
            if (stack != null)
            {
                Double itemPrice = priceMap.get(stack.getItem());
                if (itemPrice == null)
                    return -1;
                price += stack.stackSize * itemPrice;
            }
        return price;
    }

    @SuppressWarnings("unchecked")
    public static double getRecipePrice(ShapelessOreRecipe recipe, Map<Item, Double> priceMap)
    {
        double price = 0;
        for (Object stacks : recipe.getInput())
            if (stacks != null)
            {
                Double itemPrice = null;
                if (stacks instanceof Collection<?>)
                {
                    for (ItemStack stack : (Collection<ItemStack>) stacks)
                    {
                        Double p = priceMap.get(stack.getItem());
                        if (p != null && (itemPrice == null || p < itemPrice))
                            itemPrice = p / stack.stackSize;
                    }
                }
                else
                {
                    ItemStack stack = (ItemStack) stacks;
                    itemPrice = priceMap.get(stack.getItem());
                    if (itemPrice != null)
                        itemPrice /= stack.stackSize;
                }
                if (itemPrice == null)
                    return -1;
                price += itemPrice;
            }
        return price;
    }

    @SuppressWarnings("unchecked")
    public static double getRecipePrice(ShapedOreRecipe recipe, Map<Item, Double> priceMap)
    {
        double price = 0;
        for (Object stacks : recipe.getInput())
            if (stacks != null)
            {
                Double itemPrice = null;
                if (stacks instanceof Collection<?>)
                {
                    for (ItemStack stack : (Collection<ItemStack>) stacks)
                    {
                        Double p = priceMap.get(stack.getItem());
                        if (p != null && (itemPrice == null || p < itemPrice))
                            itemPrice = p / stack.stackSize;
                    }
                }
                else
                {
                    ItemStack stack = (ItemStack) stacks;
                    itemPrice = priceMap.get(stack.getItem());
                    if (itemPrice != null)
                        itemPrice /= stack.stackSize;
                }
                if (itemPrice == null)
                    return -1;
                price += itemPrice;
            }
        return price;
    }
}
