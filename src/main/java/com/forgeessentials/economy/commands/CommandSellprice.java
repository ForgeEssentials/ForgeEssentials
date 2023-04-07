package com.forgeessentials.economy.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandSellprice extends ForgeEssentialsCommandBuilder
{

    public CommandSellprice(boolean enabled)
    {
        super(enabled);
    }

    private static File priceFile = new File(ForgeEssentials.getFEDirectory(), "prices.txt");

    @Override
    public String getPrimaryAlias()
    {
        return "sellprice";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".sellprice";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("save")
                        .executes(CommandContext -> execute(CommandContext, "save")
                                )
                        )
                .then(Commands.literal("set")
                        .executes(CommandContext -> execute(CommandContext, "set")
                                )
                        .then(Commands.argument("item", ItemArgument.item())
                                .then(Commands.argument("price", DoubleArgumentType.doubleArg())
                                        .executes(CommandContext -> execute(CommandContext, "set-item")
                                                )
                                        )
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        List<String> args = new ArrayList<String>(Arrays.asList(params.split("-"))); 
        if (args.isEmpty())
        {
            calcPriceList(ctx, null, false);
            return Command.SINGLE_SUCCESS;
        }
        String subArg = args.remove(0).toLowerCase();
        switch (subArg)
        {
        case "save":
            calcPriceList(ctx, args, true);
            break;
        case "set":
            parseSetprice(ctx, args);
            break;
        default:
            break;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseSetprice(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        if (args.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/sellprice set <item> <price>");
            return;
        }

        Item item = ItemArgument.getItem(ctx, "item").getItem();
        double price = DoubleArgumentType.getDouble(ctx, "price");

        String itemId = ServerUtil.getItemName(item);
        Map<String, Double> priceMap = loadPriceList(ctx);
        priceMap.put(itemId, price);
        writeMap(priceMap, priceFile);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set price for %s to %d", itemId, (int) price));
    }

    public static void calcPriceList(CommandContext<CommandSource> ctx, List<String> args, boolean save)
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(priceLogFile)))
        {
            try (BufferedWriter craftRecipes = new BufferedWriter(new FileWriter(craftRecipesFile)))
            {
                Collection<IRecipe<?>> recpies = ServerLifecycleHooks.getCurrentServer().overworld().getRecipeManager().getRecipes();
                for(IRecipe<?> recipe : recpies)
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
                            .write(String.format("%s\n", ServerUtil.getItemName(recipe.getResultItem().getItem())));
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
                                craftRecipes.write(String.format("  :%s\n", ServerUtil.getItemName(stack.getItem())));
                            }
                        }
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
                    ChatOutputHandler.chatError(ctx.getSource(), "WARNING: Infinite loop found in recipes. Cannot calculate prices reliably!");
                    return;
                }
                changedAnyPrice = false;

                //Map<ItemStack, ItemStack> furnaceRecipes = new HashMap<>(FurnaceRecipes.instance().getSmeltingList());

                boolean changedPrice;
                do
                {
                    changedPrice = false;
                    Collection<IRecipe<?>> recpies = ServerLifecycleHooks.getCurrentServer().overworld().getRecipeManager().getRecipes();
                    for(IRecipe<?> recipe : recpies)
                    {
                        if (recipe.getResultItem() == ItemStack.EMPTY)
                        {
                            continue;
                        }

                        double price = getRecipePrice(recipe, priceMap, priceMapFull);
                        if (price > 0)
                        {
                            price /= recipe.getResultItem().getCount();
                            Double resultPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getResultItem()));
                            if (resultPrice == null || price < resultPrice)
                            {
                                priceMap.put(ItemUtil.getItemIdentifier(recipe.getResultItem()), price);
                                changedPrice = true;

                                String msg = String.format("%s = %.0f -> %s", ServerUtil.getItemName(recipe.getResultItem().getItem()), 
                                        resultPrice == null ? 0 : resultPrice, (int) price);
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
                                            msg += String.format("\n  %.0f - %s", priceMap.get(ItemUtil.getItemIdentifier(stack)),
                                                    ServerUtil.getItemName(stack.getItem()));
                                    }
                                writer.write(msg + "\n");
                            }
                        }
                    }
                    /*
                    for (Iterator<Entry<ItemStack, ItemStack>> iterator = furnaceRecipes.entrySet().iterator(); iterator.hasNext();)
                    {
                        Entry<ItemStack, ItemStack> recipe = iterator.next();
                        Double inPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getKey()));
                        if (inPrice != null)
                        {
                            double outPrice = inPrice * recipe.getKey().getCount() / recipe.getValue().getCount();
                            Double resultPrice = priceMap.get(ItemUtil.getItemIdentifier(recipe.getValue()));
                            if (resultPrice == null || outPrice < resultPrice)
                            {
                                priceMap.put(ItemUtil.getItemIdentifier(recipe.getValue()), outPrice);
                                writer.write(String.format("%s:%d = %.0f -> %d\n  %s\n", ServerUtil.getItemName(recipe.getValue().getItem()),
                                        ItemUtil.getItemDamage(
                                                recipe.getValue()),
                                        resultPrice == null ? 0 : resultPrice, (int) outPrice, ServerUtil.getItemName(recipe.getKey().getItem())));
                                changedPrice = true;
                            }
                        }
                    }*/
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

        for (Item item : ForgeRegistries.ITEMS)
        {
            String id = ServerUtil.getItemName(item);
            if (!priceMapFull.containsKey(id))
                priceMapFull.put(id, 0.0);
        }
        priceMapFull.putAll(priceMap);
        writeMap(priceMapFull, allPricesFile);

        if (save)
        {
            //get current values on disc
            Map<String, Integer> items = new HashMap<String, Integer>();
            for(Map.Entry<String,Integer> entry : ModuleEconomy.itemTables.entrySet()){
                items.put(entry.getKey(), entry.getValue());
            } 
            //add new prices
            for (Entry<String, Double> entry : priceMap.entrySet())
            {
                items.put(entry.getKey(), (int) Math.floor(entry.getValue()));
                APIRegistry.perms.registerPermissionProperty(ModuleEconomy.PERM_PRICE + "." + entry.getKey(), dTs(entry.getValue()));
            }
            //save new prices to disc
            Set<String> toWrite = new HashSet<String>(); 
            for (Map.Entry<String,Integer> entry : items.entrySet()) {
                toWrite.add(entry.getKey()+"="+Integer.toString(entry.getValue()));
            }
            List<String> aList = new ArrayList<>();
            for (String x : toWrite)
                aList.add(x);
            ModuleEconomy.FEitemTables.set(aList);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Calculated and saved new price table");
        }
        else
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Calculated new prices. Copy the prices you want to use from ./ForgeEssentials/prices.txt into Economy.cfg");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "You can also use [/calcpricelist save] to directly save the calculated prices");
        }
    }

    public static String dTs(Double value) {
        return Integer.toString((int) Math.floor(value));
    }
    private static Map<String, Double> loadPriceList(CommandContext<CommandSource> ctx)
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
            ChatOutputHandler.chatWarning(ctx.getSource(), String.format("Could not load %s. Using default values", priceFile.getName()));
            initializeDefaultPrices(priceMap);
        }
        return priceMap;
    }

    private static void initializeDefaultPrices(Map<String, Double> priceMap)
    {
        //TODO re-evaluate these prices
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
                writer.write(id + "=" + dTs(entry.getValue()) + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<Ingredient> getRecipeItems(IRecipe<?> recipe)
    {
        return recipe.getIngredients();
    }

    public static double getRecipePrice(IRecipe<?> recipe, Map<String, Double> priceMap, Map<String, Double> priceMapFull)
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
                    String id = ItemUtil.getItemIdentifier(stack);
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
