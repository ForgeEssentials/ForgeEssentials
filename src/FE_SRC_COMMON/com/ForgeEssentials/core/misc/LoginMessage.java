package com.ForgeEssentials.core.misc;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.compat.CompatReiMinimap;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Date;

import cpw.mods.fml.common.FMLCommonHandler;

public class LoginMessage
{
	private static ArrayList<String> messageList = new ArrayList<String> ();
	private static MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	
	public static void loadFile()
	{
		messageList.clear();
		File file = new File(ForgeEssentials.FEDIR, "MOTD.txt");
		if(file.exists())
		{
			try
			{
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);		
				
				while(br.ready())
				{
					String line = br.readLine().trim();
					if(!(line.startsWith("#") || line.isEmpty()))
					{
						messageList.add(line);
					}
				}
				
				br.close();
				fr.close();
			}
			catch (Exception e)
			{
				OutputHandler.info("Error reading the MOTD file.");
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				file.createNewFile();
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("# This file contains the message sent to the player on login.");
				pw.println("# Lines starting with # are not read.");
				pw.println("# There are several codes that can be used to format the text.");
				pw.println("# If you want to use color, use this symbol & (ALT code 21) to indicate a color.");
				pw.println("# Handy link: http://www.minecraftwiki.net/wiki/Formatting_codes");
				pw.println("# Other codes:");
				pw.println("# %players% => Amount of players online.");
				pw.println("# %uptime% => Current server uptime.");
				pw.println("# %uniqueplayers% => Amount of unique player logins.");
				pw.println("# %time% => Local server time. All in one string.");
				pw.println("# %hour% ; %min% ; %sec% => Local server time.");
				pw.println("# %day% ; %month% ; %year% => Local server date.");
				pw.println("# ");
				pw.println("# If you would like more codes, you can make an issue on https://github.com/ForgeEssentials/ForgeEssentialsMain/issues");
				pw.println("");
				pw.println("Welcome to a server running ForgeEssentials.");
				pw.println("There are %players% players online, and we have had %uniqueplayers% unique players.");
				pw.println("Server time: %time%. Uptime: %uptime%");
				
				pw.close();
			}
			catch (Exception e)
			{
				OutputHandler.info("Error reading the MOTD file.");
				e.printStackTrace();
			}
			
		}
	}
	
	public static void sendLoginMessage(ICommandSender sender)
	{
		for(int id = 0; id < messageList.size(); id++)//String line : messageList)
		{
			if(id == 0)
			{
				if(sender instanceof EntityPlayer)
				{
					sender.sendChatToPlayer(CompatReiMinimap.reimotd((EntityPlayer) sender) + Format(messageList.get(id)));	
				}
				else
				{
					sender.sendChatToPlayer(Format(messageList.get(id)));
				}
			}
			else
			{
				sender.sendChatToPlayer(Format(messageList.get(id)));
			}
		}
	}

	private static String Format(String line)
	{
		Date now = new Date();
		return line
				.replaceAll("&", FEChatFormatCodes.CODE.toString())
				.replaceAll("%players%", online() + "")
				.replaceAll("%uptime%", getUptime())
				.replaceAll("%uniqueplayers%",  uniqueplayers())
				.replaceAll("%time%", now.toLocaleString()).replaceAll("%hour%", now.getHours() + "").replaceAll("%min%", now.getMinutes() + "").replaceAll("%sec%", now.getSeconds() + "")
				.replaceAll("%day%", now.getDate() + "").replaceAll("%month%", now.getMonth() + "").replaceAll("%year%", now.getYear() + "");
	}
	
	private static String online()
	{
		int online = 0;
		try { online = server.getCurrentPlayerCount(); } catch (Exception e){}
		return "" + online;
	}
	
	private static String uniqueplayers()
	{
		int logins = 0;
		try { logins = server.getConfigurationManager().getAvailablePlayerDat().length; } catch (Exception e){}
		return "" + logins;
	}
	
	private static String getUptime()
	{
		String uptime = "";
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		int secsIn = (int) (rb.getUptime() / 1000);
		int hours = secsIn / 3600, remainder = secsIn % 3600, minutes = remainder / 60, seconds = remainder % 60;

		uptime += ((hours < 10 ? "0" : "") + hours + " h " + (minutes < 10 ? "0" : "") + minutes + " min " + (seconds < 10 ? "0" : "") + seconds + " sec.");

		return uptime;
	}
}
