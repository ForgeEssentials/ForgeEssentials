package com.forgeessentials.protection;

import static cpw.mods.fml.common.eventhandler.Event.Result.ALLOW;
import static cpw.mods.fml.common.eventhandler.Event.Result.DENY;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ProtectionEventHandler extends ServerEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void attackEntityEvent(AttackEntityEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (e.target == null)
            return;

        EntityPlayer source = e.entityPlayer;
        WorldPoint sourcePos = new WorldPoint(source);
        if (e.target instanceof EntityPlayer)
        {
            // player -> player
            EntityPlayer target = (EntityPlayer) e.target;
            WorldPoint targetPos = new WorldPoint(target);

            if (!APIRegistry.perms.checkPermission(new UserIdent(target), targetPos, ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkPermission(new UserIdent(source), sourcePos, ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkPermission(new UserIdent(source), targetPos, ModuleProtection.PERM_PVP))
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
            if (!APIRegistry.perms.checkPermission(new UserIdent(source), targetPos, permission))
            {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void livingHurtEvent(LivingHurtEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (e.entityLiving == null)
            return;

        if (e.source.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer source = (EntityPlayer) e.source.getEntity();
            WorldPoint sourcePos = new WorldPoint(source);
            if (e.entityLiving instanceof EntityPlayer)
            {
                // player -> player
                EntityPlayer target = (EntityPlayer) e.entityLiving;
                WorldPoint targetPos = new WorldPoint(target);
                if (!APIRegistry.perms.checkPermission(new UserIdent(target), targetPos, ModuleProtection.PERM_PVP)
                        || !APIRegistry.perms.checkPermission(new UserIdent(source), sourcePos, ModuleProtection.PERM_PVP)
                        || !APIRegistry.perms.checkPermission(new UserIdent(source), targetPos, ModuleProtection.PERM_PVP))
                {
                    e.setCanceled(true);
                    return;
                }
            }
            else
            {
                // player -> living
                EntityLivingBase target = e.entityLiving;
                WorldPoint targetPos = new WorldPoint(target);
                String permission = ModuleProtection.PERM_DAMAGE_TO + "." + target.getClass().getSimpleName();
                if (ModuleProtection.isDebugMode(source))
                    OutputHandler.chatNotification(source, permission);
                if (!APIRegistry.perms.checkPermission(new UserIdent(source), targetPos, ModuleProtection.PERM_INTERACT_ENTITY))
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
                WorldPoint targetPos = new WorldPoint(target);
                String permission = ModuleProtection.PERM_DAMAGE_BY + "." + e.source.damageType;
                if (ModuleProtection.isDebugMode(target))
                    OutputHandler.chatNotification(target, permission);
                if (!APIRegistry.perms.checkPermission(new UserIdent(target), targetPos, permission))
                {
                    e.setCanceled(true);
                    return;
                }
            }
            
            if (e.entityLiving instanceof EntityPlayer && e.source.getEntity() != null)
            {
                // non-player-entity -> player (mob)
                EntityPlayer target = (EntityPlayer) e.entityLiving;
                WorldPoint targetPos = new WorldPoint(target);
                Entity source = e.source.getEntity();
                String permission = ModuleProtection.PERM_DAMAGE_BY + "." + source.getClass().getSimpleName();
                if (ModuleProtection.isDebugMode(target))
                    OutputHandler.chatNotification(target, permission);
                if (!APIRegistry.perms.checkPermission(new UserIdent(target), targetPos, permission))
                {
                    e.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void breakEvent(BreakEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        Block block = e.world.getBlock(e.x, e.y, e.z);
        String permission = ModuleProtection.PERM_BREAK + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
        if (ModuleProtection.isDebugMode(e.getPlayer()))
            OutputHandler.chatNotification(e.getPlayer(), permission);
        WorldPoint point = new WorldPoint(e.getPlayer().dimension, e.x, e.y, e.z);
        if (!APIRegistry.perms.checkPermission(new UserIdent(e.getPlayer()), point, ModuleProtection.PERM_OVERRIDE_BREAK)
                && !APIRegistry.perms.checkPermission(new UserIdent(e.getPlayer()), point, permission))
        {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void placeEvent(BlockEvent.PlaceEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        Block block = e.world.getBlock(e.x, e.y, e.z);
        String permission = ModuleProtection.PERM_PLACE + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
        if (ModuleProtection.isDebugMode(e.player))
            OutputHandler.chatNotification(e.player, permission);
        WorldPoint point = new WorldPoint(e.player.dimension, e.x, e.y, e.z);
        if (!APIRegistry.perms.checkPermission(new UserIdent(e.player), point, ModuleProtection.PERM_OVERRIDE_PLACE)
                && !APIRegistry.perms.checkPermission(new UserIdent(e.player), point, permission))
        {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
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
            if (!APIRegistry.perms.checkPermission(new UserIdent(e.player), point, ModuleProtection.PERM_OVERRIDE_PLACE)
                    && !APIRegistry.perms.checkPermission(new UserIdent(e.player), point, permission))
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

        // Check for block interaction
        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
        {
            Block block = e.world.getBlock(e.x, e.y, e.z);
            String permission = ModuleProtection.PERM_INTERACT + "." + block.getUnlocalizedName() + "." + block.getDamageValue(e.world, e.x, e.y, e.z);
            if (ModuleProtection.isDebugMode(e.entityPlayer))
                OutputHandler.chatNotification(e.entityPlayer, permission);
            WorldPoint point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);
            boolean allow = APIRegistry.perms.checkPermission(new UserIdent(e.entityPlayer), point, ModuleProtection.PERM_OVERRIDE_INTERACT)
                    || APIRegistry.perms.checkPermission(new UserIdent(e.entityPlayer), point, permission);
            e.useBlock = allow ? ALLOW : DENY;
        }

        // Check item (and block) usage
        ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
        if (stack != null)
        {
            WorldPoint point = new WorldPoint(e.entityPlayer);
            if (stack.getItem() instanceof ItemBlock)
            {
                // Blocks are covered in placeEvent
                return;
                // ForgeDirection dir = ForgeDirection.getOrientation(e.face);
                // int x = e.x + dir.offsetX;
                // int y = e.y + dir.offsetY;
                // int z = e.z + dir.offsetZ;
                // point = new WorldPoint(e.entityPlayer.dimension, x, y, z);
            }
            else
            {
                point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);
            }

            String permission = ModuleProtection.PERM_USE + "." + stack.getUnlocalizedName() + "." + stack.getItemDamage();
            if (ModuleProtection.isDebugMode(e.entityPlayer))
                OutputHandler.chatNotification(e.entityPlayer, permission);
            boolean allow = APIRegistry.perms.checkPermission(new UserIdent(e.entityPlayer), point, ModuleProtection.PERM_OVERRIDE_USE)
                    || APIRegistry.perms.checkPermission(new UserIdent(e.entityPlayer), point, permission);
            e.useItem = allow ? ALLOW : DENY;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void entityInteractEvent(EntityInteractEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        WorldPoint point = new WorldPoint(e.entityPlayer.dimension, (int) e.target.posX, (int) e.target.posY, (int) e.target.posZ);
        String permission = ModuleProtection.PERM_INTERACT_ENTITY + "." + e.target.getClass().getSimpleName();
        if (ModuleProtection.isDebugMode(e.entityPlayer))
            OutputHandler.chatNotification(e.entityPlayer, permission);
        boolean allow = APIRegistry.perms.checkPermission(new UserIdent(e.entityPlayer), point, ModuleProtection.PERM_OVERRIDE_INTERACT_ENTITY)
                || APIRegistry.perms.checkPermission(new UserIdent(e.entityPlayer), point, ModuleProtection.PERM_INTERACT_ENTITY);
        if (!allow)
            e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void checkSpawnEvent(CheckSpawn e)
    {
        if (e.entityLiving instanceof EntityPlayer)
            return;

        WorldPoint point = new WorldPoint(e.entityLiving);
        String mobID = EntityList.getEntityString(e.entity);
        if (!APIRegistry.perms.checkPermission(null, point, ModuleProtection.PERM_MOBSPAWN_NATURAL + "." + mobID))
        {
            e.setResult(Result.DENY);
            OutputHandler.debug(mobID + " : DENIED");
        }
        else
        {
            OutputHandler.debug(mobID + " : ALLOWED");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void specialSpawnEvent(SpecialSpawn e)
    {
        if (e.entityLiving instanceof EntityPlayer)
            return;

        WorldPoint point = new WorldPoint(e.entityLiving);
        String mobID = EntityList.getEntityString(e.entity);

        if (!APIRegistry.perms.checkPermission(null, point, ModuleProtection.PERM_MOBSPAWN_FORCED + "." + mobID))
        {
            e.setResult(Result.DENY);
        }
    }

    /*
     * @SubscribeEvent public void manageZoneBannedItems(PlayerChangedZone e) { for (ItemStack returned :
     * PlayerInfo.getPlayerInfo(e.entityPlayer).getHiddenItems()) { e.entityPlayer.inventory.addItemStackToInventory(returned); }
     * 
     * AdditionalZoneData bi = ModuleProtection.itemsList.get(e.afterZone); List<String> biList = bi.getBannedItems(); for (ListIterator<String> iter =
     * biList.listIterator(); iter.hasNext(); ) { String element = iter.next(); String[] split = element.split(":"); int id = Integer.parseInt(split[0]); int
     * meta = Integer.parseInt(split[1]); ItemStack is = new ItemStack(element, 0, meta);
     * 
     * if (e.entityPlayer.inventory.hasItemStack(is)) { PlayerInfo.getPlayerInfo(e.entityPlayer).getHiddenItems().add(is); } } }
     * 
     * @SubscribeEvent public void manageCrafting(PlayerEvent.ItemCraftedEvent e) { }
     * 
     * @SubscribeEvent public void manageZoneGameModes(PlayerChangedZone e) { String val = APIRegistry.perms.getPermissionPropForPlayer(e.entityPlayer.username,
     * e.afterZone.getName(), ModuleProtection.PERMPROP_ZONE_GAMEMODE); e.entityPlayer.setGameType(EnumGameType.getByID(Integer.parseInt(val)));
     * System.out.println("yoohoo");
     * 
     * }
     */

}
