package com.ForgeEssentials.WorldBorder;

import java.util.EnumSet;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
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
	private int ticks = 0;
	private int players = 1;
	public static BorderShape shape;
	
	public ModuleWorldBorder()
	{
		if (!ModuleLauncher.borderEnabled)
			return;
		WBenabled = true;
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
	
	public static void setCenter(int rad, int posX, int posZ, BorderShape shapeToSet) 
	{
		if(borderData == null) borderData = new NBTTagCompound();
		
		shape = shapeToSet;
		
		borderData.setBoolean("set", true);
		
		borderData.setInteger("centerX", posX);
		borderData.setInteger("centerZ", posZ);
		borderData.setInteger("rad", rad);
		borderData.setByte("shape", shape.getByte());
		
		if(shape.equals(BorderShape.square))
		{
			borderData.setInteger("minX", posX - rad);
			borderData.setInteger("minZ", posZ - rad);
			
			borderData.setInteger("maxX", posX + rad);
			borderData.setInteger("maxZ", posZ + rad);
		}
		
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
			if(!borderData.getBoolean("set")) return;
		
			if(ticks % players == 0)
			{
				players = FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames().length + 1;
			}
			else
			{
				EntityPlayerMP player = ((EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get((int) (ticks % players - 1)));
				if(shape.equals(BorderShape.round))
				{
					checkPlayerRound(player);
				}
				else if(shape.equals(BorderShape.square))
				{
					checkPlayerSquare(player);
				}
			}
		}
		catch(Exception e) 
		{
			//Failed to tick ??
			OutputHandler.SOP("Failed to tick WorldBorder");
			OutputHandler.SOP("" + e.getLocalizedMessage());
		}
	}

	private static void checkPlayerRound(EntityPlayerMP player)
	{
		if(outDistance(borderData.getInteger("centerX"), borderData.getInteger("centerZ"), borderData.getInteger("rad"), (int) player.posX, (int) player.posZ))
		{
			Vector2 vecp = new Vector2(borderData.getInteger("centerX") - (int) player.posX, borderData.getInteger("centerZ") - (int)player.posZ);
			vecp.normalize();
			vecp.multiply(borderData.getInteger("rad"));
			vecp.multiply(new Vector2(-1,-1));
			vecp.add(0.5);
			
			//vecp.add(new Vector2(borderData.getInteger("centerX"), borderData.getInteger("centerZ")));
			//player.sendChatToPlayer("X:" + vecp.x + " Y:" + vecp.y);
			
			if(player.ridingEntity != null)
			{
				player.ridingEntity.setLocationAndAngles(vecp.x, player.ridingEntity.posY, vecp.y, player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
				player.playerNetServerHandler.setPlayerLocation(vecp.x, player.posY, vecp.y, player.rotationYaw, player.rotationPitch);
			}
			else
			{
				player.playerNetServerHandler.setPlayerLocation(vecp.x, player.posY, vecp.y, player.rotationYaw, player.rotationPitch);
			}
			player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));			
		}
	}
	
	private static void checkPlayerSquare(EntityPlayerMP player) 
	{
		if(player.ridingEntity != null)
		{
			if(player.ridingEntity.posX < borderData.getInteger("minX"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.ridingEntity.setLocationAndAngles(borderData.getInteger("minX"), player.ridingEntity.posY, player.ridingEntity.posZ, player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
				player.playerNetServerHandler.setPlayerLocation(borderData.getInteger("minX"), player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			}
			if(player.ridingEntity.posX > borderData.getInteger("maxX"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.ridingEntity.setLocationAndAngles(borderData.getInteger("maxX"), player.ridingEntity.posY, player.ridingEntity.posZ, player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
			}
			if(player.ridingEntity.posZ < borderData.getInteger("minZ"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.ridingEntity.setLocationAndAngles(player.ridingEntity.posX, player.ridingEntity.posY, borderData.getInteger("minZ"), player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
				player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, borderData.getInteger("minZ"), player.rotationYaw, player.rotationPitch);
			}
			if(player.ridingEntity.posZ > borderData.getInteger("maxZ"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.ridingEntity.setLocationAndAngles(player.ridingEntity.posX, player.ridingEntity.posY, borderData.getInteger("maxZ"), player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
				player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, borderData.getInteger("maxZ"), player.rotationYaw, player.rotationPitch);
			}
		}
		else
		{
			if(player.posX < borderData.getInteger("minX"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.playerNetServerHandler.setPlayerLocation(borderData.getInteger("minX"), player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			}
			if(player.posX > borderData.getInteger("maxX"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.playerNetServerHandler.setPlayerLocation(borderData.getInteger("maxX"), player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			}
			if(player.posZ < borderData.getInteger("minZ"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, borderData.getInteger("minZ"), player.rotationYaw, player.rotationPitch);
			}
			if(player.posZ > borderData.getInteger("maxZ"))
			{
				player.sendChatToPlayer("\u00a7c" + Localization.get(Localization.WB_HITBORDER));
				player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, borderData.getInteger("maxZ"), player.rotationYaw, player.rotationPitch);
			}
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

	@Override
	public void serverStopping(FMLServerStoppingEvent e) 
	{
		
	}
	
	/*
	 * Gets distance for round
	 */
	public static boolean outDistance(int centerX, int centerZ, int rad, int X, int Z)
	{
		int difX = centerX - X;
		int difZ = centerZ - Z;
		
		return (rad * rad) < ((difX * difX) + (difZ * difZ));
	}
	
	/*
	 * Used to get determen shapes
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
	}
}