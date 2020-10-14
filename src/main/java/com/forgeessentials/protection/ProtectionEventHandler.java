package com.forgeessentials.protection;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fe.event.entity.EntityAttackedEvent;
import net.minecraftforge.fe.event.entity.FallOnBlockEvent;
import net.minecraftforge.fe.event.world.FireEvent;
import net.minecraftforge.fe.event.world.PressurePlateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

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
        if (event.getTarget() == null)
            return;

        EntityPlayer source = event.getEntityPlayer();
        UserIdent sourceIdent = UserIdent.get(source);
        if (event.getTarget() instanceof EntityPlayer)
        {
            // player -> player
            EntityPlayer target = (EntityPlayer) event.getTarget();
            if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, ModuleProtection.PERM_PVP)
                    || !APIRegistry.perms.checkUserPermission(sourceIdent, new WorldPoint(target), ModuleProtection.PERM_PVP))
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
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() || event.source.getTrueSource() == null)
            return;

        UserIdent ident = null;
        if (event.source.getTrueSource() instanceof EntityPlayer)
            ident = UserIdent.get((EntityPlayer) event.source.getTrueSource());

        handleDamageToEntityEvent(event, event.getEntity(), ident);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingHurtEvent(LivingHurtEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (event.getEntityLiving() == null)
            return;

        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            // living -> player (fall-damage, mob, dispenser, lava)
            EntityPlayer target = (EntityPlayer) event.getEntityLiving();
            {
                String permission = event.getSource().isExplosion() ? ModuleProtection.PERM_DAMAGE_BY + ".explosion"
                        : ModuleProtection.PERM_DAMAGE_BY + "." + event.getSource().damageType;
                ModuleProtection.debugPermission(target, permission);
                if (!APIRegistry.perms.checkUserPermission(UserIdent.get(target), permission))
                {
                    event.setCanceled(true);
                    return;
                }
            }

            if (event.getSource().getTrueSource() != null)
            {
                // non-player-entity (mob) -> player
                Entity source = event.getSource().getTrueSource();
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
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.getEntityPlayer());
        WorldPoint point = new WorldPoint(event.getTarget());

        String permission = ModuleProtection.PERM_INTERACT_ENTITY + "." + EntityList.getEntityString(event.getTarget());
        ModuleProtection.debugPermission(event.getEntityPlayer(), permission);
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
        IBlockState blockState = event.getWorld().getBlockState(event.getPos());
        String permission = ModuleProtection.getBlockBreakPermission(blockState);
        ModuleProtection.debugPermission(event.getPlayer(), permission);
        WorldPoint point = new WorldPoint(event.getPlayer().dimension, event.getPos());
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null)
                updateBrokenTileEntity((EntityPlayerMP) event.getPlayer(), te);
            if (PlayerInfo.get(ident).getHasFEClient())
            {
                int blockId = Block.REGISTRY.getIDForObject(blockState.getBlock());
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

        UserIdent ident = UserIdent.get(event.getPlayer());
        IBlockState blockState = event.getWorld().getBlockState(event.getPos());
        String permission = ModuleProtection.getBlockPlacePermission(blockState);
        ModuleProtection.debugPermission(event.getPlayer(), permission);
        WorldPoint point = new WorldPoint(event.getPlayer().dimension, event.getPos());
        if (!APIRegistry.perms.checkUserPermission(ident, point, permission))
        {
            event.setCanceled(true);
        }
        if (stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) == GameType.CREATIVE
                && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, point, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            ChatOutputHandler.chatError(event.getPlayer(), Translator.translate("Cannot place block outside creative area"));
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = UserIdent.get(event.getPlayer());
        for (BlockSnapshot b : event.getReplacedBlockSnapshots())
        {
            IBlockState blockState = event.getWorld().getBlockState(b.getPos());
            String permission = ModuleProtection.getBlockPlacePermission(blockState);
            ModuleProtection.debugPermission(event.getPlayer(), permission);
            WorldPoint point = new WorldPoint(event.getPlayer().dimension, b.getPos());
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
        WorldPoint point = new WorldPoint(event.getWorld().provider.getDimension(), event.getPos());
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

        EntityPlayerMP player = (event.getEntity() instanceof EntityPlayerMP) ? (EntityPlayerMP) event.getEntity() : null;
        UserIdent ident = player == null ? null : UserIdent.get(player);
        WorldPoint point = new WorldPoint(event.world, event.pos);

        String permission = ModuleProtection.getBlockTramplePermission(event.world.getBlockState(event.pos));
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
        EntityLivingBase exploder = event.getExplosion().getExplosivePlacedBy();
        if (exploder instanceof EntityPlayer)
            ident = UserIdent.get((EntityPlayer) exploder);
        else if (exploder instanceof EntityLiving)
            ident = APIRegistry.IDENT_NPC;

        Vec3d center = event.getExplosion().getPosition();
        int cx = (int) Math.floor(center.x);
        int cy = (int) Math.floor(center.y);
        int cz = (int) Math.floor(center.z);
        float size = event.getExplosion().size;
        int s = (int) Math.ceil(size);

        if (!APIRegistry.perms.checkUserPermission(ident, new WorldPoint(event.getWorld(), cx, cy, cz), ModuleProtection.PERM_EXPLOSION))
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
        // WorldArea area = new WorldArea(event.world, new Point(cx - s, cy - s, cz - s), new Point(cx + s, cy + s, cz +
        // s));
        // if (!APIRegistry.perms.checkUserPermission(ident, area, ModuleProtection.PERM_EXPLOSION))
        // {
        // event.setCanceled(true);
        // return;
        // }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void explosionDetonateEvent(ExplosionEvent.Detonate event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = null;
        EntityLivingBase exploder = event.getExplosion().getExplosivePlacedBy();
        if (exploder instanceof EntityPlayer)
            ident = UserIdent.get((EntityPlayer) exploder);
        else if (exploder instanceof EntityLiving)
            ident = APIRegistry.IDENT_NPC;

        List<BlockPos> positions = event.getExplosion().getAffectedBlockPositions();
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
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        // TODO (upgrade): Check, verify and optimize this

        UserIdent ident = UserIdent.get(event.getEntityPlayer());

        WorldPoint point;
        if (event instanceof RightClickEmpty || event instanceof RightClickItem)
        {
            RayTraceResult mop = PlayerUtil.getPlayerLookingSpot(event.getEntityPlayer());
            if (mop == null && event.getPos().getX() == 0 && event.getPos().getY() == 0 && event.getPos().getZ() == 0)
                point = new WorldPoint(event.getEntityPlayer());
            else if (mop == null)
                point = new WorldPoint(event.getEntityPlayer().dimension, event.getPos());
            else
                point = new WorldPoint(event.getEntityPlayer().dimension, mop.getBlockPos());
        }
        else
            point = new WorldPoint(event.getEntityPlayer().dimension, event.getPos());

        // Check for block interaction
        if (event instanceof LeftClickBlock || event instanceof RightClickBlock && !event.getEntityPlayer().isSneaking())
        {
            IBlockState blockState = event.getWorld().getBlockState(event.getPos());
            String permission = ModuleProtection.getBlockInteractPermission(blockState);
            ModuleProtection.debugPermission(event.getEntityPlayer(), permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, permission);
            if (event instanceof LeftClickBlock)
                ((LeftClickBlock) event).setUseBlock(allow ? ALLOW : DENY);
            else
                ((RightClickBlock) event).setUseBlock(allow ? ALLOW : DENY);
        }

        // Check item (and block) usage
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack != ItemStack.EMPTY && !(stack.getItem() instanceof ItemBlock))
        {
            String permission = ModuleProtection.getItemUsePermission(stack);
            ModuleProtection.debugPermission(event.getEntityPlayer(), permission);
            boolean allow = APIRegistry.perms.checkUserPermission(ident, point, permission);
            if (event instanceof LeftClickBlock)
            {
                ((LeftClickBlock) event).setUseItem(allow ? ALLOW : DENY);
            }
            else if (event instanceof RightClickBlock)
            {
                ((RightClickBlock) event).setUseItem(allow ? ALLOW : DENY);
            }

            if (!allow && PlayerInfo.get(ident).getHasFEClient())
            {
                int itemId = Item.REGISTRY.getIDForObject(stack.getItem());
                Set<Integer> ids = new HashSet<>();
                ids.add(itemId);
                NetworkUtils.netHandler.sendTo(new Packet3PlayerPermissions(false, ids, null), ident.getPlayerMP());
            }
        }

        if (anyCreativeModeAtPoint(event.getEntityPlayer(), point)
                && stringToGameType(APIRegistry.perms.getUserPermissionProperty(ident, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // If entity is in creative area, but player not, deny interaction
            System.out.println("THIS IS NOT IMPLEMENTED YET!!!!!!!!!!!!!!");
            // event.useBlock = DENY;
            if (!(event instanceof LeftClickBlock))
                ChatOutputHandler.chatError(event.getEntityPlayer(), Translator.translate("Cannot interact with creative area if not in creative mode."));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void pressurePlateEvent(PressurePlateEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        UserIdent ident = null;
        if (event.getEntity() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
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

        UserIdent ident = UserIdent.get(event.getEntityPlayer());
        WorldPoint point = new WorldPoint(event.getEntity().dimension, event.getPos());
        if (!APIRegistry.perms.checkUserPermission(ident, point, ModuleProtection.PERM_SLEEP))
        {
            event.setResult(SleepResult.NOT_POSSIBLE_HERE);
            ChatOutputHandler.sendMessage(event.getEntityPlayer(), Translator.translate("You are not allowed to sleep here"));
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
        float percentage = (float) sleepingPlayers / FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount();
        LoggingHandler.felog.debug(String.format("Players sleeping: %.0f%%", percentage * 100));

        if (percentage >= FEConfig.majoritySleep && percentage < 1)
        {
            if (world.getGameRules().getBoolean("doDaylightCycle"))
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
        if (!(event.getEntityLiving() instanceof EntityLiving))
            return;
        EntityLiving entity = (EntityLiving) event.getEntityLiving();
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
        if (!(event.getEntityLiving() instanceof EntityLiving))
            return;
        EntityLiving entity = (EntityLiving) event.getEntityLiving();
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
        UserIdent ident = UserIdent.get(event.getEntityPlayer());
        if (isItemBanned(ident, event.getItem().getItem()))
        {
            event.setCanceled(true);
            event.getItem().setDead();
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
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if (event.getEntity() instanceof EntityItem)
        {
            // 1) Do nothing if the whole world is creative!
            WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.getWorld().provider.getDimension());
            if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
            {
                // 2) If creative mode is set for any group at the location where the block was destroyed, prevent drops
                if (anyCreativeModeAtPoint(null, new WorldPoint(event.getEntity())))
                {
                    event.getEntity().setDead();
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

        WorldPoint point = new WorldPoint(event.getWorld(), event.getPos());

        // 1) Do nothing if the whole world is creative!
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.getWorld().provider.getDimension());
        if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // 2) If creative mode is set for any group at the location where the block was destroyed, prevent drops
            if (anyCreativeModeAtPoint(null, point))
            {
                event.getDrops().clear();
                return;
            }
        }

        for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();)
        {
            ItemStack stack = iterator.next();
            if (stack != ItemStack.EMPTY && isItemBanned(point, stack))
                iterator.remove();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemTossEvent(ItemTossEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        // 1) Do nothing if the whole world is creative!
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(event.getEntity().dimension);
        if (stringToGameType(worldZone.getGroupPermission(Zone.GROUP_DEFAULT, ModuleProtection.PERM_GAMEMODE)) != GameType.CREATIVE)
        {
            // 2) Destroy item when player in creative mode
            // 3) If creative mode is set for any group at the location where the block was destroyed, prevent drops
            if (getGamemode(event.getPlayer()) == GameType.CREATIVE || anyCreativeModeAtPoint(event.getPlayer(), new WorldPoint(event.getEntity())))
            {
                // event.entity.world.removeEntity(event.getTarget());
                event.getEntity().setDead();
                return;
            }
        }
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerContainerEvent.Open event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        // If it's the player's own inventory - ignore
        if (event.getEntityPlayer().openContainer == event.getEntityPlayer().inventoryContainer)
            return;
        checkPlayerInventory(event.getEntityPlayer());
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
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
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

            GameType playerGm = player.interactionManager.getGameType();
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

        NonNullList<ItemStack> inventory = ident.getPlayer().inventory.mainInventory;
        for (int i = 0; i < (reset ? inventory.size() : 9); ++i)
        {
            ItemStack stack = inventory.get(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock))
                continue;
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            String permission = ModuleProtection.getBlockPlacePermission(block, 0);
            if (!APIRegistry.perms.checkUserPermission(ident, permission))
                placeIds.add(Block.REGISTRY.getIDForObject(block));
        }

        ModulePermissions.permissionHelper.disableDebugMode(false);

        NetworkUtils.netHandler.sendTo(new Packet3PlayerPermissions(reset, placeIds, null), ident.getPlayerMP());
    }

    /* ------------------------------------------------------------ */

    private HashMap<UUID, List<ZoneEffect>> zoneEffects = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerChangedZoneEventHigh(PlayerChangedZone event)
    {
        UserIdent ident = UserIdent.get(event.getEntityPlayer());
        List<ZoneEffect> effects = getZoneEffects(ident);
        effects.clear();

        // Check knockback
        if (!APIRegistry.perms.getUserPermissionProperty(ident, event.afterZone, ModuleProtection.ZONE_KNOCKBACK).equals(Zone.PERMISSION_FALSE))
        {
            sendZoneDeniedMessage(event.getEntityPlayer());

            Vec3d center = event.afterPoint.toVec3();
            if (event.afterZone instanceof AreaZone)
            {
                center = ((AreaZone) event.afterZone).getArea().getCenter().toVec3();
                center = new Vec3d(center.x, event.beforePoint.getY(), center.y);
            }
            Vec3d delta = event.beforePoint.toVec3().subtract(center).normalize();
            WarpPoint target = new WarpPoint(event.beforePoint.getDimension(), event.beforePoint.getX() - delta.x, event.beforePoint.getY() - delta.y,
                    event.beforePoint.getZ() - delta.z, event.afterPoint.getPitch(), event.afterPoint.getYaw());

            TeleportHelper.doTeleport((EntityPlayerMP) event.getEntityPlayer(), target);
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
        if (player == null || player.connection == null)
            return;
        final Packet<?> packet = te.getUpdatePacket();
        if (packet == null)
            return;
        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                if (player.connection != null)
                    player.connection.sendPacket(packet);
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

    public static void checkPlayerInventory(EntityPlayer player)
    {
        UserIdent ident = UserIdent.get(player);
        for (int slotIdx = 0; slotIdx < player.inventory.getSizeInventory(); slotIdx++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slotIdx);
            if (stack != ItemStack.EMPTY)
            {
                if (isItemBanned(ident, stack))
                {
                    player.inventory.setInventorySlotContents(slotIdx, ItemStack.EMPTY);
                    continue;
                }
                if (isInventoryItemBanned(ident, stack))
                {
                    EntityItem droppedItem = player.dropItem(stack, true, false);
                    droppedItem.motionX = 0;
                    droppedItem.motionY = 0;
                    droppedItem.motionZ = 0;
                    player.inventory.setInventorySlotContents(slotIdx, ItemStack.EMPTY);
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
