package com.forgeessentials.snooper.response;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.FunctionHelper;
import com.google.gson.*;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraftforge.common.config.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ServerInfo extends Response {
    private static final DecimalFormat DF = new DecimalFormat("########0.000");
    public static Integer ServerID = 0;
    public static String serverHash = "";
    private JsonObject data = new JsonObject();
    private boolean sendWB;
    private boolean sendMotd;
    private boolean sendIP;
    private String overrideIPValue;
    private boolean sendMods;
    private int[] TPSList;
    private boolean overrideIP;

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
        {
            return "20";
        }
        else
        {
            return DF.format(1000 / tps);
        }
    }

    @Override
    public JsonObject getResponce(JsonObject input) throws JsonParseException
    {
        if (sendMods)
        {
            JsonArray temp = new JsonArray();
            Gson gson = new Gson();
            List<ModContainer> modlist = Loader.instance().getActiveModList();
            for (int i = 0; i < modlist.size(); i++)
            {
                ArrayList<String> modData = new ArrayList<String>();
                modData.add(modlist.get(i).getName());
                modData.add(modlist.get(i).getDisplayVersion());
                temp.add(new JsonPrimitive(gson.toJson(modData)));
            }
            data.add("Mods", temp);
        }

        if (sendIP)
        {
            if (overrideIP)
            {
                data.add("Hostname", new JsonPrimitive("" + overrideIPValue + ":" + server.getPort()));
            }
            else
            {
                data.add("Hostname", new JsonPrimitive(getIP() + ":" + server.getPort()));
            }
        }
        data.add("MCversion", new JsonPrimitive(server.getMinecraftVersion()));
        data.add("WorldName", new JsonPrimitive(server.getFolderName()));
        data.add("Slots", new JsonPrimitive(server.getMaxPlayers()));

        if (ServerID != 0)
        {
            data.add("ServerID", new JsonPrimitive(ServerID + ""));
        }
        if (!serverHash.equals(""))
        {
            data.add("ServerHash", new JsonPrimitive(serverHash + ""));
        }

        data.add("Gamemode", new JsonPrimitive(server.getGameType().getName()));
        data.add("Difficulty", new JsonPrimitive("" + server.getEntityWorld().difficultySetting));
        data.add("OnlinePlayers", new JsonPrimitive("" + server.getCurrentPlayerCount()));
        if (sendMotd)
        {
            data.add("MOTD", new JsonPrimitive(server.getMOTD()));
        }

        data.add("Uptime", new JsonPrimitive(getUptime()));
        data.add("TPS", getTPS());

        JsonArray users = new JsonArray();
        for (String name : server.getAllUsernames())
        {
            users.add(new JsonPrimitive(name));
        }

        data.add("Players", users);

        JsonObject out = new JsonObject();
        out.add(getName(), data);
        return out;
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
        serverHash = config.get(category, "serverHash", "", "This is here to make it easy for other sites (server lists) to help authenticate the server.")
                .getString();
    }

	/*
     * TPS needed functions
	 */

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

    public JsonObject getTPS()
    {
        try
        {
            JsonObject data = new JsonObject();
            for (int id : TPSList)
            {
                if (server.worldTickTimes.containsKey(id))
                {
                    data.add("Dim " + id, new JsonPrimitive("" + getTPSFromData(server.worldTickTimes.get(id))));
                }
            }
            return data;
        }
        catch (Exception e)
        {
            return new JsonObject();
        }
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
