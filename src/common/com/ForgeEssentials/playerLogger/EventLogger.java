package com.ForgeEssentials.playerLogger;

import java.util.HashSet;

import com.ForgeEssentials.util.AreaSelector.Point;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
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
		if(logCommands) logLoop.buffer.add(new logEntry(e.sender.getCommandSenderName(), LogCatagory.Command, getCommand(e)));
	}

	/*
	 * Needed background stuff
	 */
	
	public Point playerLoc(EntityPlayer player)
	{
		return new Point((int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	public String getCommand(CommandEvent e)
	{
		String command = e.command.getCommandName();
		for(String str : e.parameters)
		{
			command = command + " " + str;
		}
		return command;
	}
}
