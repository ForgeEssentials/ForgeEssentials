package com.forgeessentials.economy;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.commands.CommandCalculatePriceList;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandSell;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandTrade;
import com.forgeessentials.economy.commands.CommandWallet;
import com.forgeessentials.economy.network.S4PacketEconomy;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

/**
 * Economy module.
 * 
 * Handles wallets for each player, transactions and plot management.
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class)
public class ModuleEconomy extends ServerEventHandler implements Economy, IConfigLoader
{

    public static final String PERM = "fe.economy";
    public static final String PERM_COMMAND = PERM + ".command";

    public static final String PERM_XP_MULTIPLIER = PERM + ".xpmultiplier";
    public static final String PERM_STARTBUDGET = PERM + ".startbudget";
    public static final String PERM_CURRENCY = PERM + ".currency";
    public static final String PERM_CURRENCY_SINGULAR = PERM_CURRENCY + ".singular";

    public static final String PERM_DEATHTOLL = PERM + ".deathtoll";

    public static final String PERM_PRICE = PERM + ".price";

    public static final String CONFIG_CATEGORY = "Economy";
    public static final String CATEGORY_ITEM = CONFIG_CATEGORY + Configuration.CATEGORY_SPLITTER + "ItemPrices";

    public static final int DEFAULT_ITEM_PRICE = 0;

    /* ------------------------------------------------------------ */

    protected PlotManager plotManager;

    protected HashMap<UserIdent, PlayerWallet> wallets = new HashMap<>();

    /* ------------------------------------------------------------ */
    /* Module events */

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        FunctionHelper.netHandler.registerMessage(S4PacketEconomy.class, S4PacketEconomy.class, 4, Side.CLIENT);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.economy = this;
        plotManager = new PlotManager();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        new CommandWallet().register();
        new CommandPay().register();
        new CommandSell().register();
        new CommandPaidCommand().register();
        new CommandSellCommand().register();
        new CommandTrade().register();
        new CommandCalculatePriceList().register();

        APIRegistry.perms.registerPermissionProperty(PERM_XP_MULTIPLIER, "1",
                "XP to currency conversion rate (integer, a zombie drops around 5 XP, 0 to disable)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY, "coins", "Name of currency (plural)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY_SINGULAR, "coin", "Name of currency (singular)");
        APIRegistry.perms.registerPermissionProperty(PERM_STARTBUDGET, "100", "Starting amount of money for players");
        APIRegistry.perms.registerPermissionDescription(PERM_PRICE, "Default prices for items in economy");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHTOLL, "",
                "Penalty for players to pay when they die. If set to lesser than 1, value is taken as a factor of the player's wallet balance.");

        PlotManager.serverStarting();
    }

    @SubscribeEvent
    public void serverStop(FEModuleServerStopEvent e)
    {
        for (Entry<UserIdent, PlayerWallet> wallet : wallets.entrySet())
            saveWallet(wallet.getKey().getOrGenerateUuid(), wallet.getValue());
    }

    /* ------------------------------------------------------------ */
    /* Utility */

    public static void saveWallet(UUID uuid, PlayerWallet wallet)
    {
        DataManager.getInstance().save(wallet, uuid.toString());
    }

    public static void confirmNewWalletAmount(UserIdent ident, Wallet wallet)
    {
        if (ident.hasPlayer())
            OutputHandler.chatConfirmation(ident.getPlayer(), Translator.format("You have now %s", wallet.toString()));
    }

    public static int tryRemoveItems(EntityPlayerMP player, ItemStack itemStack, int amount)
    {
        int foundStacks = 0;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemStack.getItem()
                    && (itemStack.getItemDamage() == -1 || stack.getItemDamage() == itemStack.getItemDamage()))
                foundStacks += stack.stackSize;
        }
        foundStacks = amount = Math.min(foundStacks, amount);
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemStack.getItem()
                    && (itemStack.getItemDamage() == -1 || stack.getItemDamage() == itemStack.getItemDamage()))
            {
                int removeCount = Math.min(stack.stackSize, foundStacks);
                player.inventory.decrStackSize(slot, removeCount);
                foundStacks -= removeCount;
            }
        }
        return amount;
    }

    /* ------------------------------------------------------------ */
    /* Events */

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        UserIdent ident = new UserIdent(event.player);
        PlayerWallet wallet = getWallet(ident);
        if (wallet != null)
            saveWallet(ident.getOrGenerateUuid(), wallet);
    }

    @SubscribeEvent
    public void onXPPickup(PlayerPickupXpEvent e)
    {
        UserIdent ident = new UserIdent(e.entityPlayer);
        double xpMultiplier = FunctionHelper.parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_XP_MULTIPLIER), 0);
        if (xpMultiplier <= 0)
            return;
        PlayerWallet wallet = getWallet(ident);
        wallet.add(xpMultiplier * e.orb.xpValue);
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e)
    {
        if (e.entity instanceof EntityPlayerMP)
        {
            UserIdent ident = new UserIdent((EntityPlayerMP) e.entity);
            Long deathtoll = FunctionHelper.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, PERM_DEATHTOLL));
            if (deathtoll == null || deathtoll <= 0)
                return;
            Wallet walletData = APIRegistry.economy.getWallet(ident);
            if (deathtoll < 1)
                walletData.set(walletData.get() * deathtoll);
            else if (deathtoll >= 1)
                walletData.set(Math.min(0, walletData.get() - deathtoll));
        }
    }

    /* ------------------------------------------------------------ */
    /* Economy interface */

    @Override
    public PlayerWallet getWallet(UserIdent ident)
    {
        PlayerWallet wallet = wallets.get(ident);
        if (wallet == null)
            wallet = DataManager.getInstance().load(PlayerWallet.class, ident.getOrGenerateUuid().toString());
        if (wallet == null)
            wallet = new PlayerWallet(FunctionHelper.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_STARTBUDGET), 0));
        wallets.put(ident, wallet);
        return wallet;
    }

    @Override
    public PlayerWallet getWallet(EntityPlayerMP player)
    {
        return getWallet(new UserIdent(player));
    }

    @Override
    public String currency(long value)
    {
        if (value == 1)
            return APIRegistry.perms.getGlobalPermissionProperty(PERM_CURRENCY_SINGULAR);
        else
            return APIRegistry.perms.getGlobalPermissionProperty(PERM_CURRENCY);
    }

    @Override
    public String toString(long amount)
    {
        return Long.toString(amount) + " " + currency(amount);
    }

    /* ------------------------------------------------------------ */

    public static String getItemIdentifier(ItemStack itemStack)
    {
        String id = GameData.getItemRegistry().getNameForObject(itemStack.getItem());
        if (itemStack.getItemDamage() == 0 || itemStack.getItemDamage() == 32767)
            return id;
        else
            return id + ":" + itemStack.getItemDamage();
    }

    public static String getItemPricePermission(ItemStack itemStack)
    {
        return PERM_PRICE + "." + getItemIdentifier(itemStack);
    }

    public static Long getItemPrice(ItemStack itemStack, UserIdent ident)
    {
        return FunctionHelper.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, getItemPricePermission(itemStack)));
    }

    public static void setItemPrice(ItemStack itemStack, long price)
    {
        APIRegistry.perms.registerPermissionProperty(getItemPricePermission(itemStack), Long.toString(price));
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        if (config.hasCategory("ItemTables"))
        {
            ConfigCategory category = config.getCategory("ItemTables");
            for (Entry<String, Property> entry : category.entrySet())
            {
                for (Item item : GameData.getItemRegistry().typeSafeIterable())
                    if (entry.getKey().equals(item.getUnlocalizedName()))
                    {
                        String id = GameData.getItemRegistry().getNameForObject(item);
                        config.get(CATEGORY_ITEM, id, DEFAULT_ITEM_PRICE).set(entry.getValue().getInt(DEFAULT_ITEM_PRICE));;
                        break;
                    }
            }
            config.removeCategory(category);
        }

        ConfigCategory category = config.getCategory(CATEGORY_ITEM);
        for (Entry<String, Property> entry : category.entrySet())
        {
            APIRegistry.perms.registerPermissionProperty(PERM_PRICE + "." + entry.getKey(), Integer.toString(entry.getValue().getInt(DEFAULT_ITEM_PRICE)));
        }
    }

    @Override
    public void save(Configuration config)
    {
        /* do nothing */
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return false;
    }

    /* ------------------------------------------------------------ */

    public static class CantAffordException extends TranslatedCommandException
    {
        public CantAffordException()
        {
            super("You can't afford that");
        }
    }
}
