package com.forgeessentials.economy.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
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
        return "/calcpricelist [save]: Generate (and optionally directly apply) new price list";
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

    public static String getItemId(Item item)
    {
        return GameData.getItemRegistry().getNameForObject(item);
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        /*
         * Map<Item, Double> priceMap = new TreeMap<>(new Comparator<Item>() {
         * 
         * @Override public int compare(Item a, Item b) { try { String aId =
         * GameData.getItemRegistry().getNameForObject(a); String bId = GameData.getItemRegistry().getNameForObject(b);
         * return aId.compareTo(bId); } catch (Exception e) { return 0; } } });
         */
        Map<String, Double> priceMap = new TreeMap<>();
        Map<String, Double> priceMapFull = new TreeMap<>();

        File priceFile = new File(ForgeEssentials.getFEDirectory(), "prices.txt");
        File allPricesFile = new File(ForgeEssentials.getFEDirectory(), "prices_all.txt");
        File priceLogFile = new File(ForgeEssentials.getFEDirectory(), "prices_log.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(priceFile)))
        {
            Pattern pattern = Pattern.compile("\\s*I:\"([^\"]+)\"\\s*=\\s*(.*)");
            while (reader.ready())
            {
                String line = reader.readLine();
                Matcher match = pattern.matcher(line);
                if (!match.matches())
                    continue;
                try
                {
                    priceMap.put(match.group(1), Double.parseDouble(match.group(2)));
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

            priceMap.put("minecraft:wool", 48.0);
            for (int i = 1; i <= 15; i++)
                priceMap.put("minecraft:wool:" + i, 48.0);

            priceMap.put("minecraft:log", 32.0);
            for (int i = 1; i <= 6; i++)
                priceMap.put("minecraft:log:" + i, 32.0);
            priceMap.put("minecraft:log2", 32.0);
            for (int i = 1; i <= 6; i++)
                priceMap.put("minecraft:log2:" + i, 32.0);

            priceMap.put("minecraft:red_flower", 16.0);
            for (int i = 1; i <= 8; i++)
                priceMap.put("minecraft:red_flower:" + i, 16.0);

            priceMap.put("minecraft:dye", 8.0);
            for (int i = 1; i <= 15; i++)
                priceMap.put("minecraft:dye:" + i, 8.0);

            priceMap.put("minecraft:deadbush", 1.0);
            priceMap.put("minecraft:dirt", 1.0);
            priceMap.put("minecraft:grass", 1.0);
            priceMap.put("minecraft:ice", 1.0);
            priceMap.put("minecraft:leaves", 1.0);
            priceMap.put("minecraft:leaves2", 1.0);
            priceMap.put("minecraft:mycelium", 1.0);
            priceMap.put("minecraft:netherrack", 1.0);
            priceMap.put("minecraft:sand", 1.0);
            priceMap.put("minecraft:snow", 1.0);
            priceMap.put("minecraft:stone", 1.0);
            priceMap.put("minecraft:tallgrass", 1.0);
            priceMap.put("minecraft:end_stone", 1.0);

            priceMap.put("minecraft:apple", 128.0);
            priceMap.put("minecraft:beef", 64.0);
            priceMap.put("minecraft:blaze_rod", 1536.0);
            priceMap.put("minecraft:bone", 144.0);
            priceMap.put("minecraft:brown_mushroom", 32.0);
            priceMap.put("minecraft:cactus", 8.0);
            priceMap.put("minecraft:chicken", 64.0);
            priceMap.put("minecraft:clay_ball", 16.0);
            priceMap.put("minecraft:coal", 128.0);
            priceMap.put("minecraft:coal:1", 32.0);
            priceMap.put("minecraft:cobblestone", 1.0);
            priceMap.put("minecraft:cocoa", 128.0);
            priceMap.put("minecraft:diamond", 8192.0);
            priceMap.put("minecraft:dye:4", 864.0);
            priceMap.put("minecraft:emerald", 8192.0);
            priceMap.put("minecraft:feather", 48.0);
            priceMap.put("minecraft:fish", 64.0);
            priceMap.put("minecraft:flint", 4.0);
            priceMap.put("minecraft:glass", 1.0);
            priceMap.put("minecraft:glowstone_dust", 384.0);
            priceMap.put("minecraft:gold_ingot", 225.0);
            priceMap.put("minecraft:gravel", 4.0);
            priceMap.put("minecraft:egg", 32.0);
            priceMap.put("minecraft:ender_pearl", 1024.0);
            priceMap.put("minecraft:ghast_tear", 4096.0);
            priceMap.put("minecraft:gunpowder", 192.0);
            priceMap.put("minecraft:iron_ingot", 256.0);
            priceMap.put("minecraft:lava_bucket", 832.0);
            priceMap.put("minecraft:leather", 64.0);
            priceMap.put("minecraft:magma_cream", 792.0);
            priceMap.put("minecraft:melon", 16.0);
            priceMap.put("minecraft:melon_block", 144.0);
            priceMap.put("minecraft:milk_bucket", 833.0);
            priceMap.put("minecraft:obsidian", 64.0);
            priceMap.put("minecraft:porkchop", 64.0);
            priceMap.put("minecraft:pumpkin", 144.0);
            priceMap.put("minecraft:red_mushroom", 32.0);
            priceMap.put("minecraft:redstone", 64.0);
            priceMap.put("minecraft:reeds", 32.0);
            priceMap.put("minecraft:rotten_flesh", 24.0);
            priceMap.put("minecraft:sapling", 32.0);
            priceMap.put("minecraft:slime_ball", 24.0);
            priceMap.put("minecraft:soul_sand", 49.0);
            priceMap.put("minecraft:spider_eye", 128.0);
            priceMap.put("minecraft:string", 16.0);
            priceMap.put("minecraft:vine", 8.0);
            priceMap.put("minecraft:water_bucket", 769.0);
            priceMap.put("minecraft:waterlily", 16.0);
            priceMap.put("minecraft:web", 12.0);
            priceMap.put("minecraft:wheat", 24.0);
            priceMap.put("minecraft:yellow_flower", 16.0);
            
            // TODO: Prices below mainly guessed - should evaluate if these are good defaults
            priceMap.put("minecraft:potato", 16.0);
            priceMap.put("minecraft:carrot", 16.0);
            priceMap.put("minecraft:quartz", 128.0);
            priceMap.put("minecraft:sponge", 256.0);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(priceLogFile)))
        {
            for (Entry<String, Double> entry : priceMap.entrySet())
                writer.write(String.format("%0$-40s = %d\n", entry.getKey(), (int) Math.floor(entry.getValue())));
            writer.write("\n");
            writer.write("\n");

            @SuppressWarnings("unchecked")
            Map<ItemStack, ItemStack> furnaceRecipes = new HashMap<>(FurnaceRecipes.smelting().getSmeltingList());
            @SuppressWarnings("unchecked")
            List<IRecipe> recipes = new ArrayList<>(CraftingManager.getInstance().getRecipeList());

            boolean changed;
            do
            {
                changed = false;
                for (Iterator<IRecipe> iterator = recipes.iterator(); iterator.hasNext();)
                {
                    IRecipe recipe = iterator.next();
                    if (recipe.getRecipeOutput() == null)
                        continue;

                    double price = getRecipePrice(recipe, priceMap, priceMapFull);
                    if (price > 0)
                    {
                        iterator.remove();
                        price /= recipe.getRecipeOutput().stackSize;
                        Double resultPrice = priceMap.get(ModuleEconomy.getItemIdentifier(recipe.getRecipeOutput()));
                        if (resultPrice == null || price < resultPrice)
                        {
                            priceMap.put(ModuleEconomy.getItemIdentifier(recipe.getRecipeOutput()), price);
                            changed = true;

                            String msg = getItemId(recipe.getRecipeOutput().getItem()) + " = " + (int) price;
                            for (Object stacks : getRecipeItems(recipe))
                                if (stacks != null)
                                {
                                    ItemStack stack = (ItemStack) ((stacks instanceof List<?>) ? ((List<?>) stacks).get(0) : stacks);
                                    if (stack != null)
                                        msg += String.format("\n  %d - %s", stack.stackSize, getItemId(stack.getItem()));
                                }
                            writer.write(msg + "\n");
                        }
                    }
                }

                for (Iterator<Entry<ItemStack, ItemStack>> iterator = furnaceRecipes.entrySet().iterator(); iterator.hasNext();)
                {
                    Entry<ItemStack, ItemStack> recipe = iterator.next();
                    Double inPrice = priceMap.get(ModuleEconomy.getItemIdentifier(recipe.getKey()));
                    if (inPrice != null)
                    {
                        iterator.remove();
                        double outPrice = inPrice * recipe.getKey().stackSize / recipe.getValue().stackSize;
                        Double resultPrice = priceMap.get(ModuleEconomy.getItemIdentifier(recipe.getValue()));
                        if (resultPrice == null || outPrice < resultPrice)
                        {
                            priceMap.put(ModuleEconomy.getItemIdentifier(recipe.getValue()), outPrice);
                            writer.write(String.format("%s = %d\n  %s\n", //
                                    getItemId(recipe.getValue().getItem()), (int) outPrice, getItemId(recipe.getKey().getItem())));
                            changed = true;
                        }
                    }
                }
            }
            while (changed);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        writeMap(priceMap, priceFile);

        for (Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            String id = getItemId(item);
            if (!priceMapFull.containsKey(id))
                priceMapFull.put(id, 0.0);
        }
        priceMapFull.putAll(priceMap);
        writeMap(priceMapFull, allPricesFile);

        if (!arguments.isEmpty() && arguments.remove().equalsIgnoreCase("save"))
        {
            Configuration config = ForgeEssentials.getConfigManager().getConfig(ModuleEconomy.CONFIG_CATEGORY);
            ConfigCategory category = config.getCategory(ModuleEconomy.CATEGORY_ITEM);
            for (Entry<String, Double> entry : priceMap.entrySet())
            {
                category.put(entry.getKey(), new Property(entry.getKey(), Integer.toString((int) Math.floor(entry.getValue())), Type.INTEGER));
                APIRegistry.perms.registerPermissionProperty(ModuleEconomy.PERM_PRICE + "." + entry.getKey(),
                        Integer.toString((int) Math.floor(entry.getValue())));
            }
            config.save();
        } else {
            arguments.confirm("Calculated new prices. Copy the prices you want to use from ./ForgeEssentials/prices.txt into Economy.cfg");
            arguments.confirm("You can also use [/" + getCommandName() + " save] to directly save the calculated prices");
        }
    }

    private static void writeMap(Map<String, Double> priceMap, File file)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (Entry<String, Double> entry : priceMap.entrySet())
            {
                String id = "I:\"" + entry.getKey() + "\"";
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

    public static List<?> getRecipeItems(IRecipe recipe)
    {
        if (recipe instanceof ShapelessRecipes)
            return ((ShapelessRecipes) recipe).recipeItems;
        else if (recipe instanceof ShapedRecipes)
            return Arrays.asList(((ShapedRecipes) recipe).recipeItems);
        else if (recipe instanceof ShapedOreRecipe)
            return Arrays.asList(((ShapedOreRecipe) recipe).getInput());
        else if (recipe instanceof ShapelessOreRecipe)
            return ((ShapelessOreRecipe) recipe).getInput();
        else
            return null;
    }

    public static double getRecipePrice(IRecipe recipe, Map<String, Double> priceMap, Map<String, Double> priceMapFull)
    {
        double price = 0;
        List<?> stackList = getRecipeItems(recipe);
        if (stackList == null)
            return 0;
        for (Object stacks : stackList)
            if (stacks != null)
            {
                Double itemPrice = null;
                if (stacks instanceof Collection<?>)
                {
                    for (Object stack : (Collection<?>) stacks)
                    {
                        String id = ModuleEconomy.getItemIdentifier((ItemStack) stack);
                        priceMapFull.put(id, 0.0);
                        itemPrice = priceMap.get(id);
                    }
                }
                else
                {
                    ItemStack stack = (ItemStack) stacks;
                    String id = ModuleEconomy.getItemIdentifier(stack);
                    priceMapFull.put(id, 0.0);
                    itemPrice = priceMap.get(id);
                }
                if (itemPrice == null)
                    return -1;
                price += itemPrice;
            }
        return price;
    }
}
