package com.forgeessentials.protection;

import static cpw.mods.fml.common.eventhandler.Event.Result.ALLOW;
import static cpw.mods.fml.common.eventhandler.Event.Result.DENY;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.LEFT_CLICK_BLOCK;
import static net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fe.event.entity.SpecialEntityAttackedEvent;
import net.minecraftforge.fe.event.entity.FallOnBlockEvent;
import net.minecraftforge.fe.event.world.FireEvent;
import net.minecraftforge.fe.event.world.PressurePlateEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.PermissionEvent.Group;
import com.forgeessentials.api.permissions.PermissionEvent.User;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet3PlayerPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.protection.effect.CommandEffect;
import com.forgeessentials.protection.effect.DamageEffect;
import com.forgeessentials.protection.effect.PotionEffect;
import com.forgeessentials.protection.effect.ZoneEffect;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

public class ProtectionEventHandler extends ServerEventHandler
{

    private boolean checkMajoritySleep;

    private Set<Entity> attackedEntities = new HashSet<>();

    /* ------------------------------------------------------------ */
    /* Entity permissions */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(AttackEntityEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (event.target == null)
            return;

        EntityPlayer source = event.entityPlayer;
        UserIdent sourceIdent = UserIdent.get(source);
        if (event.target instanceof EntityPlayer)
        {
            // player -> player
            EntityPlayer target = (EntityPlayer) event.target;
            if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, new WorldPoint(target), ModuleProtection.PERM_PVP))
            {
                event.setCanceled(true);
                return;
            }
        }

        // player -> entity
        handleDamageToEntityEvent(event, event.target, sourceIdent);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityAttackedEvent(SpecialEntityAttackedEvent event)
    {
    	handleEntitiesAttacked(event, event.source);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityAttackedEvent(LivingAttackEvent event)
    {
    	handleEntitiesAttacked(event, event.source);
    }

    private void handleEntitiesAttacked(EntityEvent event, DamageSource source) {
    	if (FMLCommonHandler.instance().getEffectiveSide().isClient() || source.getEntity() == null)
            return;

        UserIdent ident = null;
        if (source.getEntity() instanceof EntityPlayer)
            ident = UserIdent.get((EntityPlayer) source.getEntity());

        handleDamageToEntityEvent(event, event.entity, ident);
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingHurtEvent(LivingHurtEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (event.entityLiving == null)
            return;

        if (event.entityLiving instanceof EntityPlayer)
        {
            // living -> player (fall-damage, mob, dispenser, lava)
            EntityPlayer target = (EntityPlayer) event.entityLiving;
            {
                String permission = event.source.isExplosion() ? ModuleProtection.PERM_DAMAGE_BY + ".explosion"
                        : ModuleProtection.PERM_DAMAGE_BY + "." + event.source.damageType;
                ModuleProtection.debugPermission(target, permission);
                if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), permission))
                {
                    event.setCanceled(true);
                    return;
                }
            }

            if (event.source.getEntity() != null)
            {
                // non-player-entity (mob) -> player
                Entity source = event.source.getEntity();
                String permission = ModuleProtection.PERM_DAMAGE_BY + "." + getEntityName(source);
                ModuleProtection.debugPermission(target, permission);
                if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), permission))
                {
                    event.setCanceled(true);
                    return;
                }
                permission = MobType.getMobType(source).getDamageByPermission();
                ModuleProtection.debugPermission(target, permission);
                if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), permission))
                {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityInteractEvent(EntityInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.entityPlayer);
        WorldPoint point = new WorldPoint(event.target);
        String permission = ModuleProtection.PERM_INTERACT_ENTITY + "." + EntityList.getEntityString(event.target);
        ModuleProtection.debugPermission(event.entityPlayer, permission);
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
            return;
        }
    }

    public void handleDamageToEntityEvent(EntityEvent event, Entity target, UserIdent player)
    {
        if (attackedEntities.add(target))
        {
            WorldPoint point = new WorldPoint(target);
            String permission = ModuleProtection.PERM_DAMAGE_TO + "." + getEntityName(target);
            ModuleProtection.debugPermission(player == null ? null : player.getPlayer(), permission);
            if (!APIRegistry.perms.checkUserPermission(player, point, permission))
            {
                event.setCanceled(true);
                return;
            }

            MobType mobType = MobType.getMobType(target);
            if (mobType != MobType.UNKNOWN && !(target instanceof EntityPlayer))
            {
                permission = mobType.getDamageToPermission();
                ModuleProtection.debugPermission(player == null ? null : player.getPlayer(), permission);
                if (!APIRegistry.perms.checkUserPermission(player, point, permission))
                {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    /* ------------------------------------------------------------ */
    /* Block permissions */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void breakEvent(BreakEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        WorldPoint point = new WorldPoint(event);

        String permission = ModuleProtection.getBlockBreakPermission(event.block, event.blockMetadata);
        ModuleProtection.debugPermission(event.getPlayer(), permission);
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
            TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
            if (te != null)
                updateBrokenTileEntity((EntityPlayerMP) event.getPlayer(), te);
            if (PlayerInfo.get(ident).getHasFEClient())
            {
                int blockId = GameData.getBlockRegistry().getId(event.block);
                Set<Integer> ids = new HashSet<>();
                ids.add(blockId);
                NetworkUtils.netHandler.sendTo(new Packet3PlayerPermissions(false, null, ids), ident.getPlayerMP());
            }
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void placeEvent(BlockEvent.PlaceEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.player);
        Block block = event.world.getBlock(event.x, event.y, event.z);
        String permission = ModuleProtection.getBlockPlacePermission(block, event.world, event.x, event.y, event.z);
        ModuleProtection.debugPermission(event.player, permission);
        WorldPoint point = new WorldPoint(event.player.dimension, event.x, event.y, event.z);
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
        }
        if (stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE
                && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, point, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            ChatOutputHandler.chatError(event.player, Translator.translate("Cannot place block outside creative area"));
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.player);
        for (BlockSnapshot b : event.getReplacedBlockSnapshots())
        {
            Block block = event.world.getBlock(b.x, b.y, b.z);
            String permission = ModuleProtection.getBlockPlacePermission(block, event.world, event.x, event.y, event.z);
            ModuleProtection.debugPermission(event.player, permission);
            WorldPoint point = new WorldPoint(event.player.dimension, b.x, b.y, b.z);
            if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
            {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fireEvent(FireEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        String permission = (event instanceof FireEvent.Spread) ? ModuleProtection.PERM_FIRE_SPREAD : ModuleProtection.PERM_FIRE_DESTROY;
        WorldPoint point = new WorldPoint(event.world.provider.dimensionId, event.x, event.y, event.z);
        if (!APIRegistry.perms.checkUserPermission(null, point, permission))
        {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fallOnBlockEvent(FallOnBlockEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (event.fallHeight < 0.5) // Permission checks only for at least 1-block high fall events
            return;

        EntityPlayerMP player = (event.entity instanceof EntityPlayerMP) ? (EntityPlayerMP) event.entity : null;
        UserIdent ident = player == null ? null : UserIdent.get(player);
        WorldPoint point = new WorldPoint(event.world, event.x, event.y, event.z);
        Block block = event.block;
        int meta = point.getBlockMeta();

        String permission = ModuleProtection.getBlockTramplePermission(block, meta);
        ModuleProtection.debugPermission(player, permission);
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
            return;
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void explosionStartEvent(ExplosionEvent.Start event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = null;
        EntityLivingBase exploder = event.explosion.getExplosivePlacedBy();
        if (exploder instanceof EntityPlayer)
            ident = UserIdent.get((EntityPlayer) exploder);
        else if (exploder instanceof EntityLiving)
            ident = APIRegistry.IDENT_NPC;

        int cx = (int) Math.floor(event.explosion.explosionX);
        int cy = (int) Math.floor(event.explosion.explosionY);
        int cz = (int) Math.floor(event.explosion.explosionZ);
        int s = (int) Math.ceil(event.explosion.explosionSize);

        if (!APIRegistry.perms.checkUserPermission(ident, new WorldPoint(event.world, cx, cy, cz), ModuleProtection.PERM_EXPLOSION))
        {
            event.setCanceled(true);
            return;
        }
        for (int ix = -1; ix != 1; ix = 1)
            for (int iy = -1; iy != 1; iy = 1)
                for (int iz = -1; iz != 1; iz = 1)
                {
                    WorldPoint point = new WorldPoint(event.world, cx + s * ix, cy + s * iy, cz + s * iz);
                    if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_EXPLOSION))
                    {
                        event.setCanceled(true);
                        return;
                    }
                }
        // WorldArea area = new WorldArea(event.world, new Point(cx - s, cy - s, cz - s), new Point(cx + s, cy + s, cz +
        // s));
        // if (!APIRegistry.perms.checkUserPermission(ident, area, ModuleProtection.PERM_EXPLOSION))
        // {
        // event.setCanceled(true);
        // return;
        // }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void explosionDetonateEvent(ExplosionEvent.Detonate event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = null;
        EntityLivingBase exploder = event.explosion.getExplosivePlacedBy();
        if (exploder instanceof EntityPlayer)
            ident = UserIdent.get((EntityPlayer) exploder);
        else if (exploder instanceof EntityLiving)
            ident = APIRegistry.IDENT_NPC;

        List<ChunkPosition> positions = event.explosion.affectedBlockPositions;
        for (Iterator<ChunkPosition> it = positions.iterator(); it.hasNext();)
        {
            ChunkPosition pos = it.next();
            WorldPoint point = new WorldPoint(event.world, pos);
            String permission = ModuleProtection.getBlockExplosionPermission(point.getBlock(), point.getBlockMeta());
            if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
                it.remove();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.entityPlayer);

        WorldPoint point;
        if (event.action == RIGHT_CLICK_AIR)
        {
            MovingObjectPosition mop = PlayerUtil.getPlayerLookingSpot(event.entityPlayer);
            if (mop == null && event.x == 0 && event.y == 0 && event.z == 0)
                point = new WorldPoint(event.entityPlayer);
            else if (mop == null)
                point = new WorldPoint(event.world, event.x, event.y, event.z);
            else
                point = new WorldPoint(event.world, mop.blockX, mop.blockY, mop.blockZ);
        }
        else
            point = new WorldPoint(event.world, event.x, event.y, event.z);

        // Check for block interaction
        if (event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_BLOCK)
        {
            Block block = event.world.getBlock(event.x, event.y, event.z);
            String permission = ModuleProtection.getBlockInteractPermission(block, event.world, event.x, event.y, event.z);
            ModuleProtection.debugPermission(event.entityPlayer, permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, permission);
            event.useBlock = allow ? ALLOW : DENY;
        }

        // Check item (and block) usage
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack != null && !(stack.getItem() instanceof ItemBlock))
        {
            String permission = ModuleProtection.getItemUsePermission(stack);
            ModuleProtection.debugPermission(event.entityPlayer, permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, permission);
            event.useItem = allow ? ALLOW : DENY;
            if (!allow && PlayerInfo.get(ident).getHasFEClient())
            {
                int itemId = GameData.getItemRegistry().getId(stack.getItem());
                Set<Integer> ids = new HashSet<>();
                ids.add(itemId);
                NetworkUtils.netHandler.sendTo(new Packet3PlayerPermissions(false, ids, null), ident.getPlayerMP());
            }
        }

        if (anyCreativeModeAtPoint(event.entityPlayer, point)
                && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // If entity is in creative area, but player not, deny interaction
            event.useBlock = DENY;
            if (event.action != LEFT_CLICK_BLOCK)
                ChatOutputHandler.chatError(event.entityPlayer, Translator.translate("Cannot interact with creative area if not in creative mode."));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void pressurePlateEvent(PressurePlateEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = null;
        if (event.entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
            ident = UserIdent.get(player);
            ModuleProtection.debugPermission(player, ModuleProtection.PERM_PRESSUREPLATE);
        }
        if (!APIRegistry.perms.checkUserPermission(ident, ModuleProtection.PERM_PRESSUREPLATE))
            event.setCanceled(true);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerSleepInBedEventHigh(PlayerSleepInBedEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.entityPlayer);
        WorldPoint point = new WorldPoint(event.entity.dimension, event.x, event.y, event.z);
        if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_SLEEP))
        {
            event.result = EnumStatus.NOT_POSSIBLE_HERE;
            ChatOutputHandler.sendMessage(event.entityPlayer, Translator.translate("You are not allowed to sleep here"));
            return;
        }
        checkMajoritySleep = true;
    }

    private void checkMajoritySleep()
    {
        if (!checkMajoritySleep)
            return;
        checkMajoritySleep = false;

        WorldServer world = ServerUtil.getOverworld();
        if (FEConfig.majoritySleep >= 1 || world.isDaytime())
            return;

        int sleepingPlayers = 0;
        for (EntityPlayerMP player : ServerUtil.getPlayerList())
            if (player.isPlayerSleeping())
                sleepingPlayers++;
        float percentage = (float) sleepingPlayers / MinecraftServer.getServer().getCurrentPlayerCount();
        LoggingHandler.felog.debug(String.format("Players sleeping: %.0f%%", percentage * 100));

        if (percentage >= FEConfig.majoritySleep && percentage < 1)
        {
            if (world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
            {
                long time = world.getWorldInfo().getWorldTime() + 24000L;
                world.getWorldInfo().setWorldTime(time - time % 24000L);
            }
            for (EntityPlayerMP player : ServerUtil.getPlayerList())
                if (player.isPlayerSleeping())
                    player.wakeUpPlayer(false, false, true);
            // TODO: We change some vanilla behaviour here - is this ok?
            // Personally I think this is a good change though
            world.provider.resetRainAndThunder();
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkSpawnEvent(CheckSpawn event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (!(event.entityLiving instanceof EntityLiving))
            return;
        EntityLiving entity = (EntityLiving) event.entityLiving;
        WorldPoint point = new WorldPoint(entity);
        // TODO: Create a cache for spawn permissions
        if (!APIRegistry.perms.checkUserPermission(null, point, ModuleProtection.PERM_MOBSPAWN_NATURAL + "." + EntityList.getEntityString(entity)))
        {
            event.setResult(Result.DENY);
            return;
        }
        if (!APIRegistry.perms.checkUserPermission(null, point, MobType.getMobType(entity).getSpawnPermission(false)))
        {
            event.setResult(Result.DENY);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void specialSpawnEvent(SpecialSpawn event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (!(event.entityLiving instanceof EntityLiving))
            return;
        EntityLiving entity = (EntityLiving) event.entityLiving;
        WorldPoint point = new WorldPoint(entity);
        if (!APIRegistry.perms.checkUserPermission(null, point, ModuleProtection.PERM_MOBSPAWN_FORCED + "." + EntityList.getEntityString(entity)))
        {
            event.setResult(Result.DENY);
            return;
        }
        if (!APIRegistry.perms.checkUserPermission(null, point, MobType.getMobType(entity).getSpawnPermission(true)))
        {
            event.setResult(Result.DENY);
            return;
        }
    }

    /* ------------------------------------------------------------ */
    /* inventory / item permissions */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemPickupEvent(EntityItemPickupEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        UserIdent ident = UserIdent.get(event.entityPlayer);
        if (isItemBanned(ident, event.item.getEntityItem()))
        {
            event.setCanceled(true);
            event.item.setDead();
            return;
        }
        if (isInventoryItemBanned(ident, event.item.getEntityItem()))
        {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void entityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (event.entity instanceof EntityItem)
        {
            // 1) Do nothing if the whole world is creative!
            WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.world.provider.dimensionId);
            if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
            {
                // 2) If creative mode is set for any group at the location where the block was destroyed, prevent drops
                if (anyCreativeModeAtPoint(null, new WorldPoint(event.entity)))
                {
                    event.entity.setDead();
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void harvestDropsEvent(HarvestDropsEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        WorldPoint point = new WorldPoint(event.world, event.x, event.y, event.z);

        // 1) Do nothing if the whole world is creative!
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.world.provider.dimensionId);
        if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // 2) If creative mode is set for any group at the location where the block was destroyed, prevent drops
            if (anyCreativeModeAtPoint(null, point))
            {
                event.drops.clear();
                return;
            }
        }

        for (Iterator<ItemStack> iterator = event.drops.iterator(); iterator.hasNext();)
        {
            ItemStack stack = iterator.next();
            if (stack != null && isItemBanned(point, stack))
                iterator.remove();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemTossEvent(ItemTossEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        // 1) Do nothing if the whole world is creative!
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.entity.dimension);
        if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // 2) Destroy item when player in creative mode
            // 3) If creative mode is set for any group at the location where the block was destroyed, prevent drops
            if (getGamemode(event.player) == GameType.CREATIVE || anyCreativeModeAtPoint(event.player, new WorldPoint(event.entity)))
            {
                // event.entity.worldObj.removeEntity(event.target);
                event.entity.setDead();
                return;
            }
        }
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerOpenContainerEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        // If it's the player's own inventory - ignore
        if (event.entityPlayer.openContainer == event.entityPlayer.inventoryContainer)
            return;
        checkPlayerInventory(event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        checkPlayerInventory(event.player);
    }

    @SubscribeEvent
    public void playerChangedZoneEvent(PlayerChangedZone event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
        UserIdent ident = UserIdent.get(player);

        sendPermissionUpdate(ident, true);

        String inventoryGroup = APIRegistry.perms.getUserPermissionProperty(ident, event.afterPoint.toWorldPoint(), ModuleProtection.PERM_INVENTORY_GROUP);
        if (inventoryGroup == null)
            inventoryGroup = "default";

        GameType lastGm = stringToGameType(
                APIRegistry.perms.getUserPermissionProperty(ident, event.beforePoint.toWorldPoint(), ModuleProtection.PERM_GAMEMODE));
        GameType gm = stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, event.afterPoint.toWorldPoint(), ModuleProtection.PERM_GAMEMODE));
        if (gm != GameType.NOT_SET || lastGm != GameType.NOT_SET)
        {
            // If leaving a creative zone and no other gamemode is set, revert to default (survival)
            if (lastGm != GameType.NOT_SET && gm == GameType.NOT_SET)
                gm = GameType.SURVIVAL;

            GameType playerGm = player.theItemInWorldManager.getGameType();
            if (playerGm != gm)
            {
                // ChatOutputHandler.felog.info(String.format("Changing gamemode for %s from %s to %s",
                // ident.getUsernameOrUUID(), playerGm.getName(), gm.getName()));
                if (gm != GameType.CREATIVE)
                {
                    // TODO: Teleport player slightly above ground to prevent fall-death
                }
                player.setGameType(gm);
            }
            if (gm == GameType.CREATIVE)
                inventoryGroup = "creative";
        }

        // Apply inventory-group
        PlayerInfo pi = PlayerInfo.get(player);
        pi.setInventoryGroup(inventoryGroup);

        checkPlayerInventory(player);
    }

    public void sendPermissionUpdate(UserIdent ident, boolean reset)
    {
        if (!ident.hasPlayer()) // we can only send perm updates to players
            return;
        if (!PlayerInfo.get(ident).getHasFEClient())
            return;

        Set<Integer> placeIds = new HashSet<>();

        ModulePermissions.permissionHelper.disableDebugMode(true);

        ItemStack[] inventory = ident.getPlayer().inventory.mainInventory;
        for (int i = 0; i < (reset ? inventory.length : 9); ++i)
        {
            ItemStack stack = inventory[i];
            if (stack == null || !(stack.getItem() instanceof ItemBlock))
                continue;
            Block block = ((ItemBlock) stack.getItem()).field_150939_a;
            String permission = ModuleProtection.getBlockPlacePermission(block, 0);
            if (!APIRegistry.perms.checkUserPermission(ident, permission))
                placeIds.add(GameData.getBlockRegistry().getId(block));
        }

        ModulePermissions.permissionHelper.disableDebugMode(false);

        NetworkUtils.netHandler.sendTo(new Packet3PlayerPermissions(reset, placeIds, null), ident.getPlayerMP());
    }

    /* ------------------------------------------------------------ */

    private HashMap<UUID, List<ZoneEffect>> zoneEffects = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerChangedZoneEventHigh(PlayerChangedZone event)
    {
        UserIdent ident = UserIdent.get(event.entityPlayer);
        List<ZoneEffect> effects = getZoneEffects(ident);
        effects.clear();

        // Check knockback
        if (!APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_KNOCKBACK).equals(Zone.PERMISSION_FALSE))
        {
            sendZoneDeniedMessage(event.entityPlayer);

            Vec3 center = event.afterPoint.toVec3();
            if (event.afterZone instanceof AreaZone)
            {
                center = ((AreaZone) event.afterZone).getArea().getCenter().toVec3();
                center.yCoord = event.beforePoint.getY();
            }
            Vec3 delta = event.beforePoint.toVec3().subtract(center).normalize();
            WarpPoint target = new WarpPoint(event.beforePoint.getDimension(), event.beforePoint.getX() - delta.xCoord, event.beforePoint.getY() - delta.yCoord,
                    event.beforePoint.getZ() - delta.zCoord, event.afterPoint.getPitch(), event.afterPoint.getYaw());

            TeleportHelper.doTeleport((EntityPlayerMP) event.entityPlayer, target);
            event.setCanceled(true);
            return;
        }

        // Check command effect
        String command = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_COMMAND);
        if (command != null && !command.isEmpty())
        {
            int interval = ServerUtil
                    .parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_COMMAND_INTERVAL), 0);
            effects.add(new CommandEffect(ident.getPlayerMP(), interval, command));
        }

        int damage = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_DAMAGE), 0);
        if (damage > 0)
        {
            int interval = ServerUtil
                    .parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_DAMAGE_INTERVAL), 0);
            effects.add(new DamageEffect(ident.getPlayerMP(), interval, damage));
        }

        // Check potion effect
        String potion = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_POTION);
        if (potion != null && !potion.isEmpty())
        {
            int interval = ServerUtil
                    .parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_POTION_INTERVAL), 0);
            effects.add(new PotionEffect(ident.getPlayerMP(), interval, potion));
        }

        if (effects.isEmpty())
            zoneEffects.remove(ident.getUuid());
        else
            zoneEffects.put(ident.getUuid(), effects);
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event)
    {
        if (!attackedEntities.isEmpty())
            attackedEntities.clear();
        if (event.side != Side.SERVER || event.phase == TickEvent.Phase.END)
            return;
        for (List<ZoneEffect> effects : zoneEffects.values())
        {
            for (ZoneEffect effect : effects)
            {
                effect.update();
                if (effect.isLethal())
                    sendZoneDeniedMessage(effect.getPlayer());
            }
        }
        if (checkMajoritySleep)
            checkMajoritySleep();

        if (ServerUtil.getOverworld().getWorldInfo().getWorldTotalTime() % (20 * 4) == 0)
        {
            for (EntityPlayerMP player : ServerUtil.getPlayerList())
                sendPermissionUpdate(UserIdent.get(player), false);
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        zoneEffects.remove(event.player.getPersistentID());
    }

    @SubscribeEvent
    public void permissionChange(User.ModifyPermission e)
    {
        sendPermissionUpdate(e.ident, true);
    }

    @SubscribeEvent
    public void permissionChange(User.ModifyGroups e)
    {
        sendPermissionUpdate(e.ident, true);
    }

    @SubscribeEvent
    public void permissionChange(Group.ModifyPermission e)
    {
    	if(Zone.GROUP_DEFAULT.equals(e.group)||Zone.GROUP_PLAYERS.equals(e.group)) {
        	for (PlayerInfo info : PlayerInfo.getAll())
            {
                if (info.ident.hasPlayer()) {
                    sendPermissionUpdate(info.ident, true);
                }
            }
            return;
        }
        if (e.serverZone.getGroupPlayers().get(e.group) == null)
            return;
        for (UserIdent ident : e.serverZone.getGroupPlayers().get(e.group))
        {
            if (ident.hasPlayer())
                sendPermissionUpdate(ident, true);
        }
    }

    /* ------------------------------------------------------------ */

    public static String getEntityName(Entity target)
    {
        if (target instanceof EntityPlayer)
            return "Player";
        String name = EntityList.getEntityString(target);
        return name != null ? name : target.getClass().getSimpleName();
    }

    public static void updateBrokenTileEntity(final EntityPlayerMP player, final TileEntity te)
    {
        if (player == null || player.playerNetServerHandler == null)
            return;
        final Packet packet = te.getDescriptionPacket();
        if (packet == null)
            return;
        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                if (player.playerNetServerHandler != null)
                    player.playerNetServerHandler.sendPacket(packet);
            }
        });
    }

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
            GameType[] allGameTypes = GameType.values();
            for (GameType gameType : allGameTypes)
            {
                if (gameType.getName().equalsIgnoreCase(gm))
                    return gameType;
            }
            return GameType.NOT_SET;
        }
    }

    public static GameType getGamemode(EntityPlayer player)
    {
        return stringToGameType(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(player), ModuleProtection.PERM_GAMEMODE));
    }

    public static boolean anyCreativeModeAtPoint(EntityPlayer player, WorldPoint point)
    {
        if (player != null && stringToGameType(
                APIRegistry.perms.getUserPermissionProperty(UserIdent.get(player), point, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE)
            return true;
        for (String group : APIRegistry.perms.getServerZone().getGroups())
        {
            if (stringToGameType(APIRegistry.perms.getGroupPermissionProperty(group, point, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE)
                return true;
        }
        return false;
    }

    public static boolean isItemBanned(UserIdent ident, ItemStack stack)
    {
        if (stack == null)
            return false;
        return !APIRegistry.perms.checkUserPermission(ident, ModuleProtection.getItemBanPermission(stack));
    }

    public static boolean isItemBanned(WorldPoint point, ItemStack stack)
    {
        if (stack == null)
            return false;
        return !APIRegistry.perms.checkUserPermission(null, point, ModuleProtection.getItemBanPermission(stack));
    }

    public static boolean isInventoryItemBanned(UserIdent ident, ItemStack stack)
    {
        if (stack == null)
            return false;
        return !APIRegistry.perms.checkUserPermission(ident, ModuleProtection.getItemInventoryPermission(stack));
    }

    public static void checkPlayerInventory(EntityPlayer player)
    {
        UserIdent ident = UserIdent.get(player);
        for (int slotIdx = 0; slotIdx < player.inventory.getSizeInventory(); slotIdx++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slotIdx);
            if (stack != null)
            {
                if (isItemBanned(ident, stack))
                {
                    player.inventory.setInventorySlotContents(slotIdx, null);
                    continue;
                }
                if (isInventoryItemBanned(ident, stack))
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

    private static void sendZoneDeniedMessage(EntityPlayer player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout("zone_denied_message"))
        {
            ChatOutputHandler.chatError(player, ModuleProtection.MSG_ZONE_DENIED);
            pi.startTimeout("zone_denied_message", 4000);
        }
    }

    private List<ZoneEffect> getZoneEffects(UserIdent ident)
    {
        List<ZoneEffect> effects = zoneEffects.get(ident.getUuid());
        if (effects == null)
            effects = new ArrayList<>();
        return effects;
    }

}
