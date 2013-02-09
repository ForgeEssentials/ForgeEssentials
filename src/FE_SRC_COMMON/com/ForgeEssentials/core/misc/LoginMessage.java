package com.ForgeEssentials.core.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Date;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.compat.CompatReiMinimap;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
//import com.ForgeEssentials.economy.Wallet;

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
				pw.println("# %playername% => The name of the player the message is send to");
				//pw.println("# %balance% => Prints the users balance (economy)");
				pw.println("# %players% => Amount of players online.");
				pw.println("# %uptime% => Current server uptime.");
				pw.println("# %uniqueplayers% => Amount of unique player logins.");
				pw.println("# %time% => Local server time. All in one string.");
				pw.println("# %hour% ; %min% ; %sec% => Local server time.");
				pw.println("# %day% ; %month% ; %year% => Local server date.");
				pw.println("# ");
				pw.println("# If you would like more codes, you can make an issue on https://github.com/ForgeEssentials/ForgeEssentialsMain/issues");
				pw.println("");
				pw.println("Welcome %playername%, to a server running ForgeEssentials."); //personal welcome is nicer :)
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
					sender.sendChatToPlayer(CompatReiMinimap.reimotd((EntityPlayer) sender) + Format(messageList.get(id),sender.getCommandSenderName()));//only sending the name of the player, going to fix this later so I'm sending everything
				}
				else
				{
					sender.sendChatToPlayer(Format(messageList.get(id),sender.getCommandSenderName()));
				}
			}
			else
			{
				sender.sendChatToPlayer(Format(messageList.get(id),sender.getCommandSenderName()));
			}
		}
	}

	/**
	 * Formats the chat, replacing given strings by their values
	 * 
	 * @param String to parse
	 *            the amount to add to the wallet
	 *
	 */
	
	private static String Format(String line,String playerName) //replaces the variables with data
	{
		EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(playerName);
		Date now = new Date();
		//int wallet = Wallet.getWallet(player); //needed to return wallet info
		return line
				.replaceAll("&", FEChatFormatCodes.CODE.toString()) //color codes
				.replaceAll("%playername%",player.username)
				.replaceAll("%players%", online()) //players
				//.replaceAll("%balance%",wallet + " " + Wallet.currency(wallet))//can be usefull for the user
				.replaceAll("%uptime%", getUptime()) //uptime
				.replaceAll("%uniqueplayers%",  uniqueplayers()) //unique players
				.replaceAll("%time%", now.toLocaleString()).replaceAll("%hour%", now.getHours() + "").replaceAll("%min%", now.getMinutes() + "").replaceAll("%sec%", now.getSeconds() + "") //time
				.replaceAll("%day%", now.getDate() + "").replaceAll("%month%", now.getMonth() + "").replaceAll("%year%", now.getYear() + ""); //date
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
