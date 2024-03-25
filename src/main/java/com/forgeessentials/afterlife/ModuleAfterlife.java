package com.forgeessentials.afterlife;

import java.util.ArrayList;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Module to handle death-chest and respawn debuffs.
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class, defaultModule = false, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleAfterlife extends ServerEventHandler
{

    @FEModule.Instance
    public static ModuleAfterlife instance;

    public static final String PERM = "fe.afterlife";
    public static final String PERM_DEBUFFS = PERM + ".debuffs";
    public static final String PERM_HP = PERM + ".hp";
    public static final String PERM_FOOD = PERM + ".food";

    public static final String PERM_DEATHCHEST = PERM + ".deathchest";
    public static final String PERM_DEATHCHEST_XP = PERM_DEATHCHEST + ".xp";
    public static final String PERM_DEATHCHEST_BLOCK = PERM_DEATHCHEST + ".block";
    public static final String PERM_DEATHCHEST_FENCE = PERM_DEATHCHEST + ".fence";
    public static final String PERM_DEATHCHEST_SAFETIME = PERM_DEATHCHEST + ".safetime";
    public static final String PERM_DEATHCHEST_BYPASS = PERM_DEATHCHEST + ".bypass";

    @SubscribeEvent
    public static void registerTE(RegistryEvent.Register<BlockEntityType<?>> evt)
    {
        BlockEntityType<?> type = BlockEntityType.Builder.of(TileEntitySkullGrave::new, Blocks.SKELETON_SKULL)
                .build(null);
        type.setRegistryName("ForgeEssentials", "FESkull");
        evt.getRegistry().register(type);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        Grave.loadAll();

        APIRegistry.perms.registerPermissionDescription(PERM, "Permissions for afterlife configuration");
        APIRegistry.perms.registerPermissionDescription(PERM_DEBUFFS,
                "Potion effects to apply on respawn (comma separated list of id:duration:amplifier)");
        APIRegistry.perms.registerPermissionDescription(PERM_HP, "Respawn HP");
        APIRegistry.perms.registerPermissionDescription(PERM_FOOD, "Respawn food");

        APIRegistry.perms.registerPermission(PERM_DEATHCHEST, DefaultPermissionLevel.ALL,
                "Allow creation of deathchests");
        APIRegistry.perms.registerPermission(PERM_DEATHCHEST_FENCE, DefaultPermissionLevel.ALL,
                "Put the skull on a spike");
        APIRegistry.perms.registerPermission(PERM_DEATHCHEST_BYPASS, DefaultPermissionLevel.OP,
                "Bypass grave protection");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_BLOCK, "", "If set, use this block ID for graves");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_XP, "0.25",
                "Ratio of XP that you want to allow someone to keep in a grave. 1 keeps all XP, 0 disables XP recovery.");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_SAFETIME, "300",
                "Time in seconds a grave is protected. After this time anyone can take all stuff");

        CommandFeSettings.addSetting("Afterlife", "respawn_hp", PERM_HP);
        CommandFeSettings.addSetting("Afterlife", "respawn_food", PERM_FOOD);
        CommandFeSettings.addSetting("Afterlife", "respawn_debuffs", PERM_DEBUFFS);
        CommandFeSettings.addSetting("Grave", "enable", PERM_DEATHCHEST);
        CommandFeSettings.addSetting("Grave", "safetime", PERM_DEATHCHEST_SAFETIME);
        CommandFeSettings.addSetting("Grave", "recoverable_xp", PERM_DEATHCHEST_XP);
        CommandFeSettings.addSetting("Grave", "fence", PERM_DEATHCHEST_FENCE);
        CommandFeSettings.addSetting("Grave", "block", PERM_DEATHCHEST_BLOCK);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e)
    {
        Grave.saveAll();
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        if (e.getPlayer().level.isClientSide)
            return;

        String potionEffects = APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.getPlayer()),
                ModuleAfterlife.PERM_DEBUFFS);
        if (potionEffects != null)
            PlayerUtil.applyPotionEffects(e.getPlayer(), potionEffects);

        Integer respawnHP = ServerUtil.tryParseInt(
                APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.getPlayer()), ModuleAfterlife.PERM_HP));
        if (respawnHP != null)
            e.getPlayer().setHealth(respawnHP);

        Integer respawnFood = ServerUtil.tryParseInt(
                APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.getPlayer()), ModuleAfterlife.PERM_FOOD));
        if (respawnFood != null)
            e.getPlayer().getFoodData().eat(-1 * (20 - respawnFood), 0);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathDropEvent(LivingDropsEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Grave grave = Grave.createGrave((Player) event.getEntity(), event.getDrops());
            if (grave != null)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void exDropEvent(LivingExperienceDropEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            //Test for the event where xp is set to zero because of keep inventory or spectator
            if(event.getOriginalExperience()!=0) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event)
    {
        if (event.phase == Phase.END)
            return;
        if (ServerLifecycleHooks.getCurrentServer().getWorldData().overworldData().getGameTime() % 20 == 0)
        {
            for (Grave grave : new ArrayList<>(Grave.graves.values()))
                grave.updateBlocks();
        }
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getEntity().level.isClientSide)
            return;

        WorldPoint point = new WorldPoint(event.getWorld(), event.getPos());
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        grave.interact((ServerPlayer) event.getPlayer());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void blockBreakEvent(BreakEvent event)
    {
        if (event.getWorld().isClientSide())
            return;

        WorldPoint point = new WorldPoint(event.getPlayer().level, event.getPos());
        Grave grave = Grave.graves.get(point);
        if (grave == null)
        {
            // Check for fence post
            point.setY(point.getY() + 1);
            grave = Grave.graves.get(point);
            if (grave == null || !grave.hasFencePost)
                return;
        }


        if (grave.canOpen(event.getPlayer()))
        {
            grave.remove(true);
        }
        else if (grave.isProtected)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    Translator.translate("You may not defile the grave of a player"));
        } else
        {
            event.setCanceled(true);
        }
    }

}
