package com.ForgeEssentials.WorldBorder;

import java.util.EnumSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.permissions.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.TickRegistry;

public class ModuleWorldBorder implements IFEModule, IScheduledTickHandler
{
	public static boolean WBenabled = false;
	public static NBTTagCompound borderData;
	private int ticks = 0;
	private int players = 1;
	
	public ModuleWorldBorder()
	{
		if (!ModuleLauncher.borderEnabled)
			return;
	}

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("WorldBorder module is enabled. Loading...");
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandWB());
		TickRegistry.registerScheduledTickHandler(this, Side.SERVER);
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		borderData = DataStorage.getData("WorldBorder");
	}

	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerGlobalPermission("ForgeEssentials.worldborder", false);
		event.registerGlobalPermission("ForgeEssentials.worldborder.bypass", false);
		event.registerGlobalPermission("ForgeEssentials.worldborder.admin", false);
	}
	
	public static void setCenter(int rad, int posX, int posZ) 
	{
		if(borderData == null) borderData = new NBTTagCompound();
		borderData.setInteger("minX", posX - rad);
		borderData.setInteger("maxX", posX + rad);
		
		borderData.setInteger("minZ", posZ - rad);
		borderData.setInteger("maxZ", posZ + rad);
		
		DataStorage.setData("WorldBorder", borderData);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		try
		{
			if(this.ticks >= Integer.MAX_VALUE) this.ticks = 1;
			this.ticks ++;    	
			if(!WBenabled) return;
		
			if(ticks % players == 0)
			{
				players = FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames().length + 1;
			}
			else
			{
				EntityPlayerMP player = ((EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get((int) (ticks % players - 1)));
				checkPlayer(player);
			}
		}
		catch(Exception e) {}
	}

	private void checkPlayer(EntityPlayerMP player) 
	{
		if(player.posX < borderData.getInteger("minX"))
		{
			player.sendChatToPlayer("\u00a7c" + Localization.get("worldborder.message"));
			player.playerNetServerHandler.setPlayerLocation(borderData.getInteger("minX"), player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		}
		if(player.posX > borderData.getInteger("maxX"))
		{
			player.sendChatToPlayer("\u00a7c" + Localization.get("worldborder.message"));
			player.playerNetServerHandler.setPlayerLocation(borderData.getInteger("maxX"), player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		}
		if(player.posZ < borderData.getInteger("minZ"))
		{
			player.sendChatToPlayer("\u00a7c" + Localization.get("worldborder.message"));
			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, borderData.getInteger("minZ"), player.rotationYaw, player.rotationPitch);
		}
		if(player.posZ > borderData.getInteger("maxZ"))
		{
			player.sendChatToPlayer("\u00a7c" + Localization.get("worldborder.message"));
			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, borderData.getInteger("maxZ"), player.rotationYaw, player.rotationPitch);
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
		if(players < 50)
		{
			return 20;
		}
		else if (players < 100)
		{
			return 10;
		}
		else if (players < 200)
		{
			return 5;
		}
		else
		{
			return 0;
		}
	}
	
}