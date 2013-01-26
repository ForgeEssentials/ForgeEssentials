package com.ForgeEssentials.WorldBorder;

import com.ForgeEssentials.WorldBorder.Effects.IEffect;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.vector.Vector2;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.event.ForgeSubscribe;

import java.util.EnumSet;
import java.util.HashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Bounces players back into the border if they pass it. No bypass permissions available, If needed, tell me on github.
 * 
 * @author Dries007
 * 
 */
@FEModule(name = "WorldBorder", parentMod = ForgeEssentials.class, configClass = ConfigWorldBorder.class)
public class ModuleWorldBorder implements IScheduledTickHandler
{
	public static boolean						WBenabled		= false;
	public static boolean						logToConsole	= true;

	@Config
	public static ConfigWorldBorder				config;

	public static BorderShape					shape;
	public static HashMap<Integer, IEffect[]>	effectsList		= new HashMap();
	public static int							overGenerate	= 345;
	public static boolean						set				= false;

	public static int							X;
	public static int							Z;
	public static int							rad;

	public static int							maxX;
	public static int							maxZ;
	public static int							minX;
	public static int							minZ;

	private int									ticks			= 0;
	private int									players			= 1;

	public ModuleWorldBorder()
	{
		WBenabled = true;
		OutputHandler.SOP("WorldBorder module is enabled. Loading...");
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandWB());
		TickRegistry.registerScheduledTickHandler(this, Side.SERVER);
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		// nothing here atm.
	}

	/*
	 * Tickhandler part
	 */

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		try
		{
			if (ticks >= Integer.MAX_VALUE)
			{
				ticks = 1;
			}
			ticks++;
			if (!WBenabled)
			{
				return;
			}
			if (!set)
			{
				return;
			}

			if (ticks % players == 0)
			{
				players = FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames().length + 1;
			}
			else
			{
				EntityPlayerMP player = ((EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList
						.get(ticks % players - 1));
				shape.doCheck(player);
			}
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Failed to tick WorldBorder");
			OutputHandler.SOP("" + e.getLocalizedMessage());
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "WorldBorder";
	}

	@Override
	public int nextTickSpacing()
	{
		if (players < 10)
		{
			return 10;
		}
		else if (players < 20)
		{
			return 5;
		}
		else
		{
			return 0;
		}
	}

	/*
	 * Used to get determen shapes & execute the actual check.
	 */

	public enum BorderShape
	{
		round, square;

		public byte getByte()
		{
			if (equals(round))
			{
				return 1;
			}
			if (equals(square))
			{
				return 2;
			}
			return 0;
		}

		public static BorderShape getFromByte(byte byte1)
		{
			if (byte1 == 1)
			{
				return BorderShape.round;
			}
			else if (byte1 == 2)
			{
				return BorderShape.square;
			}
			return null;
		}

		public void doCheck(EntityPlayerMP player)
		{
			if (equals(round))
			{
				int dist = (int) getDistanceRound(X, Z, (int) player.posX, (int) player.posZ);
				if (dist > rad)
				{
					executeClosestEffects(dist - ModuleWorldBorder.rad, player);
				}
			}
			if (equals(square))
			{
				if (player.posX < minX)
				{
					executeClosestEffects((int) player.posX - minX, player);
				}
				if (player.posX > maxX)
				{
					executeClosestEffects((int) player.posX - maxX, player);
				}
				if (player.posZ < minZ)
				{
					executeClosestEffects((int) player.posZ - minZ, player);
				}
				if (player.posZ > maxZ)
				{
					executeClosestEffects((int) player.posZ - maxZ, player);
				}
			}
		}
	}

	/*
	 * Penalty part
	 */

	public static void registerEffects(int dist, IEffect[] effects)
	{
		effectsList.put(dist, effects);
	}

	public static void executeClosestEffects(int dist, EntityPlayerMP player)
	{
		dist = Math.abs(dist);
		log(player, dist);
		for (int i = dist; i >= 0; i--)
		{
			if (effectsList.containsKey(i))
			{
				for (IEffect effect : effectsList.get(i))
				{
					effect.execute(player);
				}
			}
		}
	}

	/*
	 * Static Helper Methods
	 */

	public static double getDistanceRound(int centerX, int centerZ, int X, int Z)
	{
		int difX = centerX - X;
		int difZ = centerZ - Z;

		return Math.sqrt(((difX * difX) + (difZ * difZ)));
	}

	public static Vector2 getDirectionVector(EntityPlayerMP player)
	{
		Vector2 vecp = new Vector2(X - player.posX, Z - player.posZ);
		vecp.normalize();
		vecp.multiply(-1);
		return vecp;
	}

	public static void log(EntityPlayerMP player, int dist)
	{
		if (logToConsole)
		{
			OutputHandler.SOP(player.username + " passed the worldborder by " + dist + " blocks.");
		}
	}

	public static void setCenter(int rad, int posX, int posZ, BorderShape shapeToSet, boolean set)
	{
		shape = shapeToSet;
		ModuleWorldBorder.set = set;

		X = posX;
		Z = posZ;
		ModuleWorldBorder.rad = rad;

		maxX = posX + rad;
		maxZ = posZ + rad;

		minX = posX - rad;
		minZ = posZ - rad;

		config.forceSave();
	}
}