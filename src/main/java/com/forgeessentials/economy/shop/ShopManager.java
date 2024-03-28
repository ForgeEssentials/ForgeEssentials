package com.forgeessentials.economy.shop;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.protection.ProtectionEventHandler;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.entity.EntityAttackedEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.common.reflect.TypeToken;

import net.minecraft.world.level.block.Block;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class ShopManager extends ServerEventHandler
{

    public static final String PERM_BASE = ModuleEconomy.PERM + ".shop";
    public static final String PERM_CREATE = PERM_BASE + ".create";
    public static final String PERM_DESTROY = PERM_BASE + ".destroy";
    public static final String PERM_USE = PERM_BASE + ".use";

    public static final String MSG_MODIFY_DENIED = "You are not allowed to modify shops!";
    public static final String STOCK_HELP = "If disabled, shops have an infinite stock. Otherwise players can only buy items that have been sold to the shop before";

    public static final String CONFIG_FILE = "EconomyConfig";

    public static final Set<String> shopTags = new HashSet<>();

    public static boolean useStock;

    protected static Set<ShopData> shops = new HashSet<>();

    protected static Map<WorldPoint, ShopData> shopSignMap = new WeakHashMap<>();

    protected static Map<UUID, ShopData> shopFrameMap = new WeakHashMap<>();

    /* ------------------------------------------------------------ */
    /* Data */

    public ShopManager()
    {
        shopTags.add("[FEShop]");
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent event)
    {
        super.serverStopped(event);
        save();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        load();
        APIRegistry.perms.registerPermissionDescription(PERM_BASE, "Shop permissions");
        APIRegistry.perms.registerPermission(PERM_USE, DefaultPermissionLevel.ALL, "Allow usage of shops");
        APIRegistry.perms.registerPermission(PERM_CREATE, DefaultPermissionLevel.OP, "Allow creating shops");
        APIRegistry.perms.registerPermission(PERM_DESTROY, DefaultPermissionLevel.OP, "Allow destroying shops");
    }

    public static File getSaveFile()
    {
        return new File(DataManager.getInstance().getBasePath(), "shops.json");
    }

    public static void save()
    {
        DataManager.save(shops, getSaveFile());
    }

    public static void load()
    {
        shops.clear();
        shopSignMap.clear();

        Type type = new TypeToken<List<ShopData>>() {
        }.getType();
        List<ShopData> shopList = DataManager.load(type, getSaveFile());
        if (shopList == null)
            return;
        for (ShopData shop : shopList)
            addShop(shop);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void breakEvent(BreakEvent event)
    {
        if (FMLEnvironment.dist.isClient())
            return;
        WorldPoint point = new WorldPoint(event.getPlayer().level, event.getPos());
        ShopData shop = getShop(point, event.getPlayer().createCommandSourceStack());
        if (shop == null)
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        if (!APIRegistry.perms.checkUserPermission(ident, point, PERM_DESTROY))
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    Translator.translate(MSG_MODIFY_DENIED));
            event.setCanceled(true);
            BlockEntity te = event.getWorld().getBlockEntity(event.getPos());
            if (te != null)
                ProtectionEventHandler.updateBrokenTileEntity((ServerPlayer) event.getPlayer(), te);
            return;
        }

        removeShop(shop);
        ChatOutputHandler.chatNotification(event.getPlayer().createCommandSourceStack(),
                Translator.translate("Shop destroyed"));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityAttackedEvent(EntityAttackedEvent event)
    {
        if (FMLEnvironment.dist.isClient() || !(event.getEntity() instanceof ItemFrame))
            return;
        final ShopData shop = shopFrameMap.get(event.getEntity().getUUID());
        if (shop == null)
            return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(final AttackEntityEvent event)
    {
        if (FMLEnvironment.dist.isClient() || !(event.getTarget() instanceof ItemFrame))
            return;
        final ShopData shop = shopFrameMap.get(event.getTarget().getUUID());
        if (shop == null)
            return;
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(event.getPlayer()), new WorldPoint(event.getTarget()),
                PERM_DESTROY))
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    Translator.translate(MSG_MODIFY_DENIED));
            event.setCanceled(true);
            return;
        }
        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                shop.update();
                if (!shop.isValid)
                {
                    removeShop(shop);
                    ChatOutputHandler.chatNotification(event.getPlayer().createCommandSourceStack(),
                            Translator.translate("Shop destroyed"));
                }
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void entityInteractEvent(PlayerInteractEvent.EntityInteract event)
    {
        if (FMLEnvironment.dist.isClient())
            return;
        ShopData shop = shopFrameMap.get(event.getTarget().getUUID());
        if (shop == null)
            return;
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(event.getPlayer()), new WorldPoint(event.getTarget()),
                PERM_CREATE))
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    Translator.translate(MSG_MODIFY_DENIED));
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractEvent(final PlayerInteractEvent event)
    {
        if (event instanceof LeftClickBlock || ServerLifecycleHooks.getCurrentServer().isSingleplayer())
            return;
        if(ModuleLauncher.getModuleList().contains("Commands")) {
        	ModuleCommands.eventHandler.playerActive((ServerPlayer) event.getPlayer());
        }
        ItemStack equippedStack = event.getPlayer().getMainHandItem();
        Item equippedItem = equippedStack != ItemStack.EMPTY ? equippedStack.getItem() : null;

        WorldPoint point;
        if (event instanceof RightClickItem)
        {
            if (!(equippedItem instanceof BlockItem))
                return;
            HitResult mop = PlayerUtil.getPlayerLookingSpot(event.getPlayer());
            if (mop.getType() == HitResult.Type.MISS)
                return;
            point = new WorldPoint(event.getWorld(), new BlockPos(mop.getLocation()));
        }
        else
            point = new WorldPoint(event.getWorld(), event.getPos());

        UserIdent ident = UserIdent.get(event.getPlayer());
        ShopData shop = shopSignMap.get(point);
        boolean newShop = (shop == null);
        if (newShop)
        {
            Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
            if (!ItemUtil.isSign(block))
                return;
            Component[] text = ItemUtil.getSignText(point);
            if (text == null || text.length < 1 || !shopTags.contains(text[0].getString()))
                return;
            if (!APIRegistry.perms.checkUserPermission(ident, point, PERM_CREATE))
            {
                ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                        Translator.translate("You are not allowed to create shops!"));
                return;
            }
            ItemFrame frame = ShopData.findFrame(point);
            if (frame == null)
            {
                ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                        Translator.translate("No item frame found"));
                return;
            }
            if (shopFrameMap.containsKey(frame.getUUID()))
            {
                ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                        Translator.translate("Item frame already used for another shop!"));
                return;
            }
            shop = new ShopData(point, frame);
        }

        shop.update();
        if (!shop.isValid)
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    Translator.format("Shop invalid: %s", shop.getError()));
            if (!newShop)
                removeShop(shop);
            return;
        }
        if (newShop)
        {
            ChatOutputHandler.chatConfirmation(event.getPlayer().createCommandSourceStack(),
                    Translator.translate("Created shop!"));
            addShop(shop);
            return;
        }

        event.setCanceled(true);

        if (!APIRegistry.perms.checkUserPermission(ident, point, PERM_USE))
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    Translator.translate("You are not allowed to use shops!"));
            return;
        }

        ItemStack transactionStack = shop.getItemStack();
        boolean sameItem = transactionStack.getItem() == equippedItem;
        transactionStack.setCount(shop.amount);
        Component itemName = transactionStack.getDisplayName();

        Wallet wallet = APIRegistry.economy.getWallet(UserIdent.get((ServerPlayer) event.getPlayer()));

        if (shop.sellPrice >= 0 && (shop.buyPrice < 0 || sameItem))
        {
            if (ModuleEconomy.countInventoryItems(event.getPlayer(), transactionStack) < transactionStack.getCount())
            {
                TranslatableComponent msg = new TranslatableComponent("You do not have enough %s", itemName);
                msg.withStyle(ChatOutputHandler.chatConfirmationColor);
                ChatOutputHandler.sendMessage(event.getPlayer().createCommandSourceStack(), msg);
                return;
            }
            int removedAmount = 0;
            if (sameItem)
            {
                removedAmount = Math.min(equippedStack.getCount(), transactionStack.getCount());
                equippedStack.setCount(equippedStack.getCount() - removedAmount);
                if (equippedStack.getCount() <= 0)
                    event.getPlayer().getInventory().items.set(event.getPlayer().getInventory().selected, ItemStack.EMPTY);
            }
            if (removedAmount < transactionStack.getCount())
                removedAmount += ModuleEconomy.tryRemoveItems(event.getPlayer(), transactionStack,
                        transactionStack.getCount() - removedAmount);
            wallet.add(shop.sellPrice);
            shop.setStock(shop.getStock() + 1);

            String price = APIRegistry.economy.toString(shop.sellPrice);
            TranslatableComponent msg = new TranslatableComponent("Sold %s x %s for %s (wallet: %s)", shop.amount,
                    itemName, price, wallet.toString());
            msg.withStyle(ChatOutputHandler.chatConfirmationColor);
            ChatOutputHandler.sendMessage(event.getPlayer().createCommandSourceStack(), msg);
        }
        else
        {
            if (useStock && shop.getStock() <= 0)
            {
                ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(), "Shop stock is empty");
                return;
            }
            if (!wallet.withdraw(shop.buyPrice))
            {
                String errorMsg = Translator.format("You do not have enough %s in your wallet",
                        APIRegistry.economy.currency(2));
                ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(), errorMsg);
                return;
            }
            if (useStock)
                shop.setStock(shop.getStock() - 1);
            PlayerUtil.give(event.getPlayer(), transactionStack);
            String price = APIRegistry.economy.toString(shop.buyPrice);
            TranslatableComponent msg = new TranslatableComponent("Bought %s x %s for %s (wallet: %s)",
                    shop.amount, itemName, price, wallet.toString());
            msg.withStyle(ChatOutputHandler.chatConfirmationColor);
            ChatOutputHandler.sendMessage(event.getPlayer().createCommandSourceStack(), msg);
        }
    }

    public static ShopData getShop(WorldPoint point, CommandSourceStack sender)
    {
        ShopData shop = shopSignMap.get(point);
        if (shop == null)
            return null;
        if (!shop.isValid)
            shop.update();
        if (!shop.isValid)
        {
            ChatOutputHandler.chatError(sender, Translator.format("Shop invalid: %s", shop.getError()));
            return null;
        }
        return shop;
    }

    private static void removeShop(ShopData shop)
    {
        shops.remove(shop);
        shopSignMap.remove(shop.pos);
        shopFrameMap.remove(shop.itemFrameId);
    }

    private static void addShop(ShopData shop)
    {
        shops.add(shop);
        shopSignMap.put(shop.pos, shop);
        shopFrameMap.put(shop.itemFrameId, shop);
    }

    /* ------------------------------------------------------------ */

    static ForgeConfigSpec.BooleanValue FEuseStock;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEshopTags;

    public static void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push(CONFIG_FILE);
        FEuseStock = BUILDER.comment(STOCK_HELP).define("use_stock", false);
        List<String> aList = new ArrayList<>(shopTags.size());
        aList.addAll(shopTags);
        FEshopTags = BUILDER.defineList("shopTags", aList, ConfigBase.stringValidator);
        BUILDER.pop();
    }

    public static void bakeConfig(boolean reload)
    {

        useStock = FEuseStock.get();
        shopTags.clear();
        shopTags.addAll(FEshopTags.get());
    }
}
