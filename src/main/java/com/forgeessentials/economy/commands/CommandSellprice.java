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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandSellprice extends ForgeEssentialsCommandBuilder
{

    public CommandSellprice(boolean enabled)
    {
        super(enabled);
    }

    private static File priceFile = new File(ForgeEssentials.getFEDirectory(), "prices.txt");
    public static List<String> colors = Arrays.asList("white", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");
    public static List<String> woodTypes = Arrays.asList("oak", "spruce", "birch", "jungle", "acacia", "dark_oak");

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "sellprice";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("save").executes(CommandContext -> execute(CommandContext, "save")))
                .then(Commands.literal("set")
                        .then(Commands.argument("item", ItemArgument.item())
                                .then(Commands.argument("price", DoubleArgumentType.doubleArg())
                                        .executes(CommandContext -> execute(CommandContext, "set")))))
                .then(Commands.literal("generate").executes(CommandContext -> execute(CommandContext, "generate")))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/sellprice save                 -Save generated prices to config");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/sellprice set <item> <price>  -Set a price for an item");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/sellprice generate            -Generate default prices");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("generate"))
        {
            calcPriceList(ctx, false);
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("save"))
        {
            writeToConfig(ctx.getSource(), loadPriceList(ctx));
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("set"))
        {
            parseSetprice(ctx);
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseSetprice(CommandContext<CommandSourceStack> ctx) throws CommandRuntimeException
    {
        Item item = ItemArgument.getItem(ctx, "item").getItem();
        double price = DoubleArgumentType.getDouble(ctx, "price");

        String itemId = ItemUtil.getItemName(item);
        Map<String, Double> priceMap = loadPriceList(ctx);
        priceMap.put(itemId, price);
        writeMap(priceMap, priceFile);
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("Set price for %s to %d", itemId, (int) price));
    }

    public static void calcPriceList(CommandContext<CommandSourceStack> ctx, boolean save)
    {
        /*
         * Map<Item, Double> priceMap = new TreeMap<>(new Comparator<Item>() {
         * 
         * @Override public int compare(Item a, Item b) { try { String aId = Item.REGISTRY.getNameForObject(a); String bId = Item.REGISTRY.getNameForObject(b); return
         * aId.compareTo(bId); } catch (Exception e) { return 0; } } });
         */
        Map<String, Double> priceMap = loadPriceList(ctx);
        Map<String, Double> priceMapFull = new TreeMap<>();

        File craftRecipesFile = new File(ForgeEssentials.getFEDirectory(), "craft_recipes.txt");
        File allPricesFile = new File(ForgeEssentials.getFEDirectory(), "prices_all.txt");
        File priceLogFile = new File(ForgeEssentials.getFEDirectory(), "prices_log.txt");
        try
        {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(priceLogFile)))
            {
                try (BufferedWriter craftRecipes = new BufferedWriter(new FileWriter(craftRecipesFile)))
                {
                    RecipeManager manager= ServerLifecycleHooks.getCurrentServer().overworld().getRecipeManager();
                    Collection<Recipe<?>> recpies = manager.getRecipes();
                    for (Recipe<?> recipe : recpies)
                    {
                        if (recipe.getResultItem() == ItemStack.EMPTY)
                        {
                            continue;
                        }
                        List<Ingredient> recipeItems = getRecipeItems(recipe);
                        if (recipeItems.isEmpty())
                        {
                            continue;
                        }
                        craftRecipes
                                .write(String.format("%s\n", ItemUtil.getItemName(recipe.getResultItem().getItem())));
                        for (Ingredient ingredient : recipeItems)
                        {
                            if (ingredient != null)
                            {
                                ItemStack stack = null;
                                ItemStack[] stacks = ingredient.getItems();
                                if (stacks != null && stacks.length > 0)
                                {
                                    stack = stacks[0];
                                }

                                if (stack != ItemStack.EMPTY)
                                {
                                    try
                                    {
                                        if (stack.getItem() != Items.AIR || stack.getItem() != null)
                                        {
                                            craftRecipes.write(
                                                    String.format("  :%s\n", ItemUtil.getItemName(stack.getItem())));
                                        }
                                    }
                                    catch (NullPointerException e)
                                    {
                                        // This happens if a crafting recipe has a 'blank' spot in it, like a chest with
                                        // a center 'hole' in the recipe
                                        LoggingHandler.felog.debug("Found null itemstack in recipe, not a problem");
                                    }
                                }
                            }
                        }
                    }
                }

                // for (Entry<String, Double> entry : priceMap.entrySet())
                // writer.write(String.format("%0$-40s = %d\n", entry.getKey(), (int)
                // Math.floor(entry.getValue())));
                // writer.write("\n");
                // writer.write("\n");

                int iterateCount = 0;
                boolean changedAnyPrice;
                do
                {
                    iterateCount++;
                    if (iterateCount > 16)
                    {
                        ChatOutputHandler.chatError(ctx.getSource(),
                                "WARNING: Infinite loop found in recipes. Cannot calculate prices reliably!");
                        return;
                    }
                    changedAnyPrice = false;

                    // Map<ItemStack, ItemStack> furnaceRecipes = new
                    // HashMap<>(FurnaceRecipes.instance().getSmeltingList());

                    boolean changedPrice;
                    do
                    {
                        changedPrice = false;
                        RecipeManager manager= ServerLifecycleHooks.getCurrentServer().overworld().getRecipeManager();
                        Collection<Recipe<?>> recpies = manager.getRecipes();
                        for (Recipe<?> recipe : recpies)
                        {
                            if (recipe.getResultItem() == ItemStack.EMPTY)
                            {
                                continue;
                            }

                            double price = getRecipePrice(recipe, priceMap, priceMapFull);
                            if (price > 0)
                            {
                                price /= recipe.getResultItem().getCount();
                                Double resultPrice = priceMap.get(ItemUtil.getItemName(recipe.getResultItem()));
                                if (resultPrice == null || price < resultPrice)
                                {
                                    priceMap.put(ItemUtil.getItemName(recipe.getResultItem()), price);
                                    changedPrice = true;

                                    StringBuilder msg = new StringBuilder(String.format("%s = %.0f -> %s",
                                            ItemUtil.getItemName(recipe.getResultItem().getItem()),
                                            resultPrice == null ? 0 : resultPrice, (int) price));
                                    for (Ingredient ingredient : getRecipeItems(recipe))
                                        if (ingredient != null)
                                        {
                                            ItemStack stack = null;
                                            ItemStack[] stacks = ingredient.getItems();
                                            if (stacks != null && stacks.length > 0)
                                            {
                                                stack = stacks[0];
                                            }

                                            if (stack != ItemStack.EMPTY)
                                                msg.append(String.format("\n  %.0f - %s",
                                                        priceMap.get(ItemUtil.getItemName(stack)),
                                                        ItemUtil.getItemName(stack.getItem())));
                                        }
                                    writer.write(msg + "\n");
                                }
                            }
                        }
                        /*
                         * for (Iterator<Entry<ItemStack, ItemStack>> iterator = furnaceRecipes.entrySet().iterator(); iterator.hasNext();) { Entry<ItemStack, ItemStack> recipe =
                         * iterator.next(); Double inPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getKey())); if (inPrice != null) { double outPrice = inPrice *
                         * recipe.getKey().getCount() / recipe.getValue().getCount(); Double resultPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getValue())); if
                         * (resultPrice == null || outPrice < resultPrice) { priceMap.put(ItemUtil.getItemIdentifier(recipe.getValue()), outPrice);
                         * writer.write(String.format("%s:%d = %.0f -> %d\n  %s\n", ServerUtil.getItemName(recipe.getValue().getItem()), ItemUtil.getItemDamage( recipe.getValue()),
                         * resultPrice == null ? 0 : resultPrice, (int) outPrice, ServerUtil.getItemName(recipe.getKey().getItem()))); changedPrice = true; } } }
                         */
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
        }
        catch (Exception e2)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Failed to generate prices, check log");
            e2.printStackTrace();
            return;
        }
        writeMap(priceMap, priceFile);

        for (Item item : ForgeRegistries.ITEMS)
        {
            String id = ItemUtil.getItemName(item);
            if (!priceMapFull.containsKey(id))
                priceMapFull.put(id, 0.0);
        }
        priceMapFull.putAll(priceMap);
        writeMap(priceMapFull, allPricesFile);

        if (save)
        {
            writeToConfig(ctx.getSource(), priceMap);
        }
        else
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "Calculated new prices. Copy the prices you want to use from ./ForgeEssentials/prices.txt into Economy.cfg");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "You can also use [/sellprice save] to directly save the calculated prices");
        }
    }

    public static String dTs(Double value)
    {
        return Integer.toString((int) Math.floor(value));
    }

    private static Map<String, Double> loadPriceList(CommandContext<CommandSourceStack> ctx)
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
            ChatOutputHandler.chatWarning(ctx.getSource(),
                    String.format("Could not load %s. Using default values", priceFile.getName()));
            initializeDefaultPrices(priceMap);
        }
        return priceMap;
    }

    private static void writeToConfig(CommandSourceStack source, Map<String, Double> priceMap)
    {
        try
        {
            // get current values on disc
            Map<String, Integer> items = new HashMap<>();
            if (!ModuleEconomy.itemTables.isEmpty())
            {
                items.putAll(ModuleEconomy.itemTables);
            }
            // add new prices
            if (!priceMap.isEmpty())
            {
                for (Map.Entry<String, Double> entry : priceMap.entrySet())
                {
                    items.put(entry.getKey(), (int) Math.floor(entry.getValue()));
                    ModuleEconomy.setItemPrice(ModuleEconomy.PERM_PRICE + "." + entry.getKey(), dTs(entry.getValue()));
                }
            }
            // save new prices to disc
            Set<String> toWrite = new HashSet<>();
            for (Map.Entry<String, Integer> entry : items.entrySet())
            {
                toWrite.add(entry.getKey() + "=" + Integer.toString(entry.getValue()));
            }
            List<String> aList = new ArrayList<>(toWrite);
            ModuleEconomy.FEitemTables.set(aList);
            ChatOutputHandler.chatConfirmation(source, "Calculated and saved new price table");
        }
        catch (Exception e)
        {
            ChatOutputHandler.chatError(source, "Failed to save prices to config, check log");
            e.printStackTrace();
        }
    }

    private static void initializeDefaultPrices(Map<String, Double> priceMap)
    {
        // TODO re-evaluate these prices
        for (String col : colors)
            priceMap.put("minecraft:" + col + "_wool", 48.0);

        for (String type : woodTypes)
            priceMap.put("minecraft:" + type + "_log", 32.0);

        // priceMap.put("minecraft:red_flower", 16.0);
        // for (int i = 1; i <= 8; i++)
        // priceMap.put("minecraft:red_flower:" + i, 16.0);

        for (String type : woodTypes)
            priceMap.put("minecraft:" + type + "_sapling", 32.0);

        for (String type : woodTypes)
            priceMap.put("minecraft:" + type + "_leaves", 1.0);

        for (String col : colors)
            priceMap.put("minecraft:" + col + "_dye", 8.0);

        priceMap.put("minecraft:dead_bush", 1.0);
        priceMap.put("minecraft:dirt", 1.0);
        priceMap.put("minecraft:grass_block", 1.0);
        priceMap.put("minecraft:grass_path", 1.0);
        priceMap.put("minecraft:ice", 1.0);
        priceMap.put("minecraft:mycelium", 1.0);
        priceMap.put("minecraft:netherrack", 1.0);
        priceMap.put("minecraft:sand", 1.0);
        priceMap.put("minecraft:red_sand", 1.0);
        priceMap.put("minecraft:snow_block", 1.0);
        priceMap.put("minecraft:stone", 1.0);
        priceMap.put("minecraft:grass", 1.0);
        priceMap.put("minecraft:sea_grass", 1.0);
        priceMap.put("minecraft:tall_grass", 1.0);
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
        priceMap.put("minecraft:charcoal", 32.0);
        priceMap.put("minecraft:cobblestone", 1.0);
        priceMap.put("minecraft:cocoa_beans", 128.0);
        priceMap.put("minecraft:diamond", 8192.0);
        priceMap.put("minecraft:lapis_lazuli", 864.0);
        priceMap.put("minecraft:emerald", 8192.0);
        priceMap.put("minecraft:feather", 48.0);
        priceMap.put("minecraft:cod", 64.0);
        priceMap.put("minecraft:salmon", 64.0);
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
        priceMap.put("minecraft:melon_slice", 16.0);
        priceMap.put("minecraft:melon", 144.0);
        priceMap.put("minecraft:milk_bucket", 833.0);
        priceMap.put("minecraft:obsidian", 64.0);
        priceMap.put("minecraft:porkchop", 64.0);
        priceMap.put("minecraft:pumpkin", 144.0);
        priceMap.put("minecraft:red_mushroom", 32.0);
        priceMap.put("minecraft:redstone", 64.0);
        priceMap.put("minecraft:sugar_cane", 32.0);
        priceMap.put("minecraft:rotten_flesh", 24.0);
        priceMap.put("minecraft:slime_ball", 24.0);
        priceMap.put("minecraft:soul_sand", 49.0);
        priceMap.put("minecraft:soul_soil", 49.0);
        priceMap.put("minecraft:spider_eye", 128.0);
        priceMap.put("minecraft:string", 16.0);
        priceMap.put("minecraft:vine", 8.0);
        priceMap.put("minecraft:water_bucket", 769.0);
        priceMap.put("minecraft:lily_pad", 16.0);
        priceMap.put("minecraft:cobweb", 12.0);
        priceMap.put("minecraft:wheat", 24.0);
        priceMap.put("minecraft:dandelion", 16.0);
        priceMap.put("minecraft:potato", 16.0);
        priceMap.put("minecraft:carrot", 16.0);
        priceMap.put("minecraft:quartz", 128.0);
        priceMap.put("minecraft:sponge", 256.0);
    }

    private static void writeMap(Map<String, Double> priceMap, File file)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (Map.Entry<String, Double> entry : priceMap.entrySet())
            {
                StringBuilder id = new StringBuilder("I:\"" + entry.getKey() + "\"");
                while (id.length() < 50)
                    id.append(' ');
                writer.write(id + "=" + dTs(entry.getValue()) + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<Ingredient> getRecipeItems(Recipe<?> recipe)
    {
        return recipe.getIngredients();
    }

    public static double getRecipePrice(Recipe<?> recipe, Map<String, Double> priceMap,
            Map<String, Double> priceMapFull)
    {
        double price = 0;
        List<Ingredient> stackList = getRecipeItems(recipe);
        if (stackList.isEmpty())
        {
            return 0;
        }
        for (Ingredient ingredient : stackList)
            if (ingredient != null)
            {
                Double itemPrice = null;
                ItemStack[] stacks = ingredient.getItems();
                for (ItemStack stack : stacks)
                {
                    if (stack == ItemStack.EMPTY)
                    {
                        continue;
                    }
                    String id = ItemUtil.getItemName(stack);
                    priceMapFull.put(id, 0.0);
                    Double p = priceMap.get(id);
                    if (p != null && (itemPrice == null || p < itemPrice))
                        itemPrice = p;
                }
                if (itemPrice == null)
                    return -1;
                price += itemPrice;
            }
        return price;
    }
}
