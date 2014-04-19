package com.forgeessentials.worldborder;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import com.forgeessentials.util.vector.Vector2;
import com.forgeessentials.worldborder.Effects.IEffect;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Bounces players back into the border if they pass it. No bypass permissions
 * available, If needed, tell me on github.
 * @author Dries007
 */
@FEModule(name = "WorldBorder", parentMod = ForgeEssentials.class, configClass = ConfigWorldBorder.class)
public class ModuleWorldBorder
{
	public static boolean						logToConsole	= true;

	@FEModule.Config
	public static ConfigWorldBorder				config;

	public static HashMap<String, WorldBorder>	borderMap		= new HashMap<String, WorldBorder>();

	public static HashMap<Integer, IEffect[]>	effectsList		= new HashMap<Integer, IEffect[]>();
	public static int							overGenerate	= 345;

	static final ClassContainer					con				= new ClassContainer(WorldBorder.class);

	@PermRegister
	public static void registerPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("fe.worldborder.admin", RegGroup.OWNERS);
		event.registerPermissionLevel("fe.worldborder.filler", RegGroup.OWNERS);
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandWB());
		e.registerServerCommand(new CommandFiller());
	}
	
	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
	    loadAll();

        Zone zone = APIRegistry.zones.getGLOBAL();
        if (!borderMap.containsKey(zone.getZoneName()))
        {
            borderMap.put(zone.getZoneName(), new WorldBorder(zone));
        }
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		saveAll();
	}

	@ForgeSubscribe
	public void playerMove(PlayerMoveEvent e)
	{
		Zone zone = APIRegistry.zones.getWorldZone(e.entityPlayer.worldObj);
		WorldBorder border = borderMap.get(zone.getZoneName());
		border.check((EntityPlayerMP) e.entityPlayer);
		borderMap.get(APIRegistry.zones.getGLOBAL().getZoneName()).check((EntityPlayerMP) e.entityPlayer);
	}

	@ForgeSubscribe
	public void worldLoad(WorldEvent.Load e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		Zone zone = APIRegistry.zones.getWorldZone(e.world);
		if (!borderMap.containsKey(zone.getZoneName()))
		{
			WorldBorder wb = (WorldBorder) DataStorageManager.getReccomendedDriver().loadObject(con, zone.getZoneName());
			if (wb != null)
			{
				borderMap.put(zone.getZoneName(), wb);
			}
			else
			{
				borderMap.put(zone.getZoneName(), new WorldBorder(zone));
			}
		}
	}

	@ForgeSubscribe
	public void worldUnLoad(WorldEvent.Unload e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		Zone zone = APIRegistry.zones.getWorldZone(e.world);
		borderMap.remove(zone.getZoneName());
	}

	public static void loadAll()
	{
		for (Object obj : DataStorageManager.getReccomendedDriver().loadAllObjects(con))
		{
			WorldBorder wb = (WorldBorder) obj;
			borderMap.put(wb.zone, wb);
		}
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

	/*
	 * Penalty part
	 */

	public static void registerEffects(int dist, IEffect[] effects)
	{
		effectsList.put(dist, effects);
	}

	public static void executeClosestEffects(WorldBorder wb, double dd, EntityPlayerMP player)
	{
		int d = (int) Math.abs(dd);
		if (logToConsole)
		{
			OutputHandler.felog.info(player.username + " passed the worldborder by " + d + " blocks.");
		}
		for (int i = d; i >= 0; i--)
		{
			if (effectsList.containsKey(i))
			{
				for (IEffect effect : effectsList.get(i))
				{
					effect.execute(wb, player);
				}
			}
		}
	}

	/*
	 * Static Helper Methods
	 */

	public static Vector2 getDirectionVector(Point center, EntityPlayerMP player)
	{
		Vector2 vecp = new Vector2(center.x - player.posX, center.z - player.posZ);
		vecp.normalize();
		vecp.multiply(-1);
		return vecp;
	}

	public static int getDistanceRound(Point center, EntityPlayer player)
	{
		double difX = center.x - player.posX;
		double difZ = center.z - player.posZ;

		return (int) Math.sqrt((difX * difX) + (difZ * difZ));
	}

	public static int getDistanceRound(int centerX, int centerZ, int x, int z)
	{
		double difX = centerX - x;
		double difZ = centerZ - z;

		return (int) Math.sqrt((difX * difX) + (difZ * difZ));
	}
}
