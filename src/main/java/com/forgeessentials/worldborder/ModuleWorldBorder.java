package com.forgeessentials.worldborder;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.vector.Vector2;
import com.forgeessentials.worldborder.Effects.IEffect;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Bounces players back into the border if they pass it. No bypass permissions available, If needed, tell me on github.
 *
 * @author Dries007
 */
@FEModule(name = "WorldBorder", parentMod = ForgeEssentials.class)
public class ModuleWorldBorder
{

    public static boolean logToConsole = true;

    private static Map<String, WorldBorder> borderMap = new HashMap<String, WorldBorder>();

    public static Map<Integer, IEffect[]> effectsList = new HashMap<Integer, IEffect[]>();

    public static int overGenerate = 345;

    public static void loadAll()
    {
        borderMap = DataManager.getInstance().loadAll(WorldBorder.class);
    }

    public static void saveAll()
    {
        for (WorldBorder wb : borderMap.values())
        {
            wb.save();
        }

        for (TickTaskFill filler : CommandFiller.map.values())
        {
            filler.stop();
        }
    }

    public static void registerEffects(int dist, IEffect[] effects)
    {
        effectsList.put(dist, effects);
    }

    public static void executeClosestEffects(WorldBorder wb, double dd, EntityPlayerMP player)
    {
        int d = (int) Math.abs(dd);
        if (logToConsole)
        {
            OutputHandler.felog.info(player.getDisplayName() + " passed the worldborder by " + d + " blocks.");
        }
        for (int i = d; i >= 0; i--)
        {
            if (effectsList.isEmpty())
                continue;
            if (effectsList.containsKey(i))
            {
                for (IEffect effect : effectsList.get(i))
                {
                    effect.execute(wb, player);
                }
            }
        }
    }

    public static Vector2 getDirectionVector(Point center, EntityPlayerMP player)
    {
        Vector2 vecp = new Vector2(center.getX() - player.posX, center.getZ() - player.posZ);
        vecp.normalize();
        vecp.multiply(-1);
        return vecp;
    }

    public static int getDistanceRound(Point center, EntityPlayer player)
    {
        double difX = center.getX() - player.posX;
        double difZ = center.getZ() - player.posZ;

        return (int) Math.sqrt((difX * difX) + (difZ * difZ));
    }

    public static int getDistanceRound(int centerX, int centerZ, int x, int z)
    {
        double difX = centerX - x;
        double difZ = centerZ - z;

        return (int) Math.sqrt((difX * difX) + (difZ * difZ));
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        ForgeEssentials.getConfigManager().registerLoader("WorldBorder", new ConfigWorldBorder());

        FECommandManager.registerCommand(new CommandWB());
        FECommandManager.registerCommand(new CommandFiller());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        APIRegistry.perms.registerPermission("fe.worldborder.admin", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission("fe.worldborder.filler", RegisteredPermValue.OP);
    }

    /*
     * Penalty part
     */

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        loadAll();
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        saveAll();
    }

    /*
     * Static Helper Methods
     */

    public static void checkBorder(EntityPlayerMP player, String name)
    {
        WorldBorder border = borderMap.get(name);
        if (border != null)
            border.check(player);
    }

    public static WorldBorder getBorder(String name, boolean createIfNull)
    {
        WorldBorder border = borderMap.get(name);
        if (border == null && createIfNull)
        {
            border = new WorldBorder(name);
            borderMap.put(name, border);
        }
        return border;
    }

    @SubscribeEvent
    public void playerMove(PlayerMoveEvent e)
    {
        checkBorder((EntityPlayerMP) e.entityPlayer, APIRegistry.perms.getServerZone().getWorldZone(e.entityPlayer.worldObj).getName());
        checkBorder((EntityPlayerMP) e.entityPlayer, APIRegistry.perms.getServerZone().getName());
    }

    @SubscribeEvent
    public void worldUnLoad(WorldEvent.Unload e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        Zone zone = APIRegistry.perms.getServerZone().getWorldZone(e.world);
        borderMap.remove(zone.getName());
    }
}
