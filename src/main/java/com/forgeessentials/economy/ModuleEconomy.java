package com.forgeessentials.economy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameData;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandSell;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandSellprice;
import com.forgeessentials.economy.commands.CommandTrade;
import com.forgeessentials.economy.commands.CommandWallet;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.economy.shop.ShopManager;
import com.forgeessentials.protection.ProtectionEventHandler;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

/**
 * Economy module.
 * 
 * Handles wallets for each player, transactions and plot management.
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class)
public class ModuleEconomy extends ServerEventHandler implements Economy, ConfigLoader
{

    public static final UserIdent ECONOMY_IDENT = UserIdent.get("fefefefe-fefe-fefe-fefe-fefefefefeec", "$FE_ECONOMY");

    public static final String PERM = "fe.economy";
    public static final String PERM_COMMAND = PERM + ".command";

    public static final String PERM_XP_MULTIPLIER = PERM + ".xpmultiplier";
    public static final String PERM_STARTBUDGET = PERM + ".startbudget";
    public static final String PERM_CURRENCY = PERM + ".currency";
    public static final String PERM_CURRENCY_SINGULAR = PERM_CURRENCY + ".singular";

    public static final String PERM_DEATHTOLL = PERM + ".deathtoll";
    public static final String PERM_COMMANDPRICE = PERM + ".cmdprice";

    public static final String PERM_PRICE = PERM + ".price";
    public static final String PERM_BOUNTY = PERM + ".bounty";
    public static final String PERM_BOUNTY_MESSAGE = PERM_BOUNTY + ".message";

    public static final String CONFIG_CATEGORY = "Economy";
    public static final String CATEGORY_ITEM = CONFIG_CATEGORY + Configuration.CATEGORY_SPLITTER + "ItemPrices";

    public static final int DEFAULT_ITEM_PRICE = 0;

    /* ------------------------------------------------------------ */

    protected PlotManager plotManager;

    protected ShopManager shopManager;

    protected HashMap<UserIdent, PlayerWallet> wallets = new HashMap<>();

    /* ------------------------------------------------------------ */
    /* Module events */

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        APIRegistry.economy = this;
        plotManager = new PlotManager();
        shopManager = new ShopManager();

        FECommandManager.registerCommand(new CommandWallet());
        FECommandManager.registerCommand(new CommandPay());
        FECommandManager.registerCommand(new CommandSell());
        FECommandManager.registerCommand(new CommandPaidCommand());
        FECommandManager.registerCommand(new CommandSellCommand());
        FECommandManager.registerCommand(new CommandTrade());
        FECommandManager.registerCommand(new CommandSellprice());
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
        APIRegistry.perms.registerPermissionProperty(PERM_XP_MULTIPLIER, "0",
                "XP to currency conversion rate (integer, a zombie drops around 5 XP, 0 to disable)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY, "coins", "Name of currency (plural)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY_SINGULAR, "coin", "Name of currency (singular)");
        APIRegistry.perms.registerPermissionProperty(PERM_STARTBUDGET, "100", "Starting amount of money for players");
        APIRegistry.perms.registerPermissionDescription(PERM_PRICE, "Default prices for items in economy");

        APIRegistry.perms.registerPermissionDescription(PERM_BOUNTY, "Bounty for killing entities (ex.: fe.economy.bounty.Skeleton = 5)");
        APIRegistry.perms.registerPermission(PERM_BOUNTY_MESSAGE, PermissionLevel.TRUE, "Whether to show a message if a bounty is given");
        for (Entry<String, Class<? extends Entity>> e : ((Map<String, Class<? extends Entity>>) EntityList.stringToClassMapping).entrySet())
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
                APIRegistry.perms.registerPermissionProperty(PERM_BOUNTY + "." + e.getKey(), "0");

        APIRegistry.perms.registerPermissionProperty(PERM_DEATHTOLL, "",
                "Penalty for players to pay when they die. If set to lesser than 1, value is taken as a factor of the player's wallet balance.");

        CommandFeSettings.addAlias("Economy", "money_per_xp", PERM_XP_MULTIPLIER);
        CommandFeSettings.addAlias("Economy", "start_budget", PERM_STARTBUDGET);
        CommandFeSettings.addAlias("Economy", "currency_name", PERM_CURRENCY);
        CommandFeSettings.addAlias("Economy", "currency_name_singular", PERM_CURRENCY_SINGULAR);
        CommandFeSettings.addAlias("Economy", "death_toll", PERM_DEATHTOLL);

        PlotManager.serverStarting();
    }

    @SubscribeEvent
    public void serverStop(FEModuleServerStopEvent e)
    {
        for (Entry<UserIdent, PlayerWallet> wallet : wallets.entrySet())
            saveWallet(wallet.getKey().getOrGenerateUuid(), wallet.getValue());
    }

    @SubscribeEvent
    public void permissionAfterLoadEvent(PermissionEvent.AfterLoad event)
    {
        event.serverZone.setPlayerPermission(ECONOMY_IDENT, "command.give", true);
        event.serverZone.setPlayerPermission(ECONOMY_IDENT, CommandWallet.PERM + ".*", true);
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
            ChatOutputHandler.chatConfirmation(ident.getPlayerMP(), Translator.format("You have now %s", wallet.toString()));
    }

    public static int tryRemoveItems(EntityPlayer player, ItemStack itemStack, int amount)
    {
        int foundStacks = 0;
        int itemDamage = ItemUtil.getItemDamage(itemStack);
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemStack.getItem() && (itemDamage == -1 || stack.getItemDamage() == itemDamage))
                foundStacks += stack.stackSize;
        }
        foundStacks = amount = Math.min(foundStacks, amount);
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemStack.getItem() && (itemDamage == -1 || stack.getItemDamage() == itemDamage))
            {
                int removeCount = Math.min(stack.stackSize, foundStacks);
                player.inventory.decrStackSize(slot, removeCount);
                foundStacks -= removeCount;
            }
        }
        return amount;
    }

    public static int countInventoryItems(EntityPlayer player, ItemStack itemType)
    {
        int foundStacks = 0;
        int itemDamage = ItemUtil.getItemDamage(itemType);
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
        {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.getItem() == itemType.getItem() && (itemDamage == -1 || stack.getItemDamage() == itemDamage))
                foundStacks += stack.stackSize;
        }
        return foundStacks;
    }

    /* ------------------------------------------------------------ */
    /* Events */

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        UserIdent ident = UserIdent.get(event.player);
        PlayerWallet wallet = getWallet(ident);
        if (wallet != null)
            saveWallet(ident.getOrGenerateUuid(), wallet);
    }

    @SubscribeEvent
    public void onXPPickup(PlayerPickupXpEvent e)
    {
        if (e.entityPlayer instanceof EntityPlayerMP)
        {
            UserIdent ident = UserIdent.get(e.entityPlayer);
            double xpMultiplier = ServerUtil.parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_XP_MULTIPLIER), 0);
            if (xpMultiplier <= 0)
                return;
            PlayerWallet wallet = getWallet(ident);
            wallet.add(xpMultiplier * e.orb.xpValue);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e)
    {
        if (e.entity instanceof EntityPlayerMP)
        {
            UserIdent ident = UserIdent.get((EntityPlayerMP) e.entity);
            Long deathtoll = ServerUtil.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, PERM_DEATHTOLL));
            if (deathtoll == null || deathtoll <= 0)
                return;
            Wallet wallet = APIRegistry.economy.getWallet(ident);
            long newAmount;
            if (deathtoll < 1)
                newAmount = wallet.get() * deathtoll;
            else if (deathtoll >= 1)
                newAmount = Math.max(0, wallet.get() - deathtoll);
            else
                return;
            long loss = wallet.get() - newAmount;
            if (loss <= 0)
                return;
            wallet.set(newAmount);
            ChatOutputHandler.chatNotification((ICommandSender) e.entity, Translator.format("You lost %s from dying", APIRegistry.economy.toString(loss)));
        }

        if (e.source.getEntity() instanceof EntityPlayerMP)
        {
            UserIdent killer = UserIdent.get((EntityPlayerMP) e.source.getEntity());
            String permission = PERM_BOUNTY + "." + ProtectionEventHandler.getEntityName(e.entityLiving);
            double bounty = ServerUtil.parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(killer, permission), 0);
            if (bounty > 0)
            {
                Wallet wallet = APIRegistry.economy.getWallet(killer);
                wallet.add(bounty);
                if (APIRegistry.perms.checkUserPermission(killer, PERM_BOUNTY_MESSAGE))
                    ChatOutputHandler.chatNotification(killer.getPlayer(),
                            Translator.format("You received %s as bounty", APIRegistry.economy.toString((long) bounty)));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void commandEvent(CommandEvent event)
    {
        if (!(event.sender instanceof EntityPlayerMP))
            return;
        UserIdent ident = UserIdent.get((EntityPlayerMP) event.sender);

        for (int i = event.parameters.length; i >= 0; i--)
        {
            String permission = PERM_COMMANDPRICE + '.' + event.command.getCommandName() + //
                    (i == 0 ? "" : ('.' + StringUtils.join(Arrays.copyOf(event.parameters, i), '.')));
            Long price = ServerUtil.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, permission));
            if (price == null)
                continue;

            Wallet wallet = APIRegistry.economy.getWallet(ident);
            if (!wallet.withdraw(price))
                event.setCanceled(true);
            break;
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
            wallet = new PlayerWallet(ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_STARTBUDGET), 0));
        wallets.put(ident, wallet);
        return wallet;
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

    public static String getItemPricePermission(ItemStack itemStack)
    {
        return PERM_PRICE + "." + ItemUtil.getItemIdentifier(itemStack);
    }

    public static Long getItemPrice(ItemStack itemStack, UserIdent ident)
    {
        return ServerUtil.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, getItemPricePermission(itemStack)));
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
                        String id = ServerUtil.getItemName(item);
                        config.get(CATEGORY_ITEM, id, DEFAULT_ITEM_PRICE).set(entry.getValue().getInt(DEFAULT_ITEM_PRICE));
                        break;
                    }
            }
            config.removeCategory(category);
        }

        ConfigCategory category = config.getCategory(CATEGORY_ITEM);
        for (Entry<String, Property> entry : category.entrySet())
            APIRegistry.perms.registerPermissionProperty(PERM_PRICE + "." + entry.getKey(), Integer.toString(entry.getValue().getInt(DEFAULT_ITEM_PRICE)));
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
