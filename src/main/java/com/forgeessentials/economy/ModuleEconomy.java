package com.forgeessentials.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandWallet;
import com.forgeessentials.economy.network.S4PacketEconomy;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.FunctionHelper;
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

    public static final String PERM_VALUE = PERM + ".value";
    public static final String PERM_VALUE_ITEM = PERM_VALUE + ".item";
    public static final String PERM_VALUE_BLOCK = PERM_VALUE + ".block";

    public static final String CONFIG_CATEGORY = "Economy";
    public static final String CATEGORY_ITEM = CONFIG_CATEGORY + Configuration.CATEGORY_SPLITTER + "ItemSellPrice";
    public static final String CATEGORY_BLOCK = CONFIG_CATEGORY + Configuration.CATEGORY_SPLITTER + "BlockSellPrice";

    public static final int DEFAULT_ITEM_PRICE = 1;
    public static final int DEFAULT_BLOCK_PRICE = 1;

    /* ------------------------------------------------------------ */

    protected PlotManager plotManager;

    protected HashMap<UserIdent, PlayerWallet> wallets = new HashMap<>();

    protected List<Offer<ItemStack>> offers = new ArrayList<>();

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
        FunctionHelper.registerServerCommand(new CommandWallet());
        FunctionHelper.registerServerCommand(new CommandPay());
        FunctionHelper.registerServerCommand(new CommandPaidCommand());
        FunctionHelper.registerServerCommand(new CommandSellCommand());

        APIRegistry.perms.registerPermissionProperty(PERM_XP_MULTIPLIER, "1",
                "XP to currency conversion rate (integer, a zombie drops around 5 XP, 0 to disable)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY, "coins", "Name of currency (plural)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY_SINGULAR, "coin", "Name of currency (singular)");
        APIRegistry.perms.registerPermissionProperty(PERM_STARTBUDGET, "100", "Starting amount of money for players");

        plotManager.registerPermissionsAndCommands();
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

    /* ------------------------------------------------------------ */
    /* Economy interface */

    @Override
    public PlayerWallet getWallet(UserIdent ident)
    {
        PlayerWallet wallet = wallets.get(ident);
        if (wallet == null)
            wallet = DataManager.getInstance().load(PlayerWallet.class, ident.getOrGenerateUuid().toString());
        if (wallet == null)
        {
            wallet = new PlayerWallet(FunctionHelper.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_STARTBUDGET), 0));
            wallets.put(ident, wallet);
        }
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
        return Long.toString(amount) + currency(amount);
    }

    /* ------------------------------------------------------------ */

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // TODO: remove compatibility code
        ConfigCategory oldCategory = config.hasCategory("ItemTables") ? config.getCategory("ItemTables") : null;
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            int defaultValue = DEFAULT_ITEM_PRICE;
            // TODO: remove compatibility code
            if (oldCategory != null && oldCategory.containsKey(item.getUnlocalizedName()))
            {
                defaultValue = oldCategory.get(item.getUnlocalizedName()).getInt(DEFAULT_ITEM_PRICE);
                oldCategory.remove(item.getUnlocalizedName());
            }
            String id = GameData.getBlockRegistry().getNameForObject(item);
            APIRegistry.perms.registerPermissionProperty(PERM_VALUE_ITEM + "." + id, Integer.toString(config.get(CATEGORY_ITEM, id, defaultValue).getInt()));
        }
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
        {
            int defaultValue = DEFAULT_BLOCK_PRICE;
            // TODO: remove compatibility code
            if (oldCategory != null && oldCategory.containsKey(block.getUnlocalizedName()))
            {
                defaultValue = oldCategory.get(block.getUnlocalizedName()).getInt(DEFAULT_BLOCK_PRICE);
                oldCategory.remove(block.getUnlocalizedName());
            }
            String id = GameData.getBlockRegistry().getNameForObject(block);
            APIRegistry.perms.registerPermissionProperty(PERM_VALUE_BLOCK + "." + id, Integer.toString(config.get(CATEGORY_BLOCK, id, defaultValue).getInt()));
        }
        if (oldCategory != null)
            config.removeCategory(oldCategory);
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

}
