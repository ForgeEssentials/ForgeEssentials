package com.ForgeEssentials.snooper.response;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ServerInfo extends Response
{
	LinkedHashMap<String, String>	data		= new LinkedHashMap<String, String>();
	private boolean					sendWB;
	private boolean					sendMotd;
	private boolean					sendIP;
	private String					overrideIPValue;
	private boolean					sendMods;
	private int[]					TPSList;
	private boolean					overrideIP;
	public static Integer			ServerID	= 0;
	public static String			serverHash	= "";

	@Override
	public String getResponceString(DatagramPacket packet)
	{
		if (sendMods)
		{
			ArrayList<String> temp = new ArrayList<String>();
			List<ModContainer> modlist = Loader.instance().getActiveModList();
			for (int i = 0; i < modlist.size(); i++)
			{
				ArrayList<String> ModData = new ArrayList<String>();
				ModData.add(modlist.get(i).getName());
				ModData.add(modlist.get(i).getDisplayVersion());
				temp.add(TextFormatter.toJSON(ModData));
			}
			data.put("mods", TextFormatter.toJSON(temp));
		}

		if (sendIP)
		{
			if (overrideIP)
			{
				data.put("hostip", "" + overrideIPValue);
			}
			else
			{
				data.put("hostip", getIP());
			}
			data.put("hostport", "" + server.getPort());
		}
		data.put("version", server.getMinecraftVersion());
		data.put("map", server.getFolderName());
		data.put("maxplayers", "" + server.getMaxPlayers());

		if (ServerID != 0)
		{
			data.put("serverID", ServerID + "");
		}
		if (!serverHash.equals(""))
		{
			data.put("serverHash", serverHash + "");
		}

		data.put("gm", server.getGameType().getName());
		data.put("diff", "" + server.getDifficulty());
		data.put("numplayers", "" + server.getCurrentPlayerCount());
		if (sendMotd)
		{
			data.put("motd", server.getServerMOTD());
		}

		data.put("uptime", getUptime());
		data.put("tps", getTPS());

		try
		{
			if (sendWB && ModuleWorldBorder.WBenabled && ModuleWorldBorder.set)
			{
				LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
				temp.put("Shape", ModuleWorldBorder.shape.name());
				Point center = new Point(ModuleWorldBorder.X, 64, ModuleWorldBorder.Z);
				temp.put("Center", TextFormatter.toJSON(center));
				data.put("wb", TextFormatter.toJSON(temp));
			}
		}
		catch (Exception e)
		{}

		return dataString = TextFormatter.toJSON(data);
	}

	@Override
	public String getName()
	{
		return "ServerInfo";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		sendWB = config.get(category, "sendWB", true).getBoolean(true);
		sendMotd = config.get(category, "sendMotd", true).getBoolean(true);
		sendIP = config.get(category, "sendIP", true).getBoolean(true);
		overrideIP = config.get(category, "overrideIP", true).getBoolean(true);
		overrideIPValue = config.get(category, "overrideIPValue", "").getString();
		sendMods = config.get(category, "sendMods", true).getBoolean(true);
		TPSList = config.get(category, "TPS_dim", new int[]
		{ -1, 0, 1 }, "Dimensions to send TPS of").getIntList();
		ServerID = config.get(category, "ServerID", 0, "This is here to make it easy for other sites (server lists) to help authenticate the server.").getInt();
		serverHash = config.get(category, "serverHash", "", "This is here to make it easy for other sites (server lists) to help authenticate the server.").getString();
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		config.get(category, "sendWB", true).set(sendWB);
		config.get(category, "sendMotd", true).set(sendMotd);
		config.get(category, "sendIP", true).set(sendIP);
		config.get(category, "overrideIP", true).set(overrideIP);
		config.get(category, "overrideIPValue", "").set(overrideIPValue);
		config.get(category, "sendMods", true).set(sendMods);
		config.get(category, "ServerID", 0, "This is here to make it easy for other sites (server lists) to help authenticate the server.").set(ServerID);
		config.get(category, "serverHash", "", "This is here to make it easy for other sites (server lists) to help authenticate the server.").set(serverHash);

		String[] list = new String[TPSList.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = "" + TPSList[i];
		}
		config.get(category, "TPS_dim", new int[]
		{ -1, 0, 1 }, "Dimensions to send TPS of").set(list);
	}

	public String getUptime()
	{
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		int secsIn = (int) (rb.getUptime() / 1000);
		return FunctionHelper.parseTime(secsIn);
	}

	public String getTPS()
	{
		try
		{
			LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
			for (int id : TPSList)
			{
				if (server.worldTickTimes.containsKey(id))
				{
					data.put("dim " + id, "" + getTPSFromData(server.worldTickTimes.get(id)));
				}
			}
			return TextFormatter.toJSON(data);
		}
		catch (Exception e)
		{
			return TextFormatter.toJSON(new String[]
			{ "" });
		}
	}

	/*
	 * TPS needed functions
	 */

	private static final DecimalFormat	DF	= new DecimalFormat("########0.000");

	/**
	 * @param par1ArrayOfLong
	 * @return amount of time for 1 tick in ms
	 */
	private static double func_79015_a(long[] par1ArrayOfLong)
	{
		long var2 = 0L;
		long[] var4 = par1ArrayOfLong;
		int var5 = par1ArrayOfLong.length;

		for (int var6 = 0; var6 < var5; ++var6)
		{
			long var7 = var4[var6];
			var2 += var7;
		}

		return (double) var2 / (double) par1ArrayOfLong.length * 1.0E-6D;
	}

	public static String getTPSFromData(long[] par1ArrayOfLong)
	{
		double tps = func_79015_a(par1ArrayOfLong);
		if (tps < 50)
			return "20";
		else
			return DF.format(1000 / tps);
	}

	public String getIP()
	{
		try
		{
			InetAddress var2 = InetAddress.getLocalHost();
			return var2.getHostAddress();
		}
		catch (UnknownHostException var3)
		{
			FMLLog.warning("Unable to determine local host IP, please set server-ip/hostname in the snooper config : " + var3.getMessage());
			return null;
		}
	}
}
