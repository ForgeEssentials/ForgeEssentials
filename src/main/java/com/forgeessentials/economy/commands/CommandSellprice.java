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

import net.minecraft.command.CommandException;
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
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ItemUtil;

public class CommandSellprice extends ParserCommandBase
{

    private static File priceFile = new File(ForgeEssentials.getFEDirectory(), "prices.txt");

    @Override
    public String getCommandName()
    {
        return "sellprice";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".sellprice";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/sellprice save|set: Manage item sell prices";
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
        return GameData.getItemRegistry().getNameForObject(item).toString();
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            calcPriceList(arguments, false);
            return;
        }

        arguments.tabComplete("save", "set");
        String subArg = arguments.remove().toLowerCase();
        switch (subArg)
        {
        case "save":
            calcPriceList(arguments, true);
            break;
        case "set":
            parseSetprice(arguments);
            break;
        default:
            break;
        }
    }

    public static void parseSetprice(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/sellprice set <item> <price>");
            return;
        }

        Item item = arguments.parseItem();
        double price = arguments.parseDouble();
        if (arguments.isTabCompletion)
            return;

        String itemId = getItemId(item);
        Map<String, Double> priceMap = loadPriceList(arguments);
        priceMap.put(itemId, price);
        writeMap(priceMap, priceFile);
        arguments.confirm(Translator.format("Set price for %s to %d", itemId, (int) price));
    }

    public static void calcPriceList(CommandParserArgs arguments, boolean save)
    {
        /*
         * Map<Item, Double> priceMap = new TreeMap<>(new Comparator<Item>() {
         * 
         * @Override public int compare(Item a, Item b) { try { String aId =
         * GameData.getItemRegistry().getNameForObject(a); String bId = GameData.getItemRegistry().getNameForObject(b);
         * return aId.compareTo(bId); } catch (Exception e) { return 0; } } });
         */
        Map<String, Double> priceMap = loadPriceList(arguments);
        Map<String, Double> priceMapFull = new TreeMap<>();

        File craftRecipesFile = new File(ForgeEssentials.getFEDirectory(), "craft_recipes.txt");
        File allPricesFile = new File(ForgeEssentials.getFEDirectory(), "prices_all.txt");
        File priceLogFile = new File(ForgeEssentials.getFEDirectory(), "prices_log.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(priceLogFile)))
        {
            try (BufferedWriter craftRecipes = new BufferedWriter(new FileWriter(craftRecipesFile)))
            {
                List<IRecipe> recipes = new ArrayList<>(CraftingManager.getInstance().getRecipeList());
                for (Iterator<IRecipe> iterator = recipes.iterator(); iterator.hasNext();)
                {
                    IRecipe recipe = iterator.next();
                    if (recipe.getRecipeOutput() == null)
                        continue;
                    List<?> recipeItems = getRecipeItems(recipe);
                    if (recipeItems == null)
                        continue;
                    craftRecipes
                            .write(String.format("%s:%d\n", getItemId(recipe.getRecipeOutput().getItem()), ItemUtil.getItemDamage(recipe.getRecipeOutput())));
                    for (Object stacks : recipeItems)
                        if (stacks != null)
                        {
                            ItemStack stack = null;
                            if (stacks instanceof List<?>)
                            {
                                if (!((List<?>) stacks).isEmpty())
                                    stack = (ItemStack) ((List<?>) stacks).get(0);
                            }
                            else
                                stack = (ItemStack) stacks;
                            if (stack != null)
                                craftRecipes.write(String.format("  %s:%d\n", getItemId(stack.getItem()), ItemUtil.getItemDamage(stack)));
                        }
                }
            }

            // for (Entry<String, Double> entry : priceMap.entrySet())
            // writer.write(String.format("%0$-40s = %d\n", entry.getKey(), (int) Math.floor(entry.getValue())));
            // writer.write("\n");
            // writer.write("\n");

            int iterateCount = 0;
            boolean changedAnyPrice;
            do
            {
                iterateCount++;
                if (iterateCount > 16)
                {
                    arguments.error("WARNING: Infinite loop found in recipes. Cannot calculate prices reliably!");
                    return;
                }
                changedAnyPrice = false;

                Map<ItemStack, ItemStack> furnaceRecipes = new HashMap<>(FurnaceRecipes.instance().getSmeltingList());
                List<IRecipe> recipes = new ArrayList<>(CraftingManager.getInstance().getRecipeList());

                boolean changedPrice;
                do
                {
                    changedPrice = false;
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
                            Double resultPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getRecipeOutput()));
                            if (resultPrice == null || price < resultPrice)
                            {
                                priceMap.put(ItemUtil.getItemIdentifier(recipe.getRecipeOutput()), price);
                                changedPrice = true;

                                String msg = String.format("%s:%d = %.0f -> %s", getItemId(recipe.getRecipeOutput().getItem()),
                                        ItemUtil.getItemDamage(recipe.getRecipeOutput()), resultPrice == null ? 0 : resultPrice, (int) price);
                                for (Object stacks : getRecipeItems(recipe))
                                    if (stacks != null)
                                    {
                                        ItemStack stack = null;
                                        if (stacks instanceof List<?>)
                                        {
                                            if (!((List<?>) stacks).isEmpty())
                                                stack = (ItemStack) ((List<?>) stacks).get(0);
                                        }
                                        else
                                            stack = (ItemStack) stacks;
                                        if (stack != null)
                                            msg += String.format("\n  %.0f - %s:%d", priceMap.get(ItemUtil.getItemIdentifier(stack)),
                                                    getItemId(stack.getItem()), ItemUtil.getItemDamage(stack));
                                    }
                                writer.write(msg + "\n");
                            }
                        }
                    }

                    for (Iterator<Entry<ItemStack, ItemStack>> iterator = furnaceRecipes.entrySet().iterator(); iterator.hasNext();)
                    {
                        Entry<ItemStack, ItemStack> recipe = iterator.next();
                        Double inPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getKey()));
                        if (inPrice != null)
                        {
                            iterator.remove();
                            double outPrice = inPrice * recipe.getKey().stackSize / recipe.getValue().stackSize;
                            Double resultPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getValue()));
                            if (resultPrice == null || outPrice < resultPrice)
                            {
                                priceMap.put(ItemUtil.getItemIdentifier(recipe.getValue()), outPrice);
                                writer.write(String.format("%s:%d = %.0f -> %d\n  %s\n", getItemId(recipe.getValue().getItem()), ItemUtil.getItemDamage(recipe
                                        .getValue()), resultPrice == null ? 0 : resultPrice, (int) outPrice, getItemId(recipe.getKey().getItem())));
                                changedPrice = true;
                            }
                        }
                    }
                    changedAnyPrice |= changedPrice;
                }
                while (changedPrice);
            }
            while (changedAnyPrice);
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

        if (save)
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
            arguments.confirm("Calculated and saved new price table");
        }
        else
        {
            arguments.confirm("Calculated new prices. Copy the prices you want to use from ./ForgeEssentials/prices.txt into Economy.cfg");
            arguments.confirm("You can also use [/calcpricelist save] to directly save the calculated prices");
        }
    }

    private static Map<String, Double> loadPriceList(CommandParserArgs arguments)
    {
        Map<String, Double> priceMap = new TreeMap<>();
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
            initializeDefaultPrices(priceMap);
        }
        return priceMap;
    }

    private static void initializeDefaultPrices(Map<String, Double> priceMap)
    {
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

    private static void writeMap(Map<String, Double> priceMap, File file)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (Entry<String, Double> entry : priceMap.entrySet())
            {
                String id = "I:\"" + entry.getKey() + "\"";
                while (id.length() < 50)
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
                        if (stack == null)
                            continue;
                        String id = ItemUtil.getItemIdentifier((ItemStack) stack);
                        priceMapFull.put(id, 0.0);
                        Double p = priceMap.get(id);
                        if (p != null && (itemPrice == null || p < itemPrice))
                            itemPrice = p;
                    }
                }
                else
                {
                    ItemStack stack = (ItemStack) stacks;
                    String id = ItemUtil.getItemIdentifier(stack);
                    if (id != null)
                    {
                        priceMapFull.put(id, 0.0);
                        itemPrice = priceMap.get(id);
                    }
                }
                if (itemPrice == null)
                    return -1;
                price += itemPrice;
            }
        return price;
    }
}
