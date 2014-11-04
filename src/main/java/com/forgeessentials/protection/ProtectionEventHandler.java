package com.forgeessentials.protection;

import static cpw.mods.fml.common.eventhandler.Event.Result.ALLOW;
import static cpw.mods.fml.common.eventhandler.Event.Result.DENY;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.LEFT_CLICK_BLOCK;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ProtectionEventHandler extends ServerEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(AttackEntityEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (e.target == null)
            return;

        EntityPlayer source = e.entityPlayer;
        UserIdent sourceIdent = new UserIdent(source);
        if (e.target instanceof EntityPlayer)
        {
            // player -> player
            EntityPlayer target = (EntityPlayer) e.target;
            if (!APIRegistry.perms.checkUserPermission(new UserIdent(target), ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, new WorldPoint(target), ModuleProtection.PERM_PVP))
            {
                e.setCanceled(true);
            }
        }
        else
        {
            // player -> entity
            Entity target = e.target;
            WorldPoint targetPos = new WorldPoint(e.target);

            String permission = ModuleProtection.PERM_DAMAGE_TO + "." + target.getClass().getSimpleName();
            if (ModuleProtection.isDebugMode(source))
                OutputHandler.chatNotification(source, permission);
            if (!APIRegistry.perms.checkUserPermission(sourceIdent, targetPos, permission))
            {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingHurtEvent(LivingHurtEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (e.entityLiving == null)
            return;

        if (e.source.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer source = (EntityPlayer) e.source.getEntity();
            if (e.entityLiving instanceof EntityPlayer)
            {
                // player -> player
                EntityPlayer target = (EntityPlayer) e.entityLiving;
                if (!APIRegistry.perms.checkUserPermission(new UserIdent(target), ModuleProtection.PERM_PVP)
                        || !APIRegistry.perms.checkUserPermission(new UserIdent(source), ModuleProtection.PERM_PVP)
                        || !APIRegistry.perms.checkUserPermission(new UserIdent(source), new WorldPoint(target), ModuleProtection.PERM_PVP))
                {
                    e.setCanceled(true);
                    return;
                }
            }
            else
            {
                // player -> living
                EntityLivingBase target = e.entityLiving;
                String permission = ModuleProtection.PERM_DAMAGE_TO + "." + target.getClass().getSimpleName();
                if (ModuleProtection.isDebugMode(source))
                    OutputHandler.chatNotification(source, permission);
                if (!APIRegistry.perms.checkUserPermission(new UserIdent(source), new WorldPoint(target), ModuleProtection.PERM_INTERACT_ENTITY))
                {
                    e.setCanceled(true);
                    return;
                }
            }
        }
        else
        {
            if (e.entityLiving instanceof EntityPlayer)
            {
                // non-player -> player (fall-damage, mob, dispenser, lava)
                EntityPlayer target = (EntityPlayer) e.entityLiving;
                String permission = ModuleProtection.PERM_DAMAGE_BY + "." + e.source.damageType;
                if (ModuleProtection.isDebugMode(target))
                    OutputHandler.chatNotification(target, permission);
                if (!APIRegistry.perms.checkUserPermission(new UserIdent(target), permission))
                {
                    e.setCanceled(true);
                    return;
                }
            }

            if (e.entityLiving instanceof EntityPlayer && e.source.getEntity() != null)
            {
                // non-player-entity -> player (mob)
                EntityPlayer target = (EntityPlayer) e.entityLiving;
                Entity source = e.source.getEntity();
                String permission = ModuleProtection.PERM_DAMAGE_BY + "." + source.getClass().getSimpleName();
                if (ModuleProtection.isDebugMode(target))
                    OutputHandler.chatNotification(target, permission);
                if (!APIRegistry.perms.checkUserPermission(new UserIdent(target), permission))
                {
                    e.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void breakEvent(BreakEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        Block block = e.world.getBlock(e.x, e.y, e.z);
        String permission = ModuleProtection.PERM_BREAK + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
        if (ModuleProtection.isDebugMode(e.getPlayer()))
            OutputHandler.chatNotification(e.getPlayer(), permission);
        WorldPoint point = new WorldPoint(e.getPlayer().dimension, e.x, e.y, e.z);
        if (!APIRegistry.perms.checkUserPermission(new UserIdent(e.getPlayer()), point, ModuleProtection.PERM_OVERRIDE_BREAK)
                && !APIRegistry.perms.checkUserPermission(new UserIdent(e.getPlayer()), point, permission))
        {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void placeEvent(BlockEvent.PlaceEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = new UserIdent(e.player);
        Block block = e.world.getBlock(e.x, e.y, e.z);
        String permission = ModuleProtection.PERM_PLACE + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
        if (ModuleProtection.isDebugMode(e.player))
            OutputHandler.chatNotification(e.player, permission);
        WorldPoint point = new WorldPoint(e.player.dimension, e.x, e.y, e.z);
        if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_OVERRIDE_PLACE)
                && !APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            e.setCanceled(true);
        }
        if (stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE
                && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, point, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            OutputHandler.chatError(e.player, "Cannot place block outside creative area");
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        for (BlockSnapshot b : e.getReplacedBlockSnapshots())
        {
            Block block = e.world.getBlock(b.x, b.y, b.z);
            String permission = ModuleProtection.PERM_PLACE + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
            if (ModuleProtection.isDebugMode(e.player))
                OutputHandler.chatNotification(e.player, permission);
            WorldPoint point = new WorldPoint(e.player.dimension, b.x, b.y, b.z);
            if (!APIRegistry.perms.checkUserPermission(new UserIdent(e.player), point, ModuleProtection.PERM_OVERRIDE_PLACE)
                    && !APIRegistry.perms.checkUserPermission(new UserIdent(e.player), point, permission))
            {
                e.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = new UserIdent(e.entityPlayer);
        
        WorldPoint point;
        if (e.action == RIGHT_CLICK_AIR)
        {
            MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(e.entityPlayer);
            if (mop == null)
                point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);
            else
                point = new WorldPoint(e.entityPlayer.dimension, mop.blockX, mop.blockY, mop.blockZ);
        }
        else
            point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);

        // Check for block interaction
        if (e.action == RIGHT_CLICK_BLOCK)
        {
            Block block = e.world.getBlock(e.x, e.y, e.z);
            String permission = ModuleProtection.PERM_INTERACT + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
            if (ModuleProtection.isDebugMode(e.entityPlayer))
                OutputHandler.chatNotification(e.entityPlayer, permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_OVERRIDE_INTERACT)
                    || APIRegistry.perms.checkUserPermission(ident, point, permission);
            e.useBlock = allow ? ALLOW : DENY;
        }

        // Check item (and block) usage
        ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
        if (stack != null && !(stack.getItem() instanceof ItemBlock))
        {
            String permission = ModuleProtection.PERM_USE + "." + stack.getUnlocalizedName() + "." + stack.getItemDamage();
            if (ModuleProtection.isDebugMode(e.entityPlayer))
                OutputHandler.chatNotification(e.entityPlayer, permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_OVERRIDE_USE)
                    || APIRegistry.perms.checkUserPermission(ident, point, permission);
            e.useItem = allow ? ALLOW : DENY;
        }

        if (anyCreativeModeAtPoint(e.entityPlayer, point)
                && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // If entity is in creative area, but player not, deny interaction
            e.useBlock = DENY;
            if (e.action != LEFT_CLICK_BLOCK)
                OutputHandler.chatError(e.entityPlayer, "Cannot interact with creative area if not in creative mode.");
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityInteractEvent(EntityInteractEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = new UserIdent(e.entityPlayer);
        WorldPoint point = new WorldPoint(e.entityPlayer.dimension, (int) e.target.posX, (int) e.target.posY, (int) e.target.posZ);
        String permission = ModuleProtection.PERM_INTERACT_ENTITY + "." + e.target.getClass().getSimpleName();
        if (ModuleProtection.isDebugMode(e.entityPlayer))
            OutputHandler.chatNotification(e.entityPlayer, permission);
        if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_OVERRIDE_INTERACT_ENTITY)
                && !APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_INTERACT_ENTITY))
        {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void checkSpawnEvent(CheckSpawn e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (e.entityLiving instanceof EntityPlayer)
            return;

        WorldPoint point = new WorldPoint(e.entityLiving);
        String mobID = EntityList.getEntityString(e.entity);
        if (!APIRegistry.perms.checkUserPermission(null, point, ModuleProtection.PERM_MOBSPAWN_NATURAL + "." + mobID))
        {
            e.setResult(Result.DENY);
            OutputHandler.debug(mobID + " : DENIED");
        }
        else
        {
            OutputHandler.debug(mobID + " : ALLOWED");
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void specialSpawnEvent(SpecialSpawn e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (e.entityLiving instanceof EntityPlayer)
            return;

        WorldPoint point = new WorldPoint(e.entityLiving);
        String mobID = EntityList.getEntityString(e.entity);

        if (!APIRegistry.perms.checkUserPermission(null, point, ModuleProtection.PERM_MOBSPAWN_FORCED + "." + mobID))
        {
            e.setResult(Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemPickupEvent(EntityItemPickupEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        handleBannedInventoryItemEvent(e, e.item.getEntityItem());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void dropItemEvent(StartTracking e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        
        if (e.target instanceof EntityItem)
        {
            // Destroy item when player in creative mode
            if (getGamemode(e.entityPlayer) == GameType.CREATIVE)
            {
                e.target.worldObj.removeEntity(e.target);
                return;
            }
            if (anyCreativeModeAtPoint(e.entityPlayer, new WorldPoint(e.target)))
            {
                // If creative mode is set for any group at the location where the block was destroyed, prevent drops
                e.target.worldObj.removeEntity(e.target);
                return;
            }
        }
    }

    @SubscribeEvent
    public void debugEvents(PlayerEvent e)
    {
        // try
        // {
        // if (e instanceof PlayerOpenContainerEvent)
        // return;
        // if (e instanceof ItemTooltipEvent)
        // return;
        // OutputHandler.chatNotification(e.entityPlayer, e.getClass().getSimpleName());
        // }
        // catch (Exception ex)
        // {
        // }
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerOpenContainerEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        // If it's the player's own inventory - ignore
        if (e.entityPlayer.openContainer == e.entityPlayer.inventoryContainer)
            return;
        checkPlayerInventory(e.entityPlayer);
    }

    @SubscribeEvent
    public void playerLoginEvent(PlayerLoggedInEvent e)
    {
        checkPlayerInventory(e.player);
    }

    @SubscribeEvent
    public void playerChangedZoneEvent(PlayerChangedZone e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        EntityPlayerMP player = (EntityPlayerMP) e.entityPlayer;
        UserIdent ident = new UserIdent(player);
        
        checkPlayerInventory(player);

        GameType gm = stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE));
        if (gm == GameType.NOT_SET)
            gm = GameType.SURVIVAL;
        if (gm != GameType.NOT_SET)
        {
            if (player.theItemInWorldManager.getGameType() != gm)
            {
                player.setGameType(gm);
                //OutputHandler.chatNotification(player, "You gamemode has been changed to " + gm.getName());
            }
        }

        // List<String> biList = bi.getBannedItems();
        // for (ListIterator<String> iter = biList.listIterator(); iter.hasNext();)
        // {
        // String element = iter.next();
        // String[] split = element.split(":");
        // int id = Integer.parseInt(split[0]);
        // int meta = Integer.parseInt(split[1]);
        // ItemStack is = new ItemStack(element, 0, meta);
        //
        // if (e.entityPlayer.inventory.hasItemStack(is))
        // {
        // PlayerInfo.getPlayerInfo(e.entityPlayer).getHiddenItems().add(is);
        // }
        // }

        // String val = APIRegistry.perms.getPermissionPropForPlayer(e.entityPlayer.username, e.afterZone.getName(), ModuleProtection.PERMPROP_ZONE_GAMEMODE);
        // e.entityPlayer.setGameType(EnumGameType.getByID(Integer.parseInt(val)));
        // System.out.println("yoohoo");
    }

    // ----------------------------------------

    public static GameType stringToGameType(String gm)
    {
        if (gm == null)
            return GameType.NOT_SET;
        switch (gm.toLowerCase())
        {
        case "0":
        case "s":
        case "survival":
            return GameType.SURVIVAL;
        case "1":
        case "c":
        case "creative":
            return GameType.CREATIVE;
        case "2":
        case "a":
        case "adventure":
            return GameType.ADVENTURE;
        default:
            return GameType.NOT_SET;
        }
    }

    public static GameType getGamemode(EntityPlayer player)
    {
        return stringToGameType(APIRegistry.perms.getUserPermissionProperty(new UserIdent(player), ModuleProtection.PERM_GAMEMODE));
    }

    public static boolean anyCreativeModeAtPoint(EntityPlayer player, WorldPoint point)
    {
        if (stringToGameType(APIRegistry.perms.getUserPermissionProperty(new UserIdent(player), point, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE)
            return true;
        for (String group : APIRegistry.perms.getServerZone().getGroups())
        {
            if (stringToGameType(APIRegistry.perms.getGroupPermissionProperty(group, point, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE)
                return true;
        }
        return false;
    }

    public static boolean isInventoryItemBanned(EntityPlayer player, ItemStack stack)
    {
        String permission = ModuleProtection.PERM_INVENTORY + "." + stack.getUnlocalizedName() + "." + stack.getItemDamage();
        //if (ModuleProtection.isDebugMode(player))
        //    OutputHandler.chatNotification(player, permission);
        return !APIRegistry.perms.checkUserPermission(new UserIdent(player), permission);
    }

    public static void handleBannedInventoryItemEvent(net.minecraftforge.event.entity.player.PlayerEvent e, ItemStack stack)
    {
        if (isInventoryItemBanned(e.entityPlayer, stack))
        {
            e.setCanceled(true);
        }
    }

    public static void checkPlayerInventory(EntityPlayer player)
    {
        //if (ModuleProtection.isDebugMode(player))
        //    OutputHandler.chatNotification(player, "PDBG: Checking inventory");
        for (int slotIdx = 0; slotIdx < player.inventory.getSizeInventory(); slotIdx++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slotIdx);
            if (stack != null)
            {
                if (isInventoryItemBanned(player, stack))
                {
                    EntityItem droppedItem = player.func_146097_a(stack, true, false);
                    droppedItem.motionX = 0;
                    droppedItem.motionY = 0;
                    droppedItem.motionZ = 0;
                    player.inventory.setInventorySlotContents(slotIdx, null);
                }
            }
        }
    }

}
