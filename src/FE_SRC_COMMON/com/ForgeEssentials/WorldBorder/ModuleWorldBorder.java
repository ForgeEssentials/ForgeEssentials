package com.ForgeEssentials.WorldBorder;

import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.WorldBorder.Effects.IEffect;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.vector.Vector2;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * Bounces players back into the border if they pass it.
 * No bypass permissions available, If needed, tell me on github.
 * 
 * @author Dries007
 *
 */

public class ModuleWorldBorder implements IFEModule, IScheduledTickHandler
{
	public static boolean WBenabled = false;
	public static NBTTagCompound borderData;
	public static boolean logToConsole = true;
	public static ConfigWorldBorder config;
	public static BorderShape shape;
	public static HashMap<Integer, IEffect[]> effectsList = new HashMap();
	public static int overGenerate = 345;
	
	private int ticks = 0;
	private int players = 1;
	
	public ModuleWorldBorder()
	{
		if (!ModuleLauncher.borderEnabled)
			return;
		WBenabled = true;
		OutputHandler.SOP("WorldBorder module is enabled. Loading...");
		config = new ConfigWorldBorder();
	}

	/*
	 * Module part
	 */
	
	@Override
	public void preLoad(FMLPreInitializationEvent e){}

	@Override
	public void load(FMLInitializationEvent e){}

	@Override
	public void postLoad(FMLPostInitializationEvent e){}

	@Override
	public void serverStopping(FMLServerStoppingEvent e) {}
	
	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandWB());
		TickRegistry.registerScheduledTickHandler(this, Side.SERVER);
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		OutputHandler.SOP("WorldBorder data loaded.");
		DataStorage.load();
		borderData = DataStorage.getData("WorldBorder");
		
		shape =	BorderShape.getFromByte(borderData.getByte("shape"));
	}

	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerPermissionDefault("ForgeEssentials.worldborder", false);
		event.registerPermissionDefault("ForgeEssentials.worldborder.admin", false);
	}

	/*
	 * Tickhandler part
	 */
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		try
		{
			if(this.ticks >= Integer.MAX_VALUE) this.ticks = 1;
			this.ticks ++;    	
			if(!WBenabled) return;
			if(!borderData.getBoolean("set")) return;
		
			if(ticks % players == 0)
			{
				players = FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames().length + 1;
			}
			else
			{
				EntityPlayerMP player = ((EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get((int) (ticks % players - 1)));
				shape.doCheck(player);
			}
		}
		catch(Exception e) 
		{
			OutputHandler.SOP("Failed to tick WorldBorder");
			OutputHandler.SOP("" + e.getLocalizedMessage());
		}
	}


	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

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
		if(players < 10)
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
			if(this.equals(round))
			{
				return 1;
			}
			if(this.equals(square))
			{
				return 2;
			}
			return 0;
		}

		public static BorderShape getFromByte(byte byte1) 
		{
			if(byte1 == 1)
			{
				return BorderShape.round;
			}
			else if(byte1 == 2)
			{
				return BorderShape.square;
			}
			return null;
		}
		
		public void doCheck(EntityPlayerMP player)
		{
			if(this.equals(round))
			{
				int dist = (int) getDistanceRound(borderData.getInteger("centerX"), borderData.getInteger("centerZ"), (int) player.posX, (int) player.posZ);
				if(dist > borderData.getInteger("rad"))
				{
					executeClosestEffects(dist - ModuleWorldBorder.borderData.getInteger("rad"), player);
				}
			}
			if(this.equals(square))
			{
				if(player.posX < borderData.getInteger("minX"))
				{
					executeClosestEffects((int) player.posX - borderData.getInteger("minX"), player);
				}
				if(player.posX > borderData.getInteger("maxX"))
				{
					executeClosestEffects((int) player.posX - borderData.getInteger("maxX"), player);
				}
				if(player.posZ < borderData.getInteger("minZ"))
				{
					executeClosestEffects((int) player.posZ - borderData.getInteger("minZ"), player);
				}
				if(player.posZ > borderData.getInteger("maxZ"))
				{
					executeClosestEffects((int) player.posZ - borderData.getInteger("maxZ"), player);
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
		for(int i = dist; i >= 0; i--)
		{
			if(effectsList.containsKey(i))
			{
				for(IEffect effect : effectsList.get(i))
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
		Vector2 vecp = new Vector2(borderData.getInteger("centerX") - player.posX, borderData.getInteger("centerZ") - player.posZ);
		vecp.normalize();
		vecp.multiply(-1);
		return vecp;
	}
	
	public static void log(EntityPlayerMP player, int dist)
	{
		if(logToConsole)
			OutputHandler.SOP(player.username + " passed the worldborder by " + dist + " blocks.");
	}
	
	public static void setCenter(int rad, int posX, int posZ, BorderShape shapeToSet) 
	{
		if(borderData == null) borderData = new NBTTagCompound();
		
		shape = shapeToSet;
		
		borderData.setBoolean("set", true);
		
		borderData.setInteger("centerX", posX);
		borderData.setInteger("centerZ", posZ);
		borderData.setInteger("rad", rad);
		borderData.setByte("shape", shape.getByte());
		
		borderData.setInteger("minX", posX - rad);
		borderData.setInteger("minZ", posZ - rad);
			
		borderData.setInteger("maxX", posX + rad);
		borderData.setInteger("maxZ", posZ + rad);
		
		
		DataStorage.setData("WorldBorder", borderData);
	}
}