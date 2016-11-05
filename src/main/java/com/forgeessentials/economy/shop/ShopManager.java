package com.forgeessentials.economy.shop;

import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.data.DataManager;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.protection.ProtectionEventHandler;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.data.DataUtils;
import com.forgeessentials.util.ItemUtil;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.TaskRegistry;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ShopManager extends ServerEventHandler implements ConfigLoader
{

    public static final String PERM_BASE = ModuleEconomy.PERM + ".shop";
    public static final String PERM_CREATE = PERM_BASE + ".create";
    public static final String PERM_DESTROY = PERM_BASE + ".destroy";
    public static final String PERM_USE = PERM_BASE + ".use";

    public static final String MSG_MODIFY_DENIED = "You are not allowed to modify shops!";
    public static final String STOCK_HELP = "If disabled, shops have an infinite stock. Otherwise players can only buy items, that have been sold to the shop before";

    public static final String CONFIG_FILE = "EconomyConfig";

    public static final Set<String> shopTags = new HashSet<String>();

    public static boolean useStock;

    protected static Set<ShopData> shops = new HashSet<ShopData>();

    protected static Map<WorldPoint, ShopData> shopSignMap = new WeakHashMap<WorldPoint, ShopData>();

    protected static Map<UUID, ShopData> shopFrameMap = new WeakHashMap<UUID, ShopData>();

    /* ------------------------------------------------------------ */
    /* Data */

    public ShopManager()
    {
        shopTags.add("[FEShop]");
        ForgeEssentials.getConfigManager().registerLoader(CONFIG_FILE, this);
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent event)
    {
        super.serverStopped(event);
        save();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
        load();
        APIRegistry.perms.registerPermissionDescription(PERM_BASE, "Shop permissions");
        APIRegistry.perms.registerPermission(PERM_USE, PermissionLevel.TRUE, "Allow usage of shops");
        APIRegistry.perms.registerPermission(PERM_CREATE, PermissionLevel.OP, "Allow creating shops");
        APIRegistry.perms.registerPermission(PERM_DESTROY, PermissionLevel.OP, "Allow destroying shops");
    }

    public static File getSaveFile()
    {
        return new File(DataManager.getInstance().getBasePath(), "shops.json");
    }

    public static void save()
    {
        DataUtils.save(shops, getSaveFile());
    }

    public static void load()
    {
        shops.clear();
        shopSignMap.clear();

        Type type = new TypeToken<List<ShopData>>() {
        }.getType();
        List<ShopData> shopList = DataUtils.load(type, getSaveFile());
        if (shopList == null)
            return;
        for (ShopData shop : shopList)
            addShop(shop);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void breakEvent(BreakEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        WorldPoint point = new WorldPoint(event);
        ShopData shop = getShop(point, event.getPlayer());
        if (shop == null)
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        if (!APIRegistry.perms.checkUserPermission(ident, point, PERM_DESTROY))
        {
            ChatUtil.chatError(event.getPlayer(), Translator.translate(MSG_MODIFY_DENIED));
            event.setCanceled(true);
            TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
            if (te != null)
                ProtectionEventHandler.updateBrokenTileEntity((EntityPlayerMP) event.getPlayer(), te);
            return;
        }

        removeShop(shop);
        ChatUtil.chatNotification(event.getPlayer(), Translator.translate("Shop destroyed"));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityAttackedEvent(EntityAttackedEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() || !(event.entity instanceof EntityItemFrame))
            return;
        final ShopData shop = shopFrameMap.get(event.entity.getPersistentID());
        if (shop == null)
            return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(final AttackEntityEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() || !(event.target instanceof EntityItemFrame))
            return;
        final ShopData shop = shopFrameMap.get(event.target.getPersistentID());
        if (shop == null)
            return;
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(event.entityPlayer), new WorldPoint(event.target), PERM_DESTROY))
        {
            ChatUtil.chatError(event.entityPlayer, Translator.translate(MSG_MODIFY_DENIED));
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
                    ChatUtil.chatNotification(event.entityPlayer, Translator.translate("Shop destroyed"));
                }
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void entityInteractEvent(EntityInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        ShopData shop = shopFrameMap.get(event.target.getPersistentID());
        if (shop == null)
            return;
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(event.entityPlayer), new WorldPoint(event.target), PERM_CREATE))
        {
            ChatUtil.chatError(event.entityPlayer, Translator.translate(MSG_MODIFY_DENIED));
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractEvent(final PlayerInteractEvent event)
    {
        if (event.action == Action.LEFT_CLICK_BLOCK || FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        ItemStack equippedStack = event.entityPlayer.getCurrentEquippedItem();
        Item equippedItem = equippedStack != null ? equippedStack.getItem() : null;

        WorldPoint point;
        if (event.action == RIGHT_CLICK_AIR)
        {
            if (!(equippedItem instanceof ItemBlock))
                return;
            MovingObjectPosition mop = PlayerUtil.getPlayerLookingSpot(event.entityPlayer);
            if (mop == null)
                return;
            point = new WorldPoint(event.world, mop.blockX, mop.blockY, mop.blockZ);
        }
        else
            point = new WorldPoint(event.world, event.x, event.y, event.z);

        UserIdent ident = UserIdent.get(event.entityPlayer);
        ShopData shop = shopSignMap.get(point);
        boolean newShop = shop == null;
        if (newShop)
        {
            Block block = event.world.getBlock(event.x, event.y, event.z);
            if (!ItemUtil.isSign(block))
                return;
            String[] text = ItemUtil.getSignText(point);
            LoggingHandler.felog.info("DEBUG text = " + text + " / type of text = " + text[0].getClass().getName());
            if (!shopTags.isEmpty())
                LoggingHandler.felog.info("DEBUG shopTags(0) type = " + shopTags.iterator().next());
            if (text == null || text.length < 1 || !shopTags.contains(text[0]))
                return;
            if (!APIRegistry.perms.checkUserPermission(ident, point, PERM_CREATE))
            {
                ChatUtil.chatError(event.entityPlayer, Translator.translate("You are not allowed to create shops!"));
                return;
            }
            EntityItemFrame frame = ShopData.findFrame(point);
            if (frame == null)
            {
                ChatUtil.chatError(event.entityPlayer, Translator.translate("No item frame found"));
                return;
            }
            if (shopFrameMap.containsKey(frame.getPersistentID()))
            {
                ChatUtil.chatError(event.entityPlayer, Translator.translate("Item frame already used for another shop!"));
                return;
            }
            shop = new ShopData(point, frame);
        }

        shop.update();
        if (!shop.isValid)
        {
            ChatUtil.chatError(event.entityPlayer, Translator.format("Shop invalid: %s", shop.getError()));
            if (!newShop)
                removeShop(shop);
            return;
        }
        if (newShop)
        {
            ChatUtil.chatConfirmation(event.entityPlayer, Translator.translate("Created shop!"));
            addShop(shop);
            return;
        }

        event.setCanceled(true);

        if (!APIRegistry.perms.checkUserPermission(ident, point, PERM_USE))
        {
            ChatUtil.chatError(event.entityPlayer, Translator.translate("You are not allowed to use shops!"));
            return;
        }

        ItemStack transactionStack = shop.getItemStack();
        boolean sameItem = transactionStack.getItem() == equippedItem;
        transactionStack.stackSize = shop.amount;
        IChatComponent itemName = transactionStack.func_151000_E();

        Wallet wallet = APIRegistry.economy.getWallet(UserIdent.get((EntityPlayerMP) event.entityPlayer));

        if (shop.sellPrice >= 0 && (shop.buyPrice < 0 || sameItem))
        {
            if (ModuleEconomy.countInventoryItems(event.entityPlayer, transactionStack) < transactionStack.stackSize)
            {
                ChatComponentTranslation msg = new ChatComponentTranslation("You do not have enough %s", itemName);
                msg.getChatStyle().setColor(ChatUtil.chatConfirmationColor);
                ChatUtil.sendMessage(event.entityPlayer, msg);
                return;
            }
            int removedAmount = 0;
            if (sameItem)
            {
                removedAmount = Math.min(equippedStack.stackSize, transactionStack.stackSize);
                equippedStack.stackSize -= removedAmount;
                if (equippedStack.stackSize <= 0)
                    event.entityPlayer.inventory.mainInventory[event.entityPlayer.inventory.currentItem] = null;
            }
            if (removedAmount < transactionStack.stackSize)
                removedAmount += ModuleEconomy.tryRemoveItems(event.entityPlayer, transactionStack, transactionStack.stackSize - removedAmount);
            wallet.add(shop.sellPrice);
            shop.setStock(shop.getStock() + 1);

            String price = APIRegistry.economy.toString(shop.sellPrice);
            ChatComponentTranslation msg = new ChatComponentTranslation("Sold %s x %s for %s (wallet: %s)", shop.amount, itemName, price, wallet.toString());
            msg.getChatStyle().setColor(ChatUtil.chatConfirmationColor);
            ChatUtil.sendMessage(event.entityPlayer, msg);
        }
        else
        {
            if (useStock && shop.getStock() <= 0)
            {
                ChatUtil.chatError(event.entityPlayer, "Shop stock is empty");
                return;
            }
            if (!wallet.withdraw(shop.buyPrice))
            {
                String errorMsg = Translator.format("You do not have enough %s in your wallet", APIRegistry.economy.currency(2));
                ChatUtil.chatError(event.entityPlayer, errorMsg);
                return;
            }
            if (useStock)
                shop.setStock(shop.getStock() - 1);
            PlayerUtil.give(event.entityPlayer, transactionStack);
            String price = APIRegistry.economy.toString(shop.buyPrice);
            ChatComponentTranslation msg = new ChatComponentTranslation("Bought %s x %s for %s (wallet: %s)", shop.amount, itemName, price, wallet.toString());
            msg.getChatStyle().setColor(ChatUtil.chatConfirmationColor);
            ChatUtil.sendMessage(event.entityPlayer, msg);
        }
    }

    public static ShopData getShop(WorldPoint point, ICommandSender sender)
    {
        ShopData shop = shopSignMap.get(point);
        if (shop == null)
            return null;
        if (!shop.isValid)
            shop.update();
        if (!shop.isValid)
        {
            ChatUtil.chatError(sender, Translator.format("Shop invalid: %s", shop.getError()));
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

    @Override
    public void load(Configuration config, boolean isReload)
    {
        useStock = config.getBoolean(CONFIG_FILE, "use_stock", false, STOCK_HELP);
        String[] tags = config.get(CONFIG_FILE, "shopTags", shopTags.toArray(new String[shopTags.size()])).getStringList();
        shopTags.clear();
        for (String tag : tags)
            shopTags.add(tag);
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

}
