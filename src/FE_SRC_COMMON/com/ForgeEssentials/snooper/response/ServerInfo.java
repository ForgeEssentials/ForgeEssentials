package com.ForgeEssentials.snooper.response;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONArray;
import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ServerInfo extends Response
{
	private JSONObject		data		= new JSONObject();
	private boolean			sendWB;
	private boolean			sendMotd;
	private boolean			sendIP;
	private String			overrideIPValue;
	private boolean			sendMods;
	private int[]			TPSList;
	private boolean			overrideIP;
	public static Integer	ServerID	= 0;
	public static String	serverHash	= "";

	@Override
	public JSONObject getResponce(JSONObject input) throws JSONException
	{
		if (sendMods)
		{
			JSONArray temp = new JSONArray();
			List<ModContainer> modlist = Loader.instance().getActiveModList();
			for (int i = 0; i < modlist.size(); i++)
			{
				ArrayList<String> ModData = new ArrayList<String>();
				ModData.add(modlist.get(i).getName());
				ModData.add(modlist.get(i).getDisplayVersion());
				temp.put(ModData);
			}
			data.put("Mods", temp);
		}

		if (sendIP)
		{
			if (overrideIP)
			{
				data.put("Hostname", "" + overrideIPValue + ":" + server.getPort());
			}
			else
			{
				data.put("Hostname", getIP() + ":" + server.getPort());
			}
		}
		data.put("MCversion", server.getMinecraftVersion());
		data.put("WorldName", server.getFolderName());
		data.put("Slots", "" + server.getMaxPlayers());

		if (ServerID != 0)
		{
			data.put("ServerID", ServerID + "");
		}
		if (!serverHash.equals(""))
		{
			data.put("ServerHash", serverHash + "");
		}

		data.put("Gamemode", server.getGameType().getName());
		data.put("Difficulty", "" + server.getDifficulty());
		data.put("OnlinePlayers", "" + server.getCurrentPlayerCount());
		if (sendMotd)
		{
			data.put("MOTD", server.getServerMOTD());
		}

		data.put("Uptime", getUptime());
		data.put("TPS", getTPS());

		data.put("Players", server.getAllUsernames());

		return new JSONObject().put(getName(), data);
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
		overrideIPValue = config.get(category, "overrideIPValue", "").value;
		sendMods = config.get(category, "sendMods", true).getBoolean(true);
		TPSList = config.get(category, "TPS_dim", new int[]
		{ -1, 0, 1 }, "Dimensions to send TPS of").getIntList();
		ServerID = config.get(category, "ServerID", 0, "This is here to make it easy for other sites (server lists) to help authenticate the server.").getInt();
		serverHash = config.get(category, "serverHash", "", "This is here to make it easy for other sites (server lists) to help authenticate the server.").value;
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		config.get(category, "sendWB", true).value = "" + sendWB;
		config.get(category, "sendMotd", true).value = "" + sendMotd;
		config.get(category, "sendIP", true).value = "" + sendIP;
		config.get(category, "overrideIP", true).value = "" + overrideIP;
		config.get(category, "overrideIPValue", "").value = overrideIPValue;
		config.get(category, "sendMods", true).value = "" + sendMods;
		config.get(category, "ServerID", 0, "This is here to make it easy for other sites (server lists) to help authenticate the server.").value = "" + ServerID;
		config.get(category, "serverHash", "", "This is here to make it easy for other sites (server lists) to help authenticate the server.").value = serverHash;

		String[] list = new String[TPSList.length];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = "" + TPSList[i];
		}
		config.get(category, "TPS_dim", new int[]
		{ -1, 0, 1 }, "Dimensions to send TPS of").valueList = list;
	}

	public String getUptime()
	{
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		int secsIn = (int) (rb.getUptime() / 1000);
		return FunctionHelper.parseTime(secsIn);
	}

	public JSONObject getTPS()
	{
		try
		{
			JSONObject data = new JSONObject();
			for (int id : TPSList)
			{
				if (server.worldTickTimes.containsKey(id))
				{
					data.put("Dim " + id, "" + getTPSFromData(server.worldTickTimes.get(id)));
				}
			}
			return data;
		}
		catch (Exception e)
		{
			return new JSONObject();
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
