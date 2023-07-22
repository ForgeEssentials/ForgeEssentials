package com.forgeessentials.protection;

import static net.minecraftforge.eventbus.api.Event.Result.DENY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.PermissionEvent.Group;
import com.forgeessentials.api.permissions.PermissionEvent.User;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet03PlayerPermissions;
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
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.entity.EntityAttackedEvent;
import com.forgeessentials.util.events.entity.PressurePlateEvent;
import com.forgeessentials.util.events.player.PlayerChangedZone;
import com.forgeessentials.util.events.world.FireEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityMultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class ProtectionEventHandler extends ServerEventHandler
{

    private boolean checkMajoritySleep;

    private Set<Entity> attackedEntities = new HashSet<>();

    /* ------------------------------------------------------------ */
    /* Entity permissions */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(AttackEntityEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (event.getTarget() == null)
            return;

        PlayerEntity source = event.getPlayer();
        UserIdent sourceIdent = UserIdent.get(source);
        if (event.getTarget() instanceof PlayerEntity)
        {
            // player -> player
            PlayerEntity target = (PlayerEntity) event.getTarget();
            if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, new WorldPoint(target),
                            ModuleProtection.PERM_PVP))
            {
                event.setCanceled(true);
                return;
            }
        }

        // player -> entity
        handleDamageToEntityEvent(event, event.getTarget(), sourceIdent);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityAttackedEvent(EntityAttackedEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || event.getSource().getDirectEntity() == null)
            return;

        UserIdent ident = null;
        if (event.getSource().getDirectEntity() instanceof PlayerEntity)
            ident = UserIdent.get((PlayerEntity) event.getSource().getDirectEntity());

        handleDamageToEntityEvent(event, event.getEntity(), ident);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingHurtEvent(LivingHurtEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        if (event.getEntityLiving() == null)
            return;

        if (event.getEntityLiving() instanceof PlayerEntity)
        {
            // living -> player (fall-damage, mob, dispenser, lava)
            PlayerEntity target = (PlayerEntity) event.getEntityLiving();
            {
                String permission = event.getSource().isExplosion() ? ModuleProtection.PERM_DAMAGE_BY + ".explosion"
                        : ModuleProtection.PERM_DAMAGE_BY + "." + event.getSource().getMsgId();
                ModuleProtection.debugPermission(target, permission);
                if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), permission))
                {
                    event.setCanceled(true);
                    return;
                }
            }

            if (event.getSource().getDirectEntity() != null)
            {
                // non-player-entity (mob) -> player
                Entity source = event.getSource().getDirectEntity();
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
    public void entityInteractEvent(PlayerInteractEvent.EntityInteract event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        WorldPoint point = new WorldPoint(event.getTarget());

        String permission = ModuleProtection.PERM_INTERACT_ENTITY + "."
                + event.getTarget().getType().getDescriptionId();
        ModuleProtection.debugPermission(event.getPlayer(), permission);
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
            if (mobType != MobType.UNKNOWN && !(target instanceof PlayerEntity))
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
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        BlockState blockState = event.getWorld().getBlockState(event.getPos());
        String permission = ModuleProtection.getBlockBreakPermission(blockState.getBlock());
        ModuleProtection.debugPermission(event.getPlayer(), permission);
        WorldPoint point = new WorldPoint(event.getPlayer().level, event.getPos());
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
            TileEntity te = event.getWorld().getBlockEntity(event.getPos());
            if (te != null)
                updateBrokenTileEntity((ServerPlayerEntity) event.getPlayer(), te);
            if (PlayerInfo.get(ident).getHasFEClient())
            {
                String blockId = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();
                Set<String> ids = new HashSet<>();
                ids.add(blockId);
                NetworkUtils.sendTo(new Packet03PlayerPermissions(false, null, ids), ident.getPlayerMP());
            }
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void placeEvent(EntityPlaceEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            UserIdent ident = UserIdent.get(player);
            BlockState blockState = event.getWorld().getBlockState(event.getPos());
            String permission = ModuleProtection.getBlockPlacePermission(blockState);
            ModuleProtection.debugPermission(player, permission);
            WorldPoint point = new WorldPoint(player.level, event.getPos());
            if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
            {
                event.setCanceled(true);
            }
            if (stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident,
                    ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE
                    && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, point,
                            ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
            {
                ChatOutputHandler.chatError(player, Translator.translate("Cannot place block outside creative area"));
                event.setCanceled(true);
                return;
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void multiPlaceEvent(EntityMultiPlaceEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (event.getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            UserIdent ident = UserIdent.get(player);
            for (BlockSnapshot b : event.getReplacedBlockSnapshots())
            {
                BlockState blockState = event.getWorld().getBlockState(b.getPos());
                String permission = ModuleProtection.getBlockPlacePermission(blockState);
                ModuleProtection.debugPermission(player, permission);
                WorldPoint point = new WorldPoint(player.level, b.getPos());
                if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
                {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fireEvent(FireEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        String permission = (event instanceof FireEvent.Spread) ? ModuleProtection.PERM_FIRE_SPREAD
                : ModuleProtection.PERM_FIRE_DESTROY;
        WorldPoint point = new WorldPoint(event.getWorld(), event.getPos());
        if (!APIRegistry.perms.checkUserPermission(null, point, permission))
        {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fallOnBlockEvent(BlockEvent.FarmlandTrampleEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (event.getFallDistance() < 0.5) // Permission checks only for at least 1-block high fall events
            return;

        ServerPlayerEntity player = (event.getEntity() instanceof ServerPlayerEntity)
                ? (ServerPlayerEntity) event.getEntity()
                : null;
        UserIdent ident = player == null ? null : UserIdent.get(player);
        WorldPoint point = new WorldPoint(event.getWorld(), event.getPos());

        String permission = ModuleProtection.getBlockTramplePermission(event.getWorld().getBlockState(event.getPos()));
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
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        UserIdent ident = null;
        Entity exploder = event.getExplosion().getExploder();
        if (exploder instanceof PlayerEntity)
            ident = UserIdent.get((PlayerEntity) exploder);
        else if (exploder instanceof LivingEntity)
            ident = APIRegistry.IDENT_NPC;

        Vector3d center = event.getExplosion().getPosition();
        int cx = (int) Math.floor(center.x);
        int cy = (int) Math.floor(center.y);
        int cz = (int) Math.floor(center.z);
        Explosion explosion = event.getExplosion();
        // Store the value of private field in variable
        float size;

        try {
            size = (float) ObfuscationReflectionHelper.getPrivateValue(Explosion.class, explosion, "field_77280_f");
        } catch (UnableToAccessFieldException e) {
            e.printStackTrace();
            size = 4;
        }

        int s = (int) Math.ceil(size);

        if (!APIRegistry.perms.checkUserPermission(ident, new WorldPoint(event.getWorld(), cx, cy, cz),
                ModuleProtection.PERM_EXPLOSION))
        {
            event.setCanceled(true);
            return;
        }
        for (int ix = -1; ix != 1; ix = 1)
            for (int iy = -1; iy != 1; iy = 1)
                for (int iz = -1; iz != 1; iz = 1)
                {
                    WorldPoint point = new WorldPoint(event.getWorld(), cx + s * ix, cy + s * iy, cz + s * iz);
                    if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_EXPLOSION))
                    {
                        event.setCanceled(true);
                        return;
                    }
                }
        // WorldArea area = new WorldArea(event.world, new Point(cx - s, cy - s, cz -
        // s), new Point(cx + s, cy + s, cz +
        // s));
        // if (!APIRegistry.perms.checkUserPermission(ident, area,
        // ModuleProtection.PERM_EXPLOSION))
        // {
        // event.setCanceled(true);
        // return;
        // }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void explosionDetonateEvent(ExplosionEvent.Detonate event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        UserIdent ident = null;
        Entity exploder = event.getExplosion().getExploder();
        if (exploder instanceof PlayerEntity)
            ident = UserIdent.get((PlayerEntity) exploder);
        else if (exploder instanceof LivingEntity)
            ident = APIRegistry.IDENT_NPC;

        List<BlockPos> positions = event.getExplosion().getToBlow();
        for (Iterator<BlockPos> it = positions.iterator(); it.hasNext();)
        {
            BlockPos pos = it.next();
            WorldPoint point = new WorldPoint(event.getWorld(), pos);
            String permission = ModuleProtection.getBlockExplosionPermission(point.getWorld().getBlockState(pos));
            if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
                it.remove();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        // TODO (upgrade): Check, verify and optimize this

        UserIdent ident = UserIdent.get(event.getPlayer());

        WorldPoint point;
        if (event instanceof RightClickItem)
        {
            RayTraceResult mop = PlayerUtil.getPlayerLookingSpot(event.getPlayer());
            if (mop.getType() == RayTraceResult.Type.MISS && event.getPos().getX() == 0 && event.getPos().getY() == 0
                    && event.getPos().getZ() == 0)
                point = new WorldPoint(event.getPlayer());
            else if (mop.getType() == RayTraceResult.Type.MISS)
                point = new WorldPoint(event.getPlayer().level, event.getPos());
            else
                point = new WorldPoint(event.getPlayer().level,
                        new BlockPos(mop.getLocation().x, mop.getLocation().y, mop.getLocation().z));
        }
        else
            point = new WorldPoint(event.getPlayer().level, event.getPos());

        // Check for block interaction
        if (event instanceof LeftClickBlock || event instanceof RightClickBlock)
        {
            BlockState blockState = event.getWorld().getBlockState(event.getPos());
            String permission = ModuleProtection.getBlockInteractPermission(blockState);
            ModuleProtection.debugPermission(event.getPlayer(), permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, permission);
            if (!allow)
            {
                if (event instanceof LeftClickBlock)
                {
                    ((LeftClickBlock) event).setUseBlock(DENY);
                }
                else
                {
                    ((RightClickBlock) event).setUseBlock(DENY);
                }
            }
        }

        // Check item (and block) usage
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (stack != ItemStack.EMPTY && !(stack.getItem() instanceof BlockItem))
        {
            String permission = ModuleProtection.getItemUsePermission(stack);
            ModuleProtection.debugPermission(event.getPlayer(), permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, permission);
            if (!allow)
            {
                if (event instanceof LeftClickBlock)
                {
                    ((LeftClickBlock) event).setUseItem(DENY);
                }
                else if (event instanceof RightClickBlock)
                {
                    ((RightClickBlock) event).setUseItem(DENY);
                }
                else if (event instanceof RightClickItem)
                {
                    // Prevents use without clicking on a block (bow, buckets, etc...)
                    event.setCanceled(true);
                }
            }

            if (!allow && PlayerInfo.get(ident).getHasFEClient())
            {
                String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
                Set<String> ids = new HashSet<>();
                ids.add(itemId);
                NetworkUtils.sendTo(new Packet03PlayerPermissions(false, ids, null), ident.getPlayerMP());
            }
        }

        if (anyCreativeModeAtPoint(event.getPlayer(), point) && stringToGameType(APIRegistry.perms
                .getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // If entity is in creative area, but player not, deny interaction
            System.out.println("THIS IS NOT IMPLEMENTED YET!!!!!!!!!!!!!!");
            // event.useBlock = DENY;
            if (!(event instanceof LeftClickBlock))
                ChatOutputHandler.chatError(event.getPlayer(),
                        Translator.translate("Cannot interact with creative area if not in creative mode."));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void pressurePlateEvent(PressurePlateEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        UserIdent ident = null;
        if (event.getEntity() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
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
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        WorldPoint point = new WorldPoint(event.getEntity().level, event.getPos());
        if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_SLEEP))
        {
            event.setResult(SleepResult.NOT_POSSIBLE_HERE);
            ChatOutputHandler.sendMessage(event.getPlayer().createCommandSourceStack(),
                    Translator.translate("You are not allowed to sleep here"));
            return;
        }
        checkMajoritySleep = true;
    }

    private void checkMajoritySleep()
    {
        if (!checkMajoritySleep)
            return;
        checkMajoritySleep = false;

        ServerWorld world = ServerUtil.getOverworld();
        if (FEConfig.majoritySleep >= 1 || world.isDay())
            return;

        int sleepingPlayers = 0;
        for (ServerPlayerEntity player : ServerUtil.getPlayerList())
            if (player.isSleeping())
                sleepingPlayers++;
        float percentage = (float) sleepingPlayers / ServerLifecycleHooks.getCurrentServer().getPlayerCount();
        LoggingHandler.felog.debug(String.format("Players sleeping: %.0f%%", percentage * 100));

        if (percentage >= FEConfig.majoritySleep && percentage < 1)
        {
            if (world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT))
            {
                long time = world.getWorldServer().getDayTime() + 24000L;
                world.getWorldServer().setDayTime(time - time % 24000L);
            }
            for (ServerPlayerEntity player : ServerUtil.getPlayerList())
                if (player.isSleeping())
                    player.stopSleeping();
            // TODO: We change some vanilla behaviour here - is this ok?
            // Personally I think this is a good change though
            world.rainLevel = 0.0f;
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkSpawnEvent(CheckSpawn event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (!(event.getEntityLiving() instanceof LivingEntity))
            return;
        LivingEntity entity = (LivingEntity) event.getEntityLiving();
        WorldPoint point = new WorldPoint(entity);
        // TODO: Create a cache for spawn permissions
        if (!APIRegistry.perms.checkUserPermission(null, point,
                ModuleProtection.PERM_MOBSPAWN_NATURAL + "." + entity.getType().getDescriptionId()))
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
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (!(event.getEntityLiving() instanceof LivingEntity))
            return;
        LivingEntity entity = (LivingEntity) event.getEntityLiving();
        WorldPoint point = new WorldPoint(entity);
        if (!APIRegistry.perms.checkUserPermission(null, point,
                ModuleProtection.PERM_MOBSPAWN_FORCED + "." + entity.getType().getDescriptionId()))
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
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        UserIdent ident = UserIdent.get(event.getPlayer());
        if (isItemBanned(ident, event.getItem().getItem()))
        {
            event.setCanceled(true);
            event.getItem().remove();
            return;
        }
        if (isInventoryItemBanned(ident, event.getItem().getItem()))
        {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void entityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        if (event.getEntity() instanceof ItemEntity)
        {
            // 1) Do nothing if the whole world is creative!
            WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.getWorld());
            if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT,
                    ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
            {
                // 2) If creative mode is set for any group at the location where the block was
                // destroyed, prevent drops
                if (anyCreativeModeAtPoint(null, new WorldPoint(event.getEntity())))
                {
                    event.getEntity().remove();
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void harvestDropsEvent(FarmlandTrampleEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        WorldPoint point = new WorldPoint(event.getWorld(), event.getPos());

        // 1) Do nothing if the whole world is creative!
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.getWorld());
        if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT,
                ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // 2) If creative mode is set for any group at the location where the block was
            // destroyed, prevent drops
            if (anyCreativeModeAtPoint(null, point))
            {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemTossEvent(ItemTossEvent event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;

        // 1) Do nothing if the whole world is creative!
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.getEntity().level);
        if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT,
                ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // 2) Destroy item when player in creative mode
            // 3) If creative mode is set for any group at the location where the block was
            // destroyed, prevent drops
            if (getGamemode(event.getPlayer()) == GameType.CREATIVE
                    || anyCreativeModeAtPoint(event.getPlayer(), new WorldPoint(event.getEntity())))
            {
                // event.entity.world.removeEntity(event.getTarget());
                event.getEntity().remove();
                return;
            }
        }
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerContainerEvent.Open event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        // If it's the player's own inventory - ignore
        if (event.getPlayer().containerMenu == event.getPlayer().inventoryMenu)
            return;
        checkPlayerInventory(event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        checkPlayerInventory(event.getPlayer());
    }

    @SubscribeEvent
    public void playerChangedZoneEvent(PlayerChangedZone event)
    {
        if (!ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return;
        if (!(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        UserIdent ident = UserIdent.get(player);

        sendPermissionUpdate(ident, true);

        String inventoryGroup = APIRegistry.perms.getUserPermissionProperty(ident, event.afterPoint.toWorldPoint(),
                ModuleProtection.PERM_INVENTORY_GROUP);
        if (inventoryGroup == null)
            inventoryGroup = "default";

        GameType lastGm = stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident,
                event.beforePoint.toWorldPoint(), ModuleProtection.PERM_GAMEMODE));
        GameType gm = stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident,
                event.afterPoint.toWorldPoint(), ModuleProtection.PERM_GAMEMODE));
        if (gm != GameType.NOT_SET || lastGm != GameType.NOT_SET)
        {
            // If leaving a creative zone and no other gamemode is set, revert to default
            // (survival)
            if (lastGm != GameType.NOT_SET && gm == GameType.NOT_SET)
                gm = GameType.SURVIVAL;

            GameType playerGm = player.gameMode.getGameModeForPlayer();
            if (playerGm != gm)
            {
                // ChatOutputHandler.felog.info(String.format("Changing gamemode for %s from %s
                // to %s",
                // ident.getUsernameOrUUID(), playerGm.getName(), gm.getName()));
                if (gm != GameType.CREATIVE)
                {
                    // TODO: Teleport player slightly above ground to prevent fall-death
                }
                player.setGameMode(gm);
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
        if (!PlayerInfo.get(ident).getHasFEClient()) // we can only send perm updates to players who have the client
            return;

        Set<String> placeIds = new HashSet<>();

        ModulePermissions.permissionHelper.disableDebugMode(true);

        NonNullList<ItemStack> inventory = ident.getPlayer().inventory.items;
        for (int i = 0; i < (reset ? inventory.size() : 9); ++i)
        {
            ItemStack stack = inventory.get(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof BlockItem))
                continue;
            Block block = ((BlockItem) stack.getItem()).getBlock();
            String permission = ModuleProtection.getBlockPlacePermission(block);
            if (!APIRegistry.perms.checkUserPermission(ident, permission))
                placeIds.add(ForgeRegistries.BLOCKS.getKey(block.getBlock()).toString());
        }

        ModulePermissions.permissionHelper.disableDebugMode(false);

        NetworkUtils.sendTo(new Packet03PlayerPermissions(reset, placeIds, null), ident.getPlayerMP());
    }

    /* ------------------------------------------------------------ */

    private HashMap<UUID, List<ZoneEffect>> zoneEffects = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerChangedZoneEventHigh(PlayerChangedZone event)
    {
        UserIdent ident = UserIdent.get(event.getPlayer());
        List<ZoneEffect> effects = getZoneEffects(ident);
        effects.clear();

        // Check knockback
        if (!APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_KNOCKBACK)
                .equals(Zone.PERMISSION_FALSE))
        {
            sendZoneDeniedMessage(event.getPlayer());

            Vector3d center = event.afterPoint.toVec3();
            if (event.afterZone instanceof AreaZone)
            {
                center = ((AreaZone) event.afterZone).getArea().getCenter().toVec3();
                center = new Vector3d(center.x, event.beforePoint.getY(), center.y);
            }
            Vector3d delta = event.beforePoint.toVec3().subtract(center).normalize();
            WarpPoint target = new WarpPoint(event.beforePoint.getDimension(), event.beforePoint.getX() - delta.x,
                    event.beforePoint.getY() - delta.y, event.beforePoint.getZ() - delta.z, event.afterPoint.getPitch(),
                    event.afterPoint.getYaw());

            TeleportHelper.doTeleport((ServerPlayerEntity) event.getPlayer(), target);
            event.setCanceled(true);
            return;
        }

        // Check command effect
        String command = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone,
                ModuleProtection.ZONE_COMMAND);
        if (command != null && !command.isEmpty())
        {
            int interval = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident,
                    event.afterZone, ModuleProtection.ZONE_COMMAND_INTERVAL), 0);
            effects.add(new CommandEffect(ident.getPlayerMP(), interval, command));
        }

        int damage = ServerUtil.parseIntDefault(
                APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_DAMAGE), 0);
        if (damage > 0)
        {
            int interval = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident,
                    event.afterZone, ModuleProtection.ZONE_DAMAGE_INTERVAL), 0);
            effects.add(new DamageEffect(ident.getPlayerMP(), interval, damage));
        }

        // Check potion effect
        String potion = APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone,
                ModuleProtection.ZONE_POTION);
        if (potion != null && !potion.isEmpty())
        {
            int interval = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident,
                    event.afterZone, ModuleProtection.ZONE_POTION_INTERVAL), 0);
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
        if (event.side != LogicalSide.SERVER || event.phase == TickEvent.Phase.END)
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

        if (ServerUtil.getOverworld().getWorldServer().getGameTime() % (20 * 4) == 0)
        {
            for (ServerPlayerEntity player : ServerUtil.getPlayerList())
                sendPermissionUpdate(UserIdent.get(player), false);
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        zoneEffects.remove(event.getPlayer().getGameProfile().getId());
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
    	System.out.println("1");
    	if(Zone.GROUP_DEFAULT.equals(e.group)||Zone.GROUP_PLAYERS.equals(e.group)) {
        	System.out.println("2");
        	for (PlayerInfo info : PlayerInfo.getAll())
            {
                if (info.ident.hasPlayer()) {
                    sendPermissionUpdate(info.ident, true);
                }
            }
            return;
        }
    	if (e.serverZone.getGroupPlayers().get(e.group) == null) {
    		return;
    	}
        for (UserIdent ident : e.serverZone.getGroupPlayers().get(e.group))
        {
            if (ident.hasPlayer()) {
                sendPermissionUpdate(ident, true);
            }
        }
    }

    /* ------------------------------------------------------------ */

    public static String getEntityName(Entity target)
    {
        if (target instanceof PlayerEntity)
            return "Player";
        String name = target.getType().getDescriptionId();
        return name != null ? name : target.getClass().getSimpleName();
    }

    public static void updateBrokenTileEntity(final ServerPlayerEntity player, final TileEntity te)
    {
        if (player == null || player.connection == null)
            return;
        final IPacket<?> packet = te.getUpdatePacket();
        if (packet == null)
            return;
        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                if (player.connection != null)
                    player.connection.send(packet);
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
            return GameType.NOT_SET;
        }
    }

    public static GameType getGamemode(PlayerEntity player)
    {
        return stringToGameType(
                APIRegistry.perms.getUserPermissionProperty(UserIdent.get(player), ModuleProtection.PERM_GAMEMODE));
    }

    public static boolean anyCreativeModeAtPoint(PlayerEntity player, WorldPoint point)
    {
        if (player != null && stringToGameType(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(player), point,
                ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE)
            return true;
        for (String group : APIRegistry.perms.getServerZone().getGroups())
        {
            if (stringToGameType(APIRegistry.perms.getGroupPermissionProperty(group, point,
                    ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE)
                return true;
        }
        return false;
    }

    public static boolean isItemBanned(UserIdent ident, ItemStack stack)
    {
        if (stack == ItemStack.EMPTY)
            return false;
        return !APIRegistry.perms.checkUserPermission(ident, ModuleProtection.getItemBanPermission(stack));
    }

    public static boolean isItemBanned(WorldPoint point, ItemStack stack)
    {
        if (stack == ItemStack.EMPTY)
            return false;
        return !APIRegistry.perms.checkUserPermission(null, point, ModuleProtection.getItemBanPermission(stack));
    }

    public static boolean isInventoryItemBanned(UserIdent ident, ItemStack stack)
    {
        if (stack == ItemStack.EMPTY)
            return false;
        return !APIRegistry.perms.checkUserPermission(ident, ModuleProtection.getItemInventoryPermission(stack));
    }

    public static void checkPlayerInventory(PlayerEntity player)
    {
        UserIdent ident = UserIdent.get(player);
        for (int slotIdx = 0; slotIdx < player.inventory.getContainerSize(); slotIdx++)
        {
            ItemStack stack = player.inventory.getItem(slotIdx);
            if (stack != ItemStack.EMPTY)
            {
                if (isItemBanned(ident, stack))
                {
                    player.inventory.setItem(slotIdx, ItemStack.EMPTY);
                    continue;
                }
                if (isInventoryItemBanned(ident, stack))
                {
                    ItemEntity droppedItem = player.drop(stack, true, false);
                    if (droppedItem != null)
                    {
                        droppedItem.setDeltaMovement(0, 0, 0);
                        player.inventory.setItem(slotIdx, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private static void sendZoneDeniedMessage(PlayerEntity playerEntity)
    {
        PlayerInfo pi = PlayerInfo.get(playerEntity);
        if (pi.checkTimeout("zone_denied_message"))
        {
            ChatOutputHandler.chatError(playerEntity, ModuleProtection.MSG_ZONE_DENIED);
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
