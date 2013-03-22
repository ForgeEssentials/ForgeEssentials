package com.ForgeEssentials.WorldBorder;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.WorldBorder.Effects.IEffect;
import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.events.PlayerMoveEvent;
import com.ForgeEssentials.util.vector.Vector2;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Bounces players back into the border if they pass it. No bypass permissions
 * available, If needed, tell me on github.
 * @author Dries007
 */
@FEModule(name = "WorldBorder", parentMod = ForgeEssentials.class, configClass = ConfigWorldBorder.class)
public class ModuleWorldBorder
{
	public static boolean						WBenabled		= false;
	public static boolean						logToConsole	= true;

	@FEModule.Config
	public static ConfigWorldBorder				config;

	public static HashMap<String, WorldBorder>	borderMap		= new HashMap<String, WorldBorder>();

	public static HashMap<Integer, IEffect[]>	effectsList		= new HashMap<Integer, IEffect[]>();
	public static int							overGenerate	= 345;

	public static boolean						globalOverride	= true;
	static final ClassContainer					con				= new ClassContainer(WorldBorder.class);

	public ModuleWorldBorder()
	{
		WBenabled = true;
	}

	@PermRegister
	public static void registerPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.WorldBorder.admin", RegGroup.OWNERS);
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
		loadAll();

		Zone zone = ZoneManager.getGLOBAL();
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
		if (WBenabled)
		{
			Zone zone = ZoneManager.getWorldZone(e.entityPlayer.worldObj);
			WorldBorder border = borderMap.get(zone.getZoneName());
			border.check((EntityPlayerMP) e.entityPlayer);
			if (globalOverride)
			{
				borderMap.get(ZoneManager.getGLOBAL().getZoneName()).check((EntityPlayerMP) e.entityPlayer);
			}
		}
	}

	@ForgeSubscribe
	public void worldLoad(WorldEvent.Load e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		Zone zone = ZoneManager.getWorldZone(e.world);
		if (borderMap.containsKey(zone.getZoneName()))
		{
			DataStorageManager.getReccomendedDriver().saveObject(con, borderMap.get(zone.getZoneName()));
		}
		else
		{
			WorldBorder wb = (WorldBorder) DataStorageManager.getReccomendedDriver().loadObject(con, zone.getZoneName());
			if (wb != null)
				borderMap.put(zone.getZoneName(), wb);
			else
				borderMap.put(zone.getZoneName(), new WorldBorder(zone));
			DataStorageManager.getReccomendedDriver().saveObject(con, borderMap.get(zone.getZoneName()));
		}
	}

	@ForgeSubscribe
	public void worldUnLoad(WorldEvent.Unload e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		Zone zone = ZoneManager.getWorldZone(e.world);
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
			OutputHandler.info(player.username + " passed the worldborder by " + d + " blocks.");
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

		return (int) Math.sqrt(Math.pow(difX, 2) + Math.pow(difZ, 2));
	}

	public static int getDistanceRound(int centerX, int centerZ, int x, int z)
	{
		double difX = centerX - x;
		double difZ = centerZ - z;

		return (int) Math.sqrt(Math.pow(difX, 2) + Math.pow(difZ, 2));
	}
}
