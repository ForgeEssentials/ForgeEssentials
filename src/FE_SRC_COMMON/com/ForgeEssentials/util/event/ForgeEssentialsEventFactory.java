package com.ForgeEssentials.util.event;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ForgeEssentialsEventFactory implements ITickHandler, IPlayerTracker
{
	private HashMap<String, WarpPoint> befores;
	
	public ForgeEssentialsEventFactory()
	{
		befores = new HashMap<String, WarpPoint>();
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		EntityPlayerMP player = (EntityPlayerMP) tickData[0];
		befores.put(player.username, new WarpPoint(player));
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		EntityPlayerMP player = (EntityPlayerMP) tickData[0];
		WarpPoint after = new WarpPoint(player);
		WarpPoint before = befores.get(player.username);
		
		PlayerMoveEvent event = new PlayerMoveEvent(player, before, after);
		MinecraftForge.EVENT_BUS.post(event);
		
		if (event.isCanceled())
			FunctionHelper.setPlayer(player, before);
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		befores.remove(player.username);
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return "PlayerMoveHandler";
	}

	@Override
	public void onPlayerLogin(EntityPlayer player){}
	@Override
	public void onPlayerChangedDimension(EntityPlayer player){}
	@Override
	public void onPlayerRespawn(EntityPlayer player){}
}
