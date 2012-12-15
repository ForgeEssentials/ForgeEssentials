package com.ForgeEssentials.playerLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

public class EventLogger implements IPlayerTracker 
{
	public LogLoop logLoop;
	
	public void start() 
	{
		logLoop = new LogLoop();
		new Thread(logLoop, "MySQL Connection Thread - PlayerLogger").start();
		
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
	}
	
	/*
	 * Logging part
	 */
	
	public static boolean logPlayerLogin = true;
	public static boolean logPlayerLogout = true;
	public static boolean logPlayerChangedDimension = true;
	public static boolean logPlayerRespawn = true;
	public static boolean logCommands = true;
	public static boolean logItemUsage = true;
	public static boolean logBlockChanges = true;
	
	@Override
	public void onPlayerLogin(EntityPlayer player) 
	{
		if(logPlayerLogin) logLoop.buffer.add(new logEntry(player.username, LogCatagory.Login, "", playerLoc(player)));
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) 
	{
		if(logPlayerLogout) logLoop.buffer.add(new logEntry(player.username, LogCatagory.Logout, "", playerLoc(player)));
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) 
	{
		if(logPlayerChangedDimension) logLoop.buffer.add(new logEntry(player.username, LogCatagory.ChangedDimension, "", playerLoc(player)));
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) 
	{
		if(logPlayerRespawn) logLoop.buffer.add(new logEntry(player.username, LogCatagory.Respawn, "", playerLoc(player)));
	}
	
	@ForgeSubscribe
	public void command(CommandEvent e)
	{
		if(logCommands && e.sender instanceof EntityPlayer) logLoop.buffer.add(new logEntry(e.sender.getCommandSenderName(), LogCatagory.Command, getCommand(e), playerLoc((EntityPlayer) e.sender)));
		if(logCommands && !(e.sender instanceof EntityPlayer)) logLoop.buffer.add(new logEntry(e.sender.getCommandSenderName(), LogCatagory.Command, getCommand(e)));
	}
	
	@ForgeSubscribe
	public void playerInteractin(PlayerInteractEvent e)
	{
		if(e.action == e.action.LEFT_CLICK_BLOCK)
		{
			
		}
	}
	

	/*
	 * Needed background stuff
	 */
	
	public WorldPoint playerLoc(EntityPlayer player)
	{
		return new WorldPoint(player.dimension, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	public String getCommand(CommandEvent e)
	{
		String command = "/" + e.command.getCommandName();
		for(String str : e.parameters)
		{
			command = command + " " + str;
		}
		return command;
	}
}
