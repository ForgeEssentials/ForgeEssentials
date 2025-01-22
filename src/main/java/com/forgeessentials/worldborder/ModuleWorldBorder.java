package com.forgeessentials.worldborder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.effect.EffectBlock;

@FEModule(name = "WorldBorder", parentMod = ForgeEssentials.class)
public class ModuleWorldBorder extends ServerEventHandler
{

    public static final String PERM = "fe.worldborder";
    public static final String PERM_ADMIN = PERM + ".admin";
    public static final String PERM_BYPASS = PERM + ".bypass";

    public static final int DEFAULT_SIZE = 32768;

    private static ModuleWorldBorder instance;

    private Map<WorldServer, WorldBorder> borders = new HashMap<>();

    public ModuleWorldBorder()
    {
        super();
        instance = this;
        DataManager.addDataType(new WorldBorderEffectType());
    }

    public static ModuleWorldBorder getInstance()
    {
        return instance;
    }

    @SubscribeEvent
    public void moduleInitEvent(FEModuleInitEvent event)
    {
        FECommandManager.registerCommand(new CommandWorldBorder());
    }

    @SubscribeEvent
    public void serverStartingEvent(FEModuleServerInitEvent event)
    {
        APIRegistry.perms.registerPermissionDescription(PERM, "Worldborder permissions");
        APIRegistry.perms.registerPermission(PERM_BYPASS, PermissionLevel.FALSE, "Ignore worldborders if granted");
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event)
    {
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
            return;
        borders.put((WorldServer) event.world, WorldBorder.load(event.world));
        getBorder(event.world);
    }

    @SubscribeEvent
    public void worldUnLoadEvent(WorldEvent.Unload event)
    {
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
            return;
        borders.remove(event.world);
    }

    @SubscribeEvent
    public void playerMoveEvent(PlayerMoveEvent event)
    {
        EntityPlayerMP player = event.getPlayer();
        WorldBorder border = getBorder(event.after.getWorld());
        if (border != null && border.isEnabled())
        {
            double minBorderDistance = Double.MAX_VALUE;
            switch (border.getShape())
            {
            case BOX:
            {
                Point p1 = border.getArea().getLowPoint();
                Point p2 = border.getArea().getHighPoint();
                minBorderDistance = Math.min(minBorderDistance, event.after.getX() - p1.getX());
                minBorderDistance = Math.min(minBorderDistance, event.after.getZ() - p1.getZ());
                minBorderDistance = Math.min(minBorderDistance, p2.getX() - event.after.getX());
                minBorderDistance = Math.min(minBorderDistance, p2.getZ() - event.after.getZ());
                break;
            }
            case ELLIPSOID:
            case CYLINDER:
            {
                Point delta = event.after.toWorldPoint();
                delta.setY(border.getCenter().getY());
                delta.subtract(border.getCenter());
                int x0 = delta.getX();
                int z0 = delta.getZ();
                if (x0 != 0 || z0 != 0)
                {
                    int a = border.getSize().getX();
                    int b = border.getSize().getZ();
                    double iM = a * b / Math.sqrt(a * a * z0 * z0 + b * b * x0 * x0);
                    Point p1 = new Point(iM * x0, delta.getY(), iM * z0);

                    minBorderDistance = Math.min(minBorderDistance, p1.length() - delta.length());
                }
                break;
            }
            default:
                LoggingHandler.felog.error("Unsupported world border shape. Disabling worldborder on world " + event.after.getWorld().provider.getDimensionId());
                borders.remove(event.after.getWorld());
                return;
            }

            // Check which effects are active
            Set<WorldBorderEffect> newActiveEffects = new HashSet<>();
            if (!PermissionManager.checkPermission(player, PERM_BYPASS))
            {
                if (minBorderDistance <= 0)
                    new EffectBlock().playerMove(border, event);

                for (WorldBorderEffect effect : border.getEffects())
                    if (minBorderDistance <= effect.getTriggerDistance())
                        newActiveEffects.add(effect);
            }

            // Deactivate old effects and update current ones
            Set<WorldBorderEffect> activeEffects = border.getOrCreateActiveEffects(player);
            for (Iterator<WorldBorderEffect> iterator = activeEffects.iterator(); iterator.hasNext();)
            {
                WorldBorderEffect effect = iterator.next();
                if (!newActiveEffects.contains(effect))
                {
                    // Remove effect that got out of range
                    effect.deactivate(border, player);
                    iterator.remove();
                }
                else
                {
                    // Update effect
                    effect.playerMove(border, event);
                }
            }

            // Add new effects
            for (WorldBorderEffect effect : newActiveEffects)
            {
                activeEffects.add(effect);
                effect.activate(border, player);
                effect.playerMove(border, event);
            }
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event)
    {
        // Tick effects
        for (EntityPlayerMP player : ServerUtil.getPlayerList())
        {
            WorldBorder border = getBorder(player.worldObj);
            if (border != null && border.isEnabled())
            {
                Set<WorldBorderEffect> effects = border.getActiveEffects(player);
                if (effects != null)
                {
                    for (WorldBorderEffect effect : effects)
                    {
                        effect.tick(border, player);
                    }
                }
            }
        }
    }

    public WorldBorder getBorder(World world)
    {
        WorldBorder border = borders.get(world);
        if (border == null)
        {
            border = new WorldBorder(new Point(0, 0, 0), DEFAULT_SIZE, DEFAULT_SIZE, world.provider.getDimensionId());
            borders.put((WorldServer) world, border);
        }
        return border;
    }

}
