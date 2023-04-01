package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.commands.*;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.economy.shop.ShopManager;
import com.forgeessentials.protection.ProtectionEventHandler;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * Economy module.
 * 
 * Handles wallets for each player, transactions and plot management.
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class)
public class ModuleEconomy extends ServerEventHandler implements Economy, ConfigLoader
{
    private static ForgeConfigSpec ECONOMY_CONFIG;
	public static final ConfigData data = new ConfigData("Economy", ECONOMY_CONFIG, new ForgeConfigSpec.Builder());
	
    public static final UserIdent ECONOMY_IDENT = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefeec", "$FE_ECONOMY");

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
    public static final String CATEGORY_ITEM = CONFIG_CATEGORY + "-" + "ItemPrices";

    public static final int DEFAULT_ITEM_PRICE = 0;

    /* ------------------------------------------------------------ */

    protected PlotManager plotManager;

    protected ShopManager shopManager;

    protected HashMap<UserIdent, PlayerWallet> wallets = new HashMap<>();

    /* ------------------------------------------------------------ */
    /* Module events */

    @SubscribeEvent
    public void load(FEModuleCommonSetupEvent e)
    {
        APIRegistry.economy = this;
        plotManager = new PlotManager();
        shopManager = new ShopManager();
    }

    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandWallet(true), dispatcher);
        FECommandManager.registerCommand(new CommandPay(true), dispatcher);
        FECommandManager.registerCommand(new CommandSell(true), dispatcher);
        FECommandManager.registerCommand(new CommandPaidCommand(true), dispatcher);
        FECommandManager.registerCommand(new CommandSellCommand(true), dispatcher);
        FECommandManager.registerCommand(new CommandTrade(true), dispatcher);
        FECommandManager.registerCommand(new CommandSellprice(true), dispatcher);
        FECommandManager.registerCommand(new CommandRequestPayment(true), dispatcher);
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

        APIRegistry.perms.registerPermissionDescription(PERM_BOUNTY, "Bounty for killing entities (ex.: fe.economy.bounty.Skeleton = 5)");
        APIRegistry.perms.registerPermission(PERM_BOUNTY_MESSAGE, DefaultPermissionLevel.ALL, "Whether to show a message if a bounty is given");
        for (Entry<RegistryKey<EntityType<?>>, EntityType<?>> e : ForgeRegistries.ENTITIES.getEntries())
            if (LivingEntity.class.isAssignableFrom(e.getValue().getClass()))
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
            ChatOutputHandler.chatConfirmation(ident.getPlayerMP().createCommandSourceStack(), Translator.format("You have now %s", wallet.toString()));
    }

    public static int tryRemoveItems(PlayerEntity player, ItemStack itemStack, int amount)
    {
        int foundStacks = 0;
        int itemDamage = ItemUtil.getItemDamage(itemStack);
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++)
        {
            ItemStack stack = player.inventory.getItem(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem() && (itemDamage == -1 || stack.getDamageValue() == itemDamage))
                foundStacks += stack.getCount();
        }
        foundStacks = amount = Math.min(foundStacks, amount);
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++)
        {
            ItemStack stack = player.inventory.getItem(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemStack.getItem() && (itemDamage == -1 || stack.getDamageValue() == itemDamage))
            {
                int removeCount = Math.min(stack.getCount(), foundStacks);
                player.inventory.removeItem(slot, removeCount);
                foundStacks -= removeCount;
            }
        }
        player.containerMenu.broadcastChanges();

        return amount;
    }

    public static int countInventoryItems(PlayerEntity player, ItemStack itemType)
    {
        int foundStacks = 0;
        int itemDamage = ItemUtil.getItemDamage(itemType);
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++)
        {
            ItemStack stack = player.inventory.getItem(slot);
            if (stack != ItemStack.EMPTY && stack.getItem() == itemType.getItem() && (itemDamage == -1 || stack.getDamageValue() == itemDamage))
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
        if (e.getEntity() instanceof ServerPlayerEntity)
        {
            UserIdent ident = UserIdent.get(e.getEntity().getUUID());
            double xpMultiplier = ServerUtil.parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(ident, PERM_XP_MULTIPLIER), 0);
            if (xpMultiplier <= 0)
                return;
            PlayerWallet wallet = getWallet(ident);
            wallet.add(xpMultiplier * e.getOrb().value);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e)
    {
        if (e.getEntity() instanceof ServerPlayerEntity)
        {
            UserIdent ident = UserIdent.get((ServerPlayerEntity) e.getEntity());
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
            ChatOutputHandler.chatNotification(((PlayerEntity) e.getEntity()).createCommandSourceStack(), Translator.format("You lost %s from dying", APIRegistry.economy.toString(loss)));
        }

        if (e.getSource().getDirectEntity() instanceof ServerPlayerEntity)
        {
            UserIdent killer = UserIdent.get((ServerPlayerEntity) e.getSource().getDirectEntity());
            String permission = PERM_BOUNTY + "." + ProtectionEventHandler.getEntityName(e.getEntityLiving());
            double bounty = ServerUtil.parseDoubleDefault(APIRegistry.perms.getUserPermissionProperty(killer, permission), 0);
            if (bounty > 0)
            {
                Wallet wallet = APIRegistry.economy.getWallet(killer);
                wallet.add(bounty);
                if (APIRegistry.perms.checkUserPermission(killer, PERM_BOUNTY_MESSAGE))
                    ChatOutputHandler.chatNotification(killer.getPlayer().createCommandSourceStack(),
                            Translator.format("You received %s as bounty", APIRegistry.economy.toString((long) bounty)));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void commandEvent(CommandEvent event)
    {
        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayerEntity))
            return;
        UserIdent ident = UserIdent.get((ServerPlayerEntity) event.getParseResults().getContext().getSource().getEntity());

        for (int i = event.getParseResults().getContext().getArguments().size(); i >= 0; i--)
        {
            String permission = PERM_COMMANDPRICE + '.' + event.getParseResults().getReader().getString() + //
                    (i == 0 ? "" : ('.' + StringUtils.join(event.getParseResults().getContext().getNodes(), '.')));
            Long price = ServerUtil.tryParseLong(APIRegistry.perms.getUserPermissionProperty(ident, permission));
            if (price == null)
                continue;

            Wallet wallet = APIRegistry.economy.getWallet(ident);
            if (!wallet.withdraw(price))
            {
                event.setCanceled(true);
                TranslationTextComponent textcomponenttranslation2 = new TranslationTextComponent("You do not have enough money to use this command.", new Object[0]);
                textcomponenttranslation2.getStyle().withColor(TextFormatting.RED);
                event.getParseResults().getContext().getSource().sendFailure(textcomponenttranslation2);
            }
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

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> FEitemTables;
    public static Map<String, Integer> itemTables;
	@Override
	public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("ItemTables").push(CATEGORY_ITEM);
        FEitemTables = BUILDER.comment(CATEGORY_ITEM).defineList("exclude_patterns", new ArrayList<String>(), ConfigBase.stringValidator);
        BUILDER.pop();
        ShopManager.load(BUILDER, isReload);
    }
	
	@Override
	public void bakeConfig(boolean reload) {
		for(String itemValue :FEitemTables.get()) {
		    String[] values = itemValue.split("=");
		    int price;
		    try {price = CommandUtils.parseInt(values[0]);
		    }catch(NumberFormatException e) 
		    {LoggingHandler.felog.error("Incorrect Prive value", e); price = DEFAULT_ITEM_PRICE;}
		    for (Item item : ForgeRegistries.ITEMS)
                if (values[0].equals(item.getDescriptionId()))
                {
                    String id = ServerUtil.getItemName(item);
                    itemTables.put(id, price);
                    break;
                }
		}
		if(!FEitemTables.get().isEmpty()) {
			for (Entry<String, Integer> entry : itemTables.entrySet())
	            APIRegistry.perms.registerPermissionProperty(PERM_PRICE + "." + entry.getKey(), Integer.toString((entry.getValue())));
		}
		ShopManager.bakeConfig(reload);
	}
	
	@Override
	public ConfigData returnData() {
		return data;
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
