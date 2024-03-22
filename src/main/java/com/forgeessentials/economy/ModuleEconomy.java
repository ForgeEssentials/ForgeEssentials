package com.forgeessentials.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandRequestPayment;
import com.forgeessentials.economy.commands.CommandSell;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandSellprice;
import com.forgeessentials.economy.commands.CommandTrade;
import com.forgeessentials.economy.commands.CommandWallet;
import com.forgeessentials.economy.shop.ShopManager;
import com.forgeessentials.protection.ProtectionEventHandler;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Economy module.
 * 
 * Handles wallets for each player, transactions and plot management.
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleEconomy extends ServerEventHandler implements Economy, ConfigLoader
{
    private static ForgeConfigSpec ECONOMY_CONFIG;
    public static final ConfigData data = new ConfigData("Economy", ECONOMY_CONFIG, new ForgeConfigSpec.Builder());

    public static final UserIdent ECONOMY_IDENT = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefeec",
            "$FE_ECONOMY");

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

    public static final int DEFAULT_ITEM_PRICE = 0;

    /* ------------------------------------------------------------ */

    protected ShopManager shopManager = new ShopManager();

    protected HashMap<UserIdent, PlayerWallet> wallets = new HashMap<>();

    /* ------------------------------------------------------------ */
    /* Module events */

    public ModuleEconomy()
    {
        APIRegistry.economy = this;
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandWallet(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPay(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSell(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPaidCommand(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSellCommand(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTrade(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSellprice(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandRequestPayment(true), event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        APIRegistry.perms.registerPermissionProperty(PERM_XP_MULTIPLIER, "0",
                "XP to currency conversion rate (integer, a zombie drops around 5 XP, 0 to disable)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY, "coins", "Name of currency (plural)");
        APIRegistry.perms.registerPermissionProperty(PERM_CURRENCY_SINGULAR, "coin", "Name of currency (singular)");
        APIRegistry.perms.registerPermissionProperty(PERM_STARTBUDGET, "100", "Starting amount of money for players");
        APIRegistry.perms.registerPermissionDescription(PERM_PRICE, "Default prices for items in economy");

        APIRegistry.perms.registerPermissionDescription(PERM_BOUNTY,
                "Bounty for killing entities (ex.: fe.economy.bounty.Skeleton = 5)");
        APIRegistry.perms.registerPermission(PERM_BOUNTY_MESSAGE, DefaultPermissionLevel.ALL,
                "Whether to show a message if a bounty is given");
        for (Entry<ResourceKey<EntityType<?>>, EntityType<?>> e : ForgeRegistries.ENTITIES.getEntries())
            if (LivingEntity.class.isAssignableFrom(e.getValue().getClass()))
                APIRegistry.perms.registerPermissionProperty(PERM_BOUNTY + "." + e.getKey(), "0");

        APIRegistry.perms.registerPermissionProperty(PERM_DEATHTOLL, "",
                "Penalty for players to pay when they die. If set to lesser than 1, value is taken as a factor of the player's wallet balance.");

        CommandFeSettings.addSetting("Economy", "money_per_xp", PERM_XP_MULTIPLIER);
        CommandFeSettings.addSetting("Economy", "start_budget", PERM_STARTBUDGET);
        CommandFeSettings.addSetting("Economy", "currency_name", PERM_CURRENCY);
        CommandFeSettings.addSetting("Economy", "currency_name_singular", PERM_CURRENCY_SINGULAR);
        CommandFeSettings.addSetting("Economy", "death_toll", PERM_DEATHTOLL);
    }

    @SubscribeEvent
    public void serverStop(FEModuleServerStoppingEvent e)
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
            ChatOutputHandler.chatConfirmation(ident.getPlayerMP().createCommandSourceStack(),
                    Translator.format("You have now %s", wallet.toString()));
    }

    public static int tryRemoveItems(Player player, ItemStack itemStack, int amount)
    {
        int foundStacks = 0;
        int itemDamage = ItemUtil.getItemDamage(itemStack);
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
        {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem()
                    && (itemDamage == -1 || stack.getDamageValue() == itemDamage))
                foundStacks += stack.getCount();
        }
        foundStacks = amount = Math.min(foundStacks, amount);
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
        {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem()
                    && (itemDamage == -1 || stack.getDamageValue() == itemDamage))
            {
                int removeCount = Math.min(stack.getCount(), foundStacks);
                player.getInventory().removeItem(slot, removeCount);
                foundStacks -= removeCount;
            }
        }
        player.containerMenu.broadcastChanges();

        return amount;
    }

    public static int countInventoryItems(Player player, ItemStack itemType)
    {
        int foundStacks = 0;
        int itemDamage = ItemUtil.getItemDamage(itemType);
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
        {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemType.getItem()
                    && (itemDamage == -1 || stack.getDamageValue() == itemDamage))
                foundStacks += stack.getCount();
        }
        return foundStacks;
    }

    /* ------------------------------------------------------------ */
    /* Events */

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        UserIdent ident = UserIdent.get(event.getPlayer());
        PlayerWallet wallet = getWallet(ident);
        if (wallet != null)
            saveWallet(ident.getOrGenerateUuid(), wallet);
    }

    @SubscribeEvent
    public void onXPPickup(PlayerXpEvent.PickupXp e)
    {
        if (e.getEntity() instanceof ServerPlayer)
        {
            UserIdent ident = UserIdent.get(e.getPlayer().getGameProfile().getId());
            double xpMultiplier = ServerUtil
                    .parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_XP_MULTIPLIER), 0);
            if (xpMultiplier <= 0)
                return;
            PlayerWallet wallet = getWallet(ident);
            wallet.add(xpMultiplier * e.getOrb().value);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e)
    {
        if (e.getEntity() instanceof ServerPlayer)
        {
            UserIdent ident = UserIdent.get((ServerPlayer) e.getEntity());
            Long deathtoll = ServerUtil
                    .tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, PERM_DEATHTOLL));
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
            ChatOutputHandler.chatNotification(((Player) e.getEntity()).createCommandSourceStack(),
                    Translator.format("You lost %s from dying", APIRegistry.economy.toString(loss)));
        }

        if (e.getSource().getDirectEntity() instanceof ServerPlayer)
        {
            UserIdent killer = UserIdent.get((ServerPlayer) e.getSource().getDirectEntity());
            String permission = PERM_BOUNTY + "." + ProtectionEventHandler.getEntityName(e.getEntityLiving());
            double bounty = ServerUtil
                    .parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(killer, permission), 0);
            if (bounty > 0)
            {
                Wallet wallet = APIRegistry.economy.getWallet(killer);
                wallet.add(bounty);
                if (APIRegistry.perms.checkUserPermission(killer, PERM_BOUNTY_MESSAGE))
                    ChatOutputHandler.chatNotification(killer.getPlayer().createCommandSourceStack(), Translator
                            .format("You received %s as bounty", APIRegistry.economy.toString((long) bounty)));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void commandEvent(CommandEvent event)
    {
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer))
            return;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        UserIdent ident = UserIdent.get((ServerPlayer) info.getSource().getEntity());

        if (event.getParseResults().getContext().getArguments().size() >= 0)
        {
            String permission = PERM_COMMANDPRICE + '.' + info.getPermissionNode();
            // System.out.println(permission);
            Long price = ServerUtil.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, permission));
            if (price == null)
                return;

            Wallet wallet = APIRegistry.economy.getWallet(ident);
            if (!wallet.withdraw(price))
            {
                event.setCanceled(true);
                info.getSource()
                        .sendFailure(new TextComponent("You do not have enough money to use this command."));
            }
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
            wallet = new PlayerWallet(ServerUtil
                    .parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_STARTBUDGET), 0));
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
        return PERM_PRICE + "." + ItemUtil.getItemName(itemStack);
    }

    public static Long getItemPrice(ItemStack itemStack, UserIdent ident)
    {
        return ServerUtil
                .tryParseLong(APIRegistry.perms.getGlobalPermissionProperty(getItemPricePermission(itemStack)));
    }

    public static void setItemPrice(ItemStack itemStack, long price)
    {
        setItemPrice(getItemPricePermission(itemStack), Long.toString(price));
    }

    public static void setItemPrice(String node, String price)
    {
        APIRegistry.perms.registerPermissionProperty(node, price);
    }

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> FEitemTables;
    public static Map<String, Integer> itemTables = new HashMap<>();

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push(CONFIG_CATEGORY);
        FEitemTables = BUILDER.comment("Itemprices for the economy module").defineList("Itemprices",
                new ArrayList<>(), ConfigBase.stringValidator);
        BUILDER.pop();
        ShopManager.load(BUILDER, isReload);
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        for (String itemValue : FEitemTables.get())
        {
            String[] values = itemValue.split("=");
            int price;
            try
            {
                price = CommandUtils.parseInt(values[1]);
            }
            catch (NumberFormatException e)
            {
                LoggingHandler.felog.error("Incorrect Price value", e);
                price = DEFAULT_ITEM_PRICE;
            }
            for (Item item : ForgeRegistries.ITEMS)
                if (values[0].equals(ItemUtil.getItemName(item)))
                {
                    itemTables.put(ItemUtil.getItemName(item), price);
                    break;
                }
        }
        if (!FEitemTables.get().isEmpty())
        {
            for (Entry<String, Integer> entry : itemTables.entrySet())
                setItemPrice(PERM_PRICE + "." + entry.getKey(), Integer.toString((entry.getValue())));
        }
        ShopManager.bakeConfig(reload);
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }
}
