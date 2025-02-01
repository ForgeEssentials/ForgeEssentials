package com.forgeessentials.afterlife;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

/**
 * Module to handle death-chest and respawn debuffs.
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class)
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
    public void load(FEModuleInitEvent e)
    {
        TileEntity.addMapping(TileEntitySkullGrave.class, "FESkull");
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        Grave.loadAll();

        APIRegistry.perms.registerPermissionDescription(PERM, "Permissions for afterlife configuration");
        APIRegistry.perms.registerPermissionDescription(PERM_DEBUFFS, "Potion effects to apply on respawn (comma separated list of id:duration:amplifier)");
        APIRegistry.perms.registerPermissionDescription(PERM_HP, "Respawn HP");
        APIRegistry.perms.registerPermissionDescription(PERM_FOOD, "Respawn food");

        APIRegistry.perms.registerPermission(PERM_DEATHCHEST, PermissionLevel.TRUE, "Allow creation of deathchests");
        APIRegistry.perms.registerPermission(PERM_DEATHCHEST_FENCE, PermissionLevel.TRUE, "Put the skull on a spike");
        APIRegistry.perms.registerPermission(PERM_DEATHCHEST_BYPASS, PermissionLevel.OP, "Bypass grave protection");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_BLOCK, "", "If set, use this block ID for graves");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_XP, "0.25",
                "Ratio of XP that you want to allow someone to keep in a grave. 1 keeps all XP, 0 disables XP recovery.");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_SAFETIME, "300",
                "Time in seconds a grave is protected. After this time anyone can take all stuff");

        CommandFeSettings.addAlias("Afterlife", "respawn_hp", PERM_HP);
        CommandFeSettings.addAlias("Afterlife", "respawn_food", PERM_FOOD);
        CommandFeSettings.addAlias("Afterlife", "respawn_debuffs", PERM_DEBUFFS);
        CommandFeSettings.addAlias("Grave", "enable", PERM_DEATHCHEST);
        CommandFeSettings.addAlias("Grave", "safetime", PERM_DEATHCHEST_SAFETIME);
        CommandFeSettings.addAlias("Grave", "recoverable_xp", PERM_DEATHCHEST_XP);
        CommandFeSettings.addAlias("Grave", "fence", PERM_DEATHCHEST_FENCE);
        CommandFeSettings.addAlias("Grave", "block", PERM_DEATHCHEST_BLOCK);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        Grave.saveAll();
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        if (e.player.worldObj.isRemote)
            return;

        String potionEffects = APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_DEBUFFS);
        if (potionEffects != null)
            PlayerUtil.applyPotionEffects(e.player, potionEffects);

        Integer respawnHP = ServerUtil.tryParseInt(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_HP));
        if (respawnHP != null)
            e.player.setHealth(respawnHP);

        Integer respawnFood = ServerUtil.tryParseInt(APIRegistry.perms.getUserPermissionProperty(UserIdent.get(e.player), ModuleAfterlife.PERM_FOOD));
        if (respawnFood != null)
            e.player.getFoodStats().addStats(-1 * (20 - respawnFood), 0);
    }

    @SubscribeEvent
    public void playerDeathDropEvent(PlayerDropsEvent event)
    {
        Grave grave = Grave.createGrave(event.entityPlayer, event.drops);
        if (grave != null)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event)
    {
        if (event.phase == Phase.END)
            return;
        if (MinecraftServer.getServer().getEntityWorld().getWorldInfo().getWorldTotalTime() % 20 == 0)
        {
            for (Grave grave : new ArrayList<Grave>(Grave.graves.values()))
                grave.updateBlocks();
        }
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (event.entity.worldObj.isRemote)
            return;
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK)
            return;

        WorldPoint point = new WorldPoint(event.entity.worldObj, event.pos);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        grave.interact((EntityPlayerMP) event.entityPlayer);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void blockBreakEvent(BreakEvent event)
    {
        if (event.world.isRemote)
            return;

        WorldPoint point = new WorldPoint(event.world, event.pos);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
        {
            // Check for fence post
            point.setY(event.pos.getY() + 1);
            grave = Grave.graves.get(point);
            if (grave == null || !grave.hasFencePost)
                return;
        }

        if (grave.isProtected)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer(), Translator.translate("You may not defile the grave of a player"));
            return;
        }
        if (grave.canOpen(event.getPlayer()))
        {
            grave.remove(true);
        }
        else
        {
            event.setCanceled(true);
        }
    }

}
