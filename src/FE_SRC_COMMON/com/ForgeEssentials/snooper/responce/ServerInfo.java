package com.ForgeEssentials.snooper.responce;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.snooper.ConfigSnooper;
import com.ForgeEssentials.snooper.ModuleSnooper;
import com.ForgeEssentials.snooper.TextFormatter;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ServerInfo extends Response
{
	public ServerInfo(DatagramPacket packet)
	{
		super(packet);
		HashMap<String, String> data = new HashMap();
		// ModList
		if(ConfigSnooper.send_Mods)
		{
			ArrayList<String> temp = new ArrayList<String>();
			List<ModContainer> modlist = Loader.instance().getActiveModList();
			for(int i = 0; i < modlist.size(); i++)
			{
				ArrayList<String> ModData = new ArrayList<String>();
				ModData.add(modlist.get(i).getName());
				ModData.add(modlist.get(i).getDisplayVersion());
				temp.add(TextFormatter.toJSON(ModData));
			}
			data.put("mods", TextFormatter.toJSON(temp));
		}
		
		// IP & Port
		if(ConfigSnooper.send_IP)
		{
			if(ModuleSnooper.overrideIP) data.put("hostip", "" + ModuleSnooper.overrideIPValue);
			else data.put("hostip", getIP());
			data.put("hostport", "" + server.getPort());
		}
		// MC version
		data.put("version", server.getMinecraftVersion());
		// WorldName
		data.put("map", server.getFolderName());
		// Player slots
		data.put("maxplayers", "" + server.getMaxPlayers());
		// Gamemode
		data.put("gm", server.getGameType().getName());
		// Difficulty
		data.put("diff", "" + server.getDifficulty());
		// Players online
		data.put("numplayers", "" + server.getCurrentPlayerCount());
		// MOTD (Server list info)
		if(ConfigSnooper.send_Motd) data.put("motd", server.getServerMOTD());
		// Uptime
		data.put("uptime", getUptime());
		// TPS
		data.put("tps", getTPS());
		//WorldBorder
		try
		{
			if(ConfigSnooper.send_WB && ModuleWorldBorder.WBenabled && ModuleWorldBorder.borderData.getBoolean("set"))
			{
				HashMap<String, String> temp = new HashMap();
				temp.put("Shape", ModuleWorldBorder.shape.name());
				Point center = new Point(ModuleWorldBorder.borderData.getInteger("centerX"), 64, ModuleWorldBorder.borderData.getInteger("centerZ"));
				temp.put("Center", TextFormatter.toJSON(center));
				data.put("wb", TextFormatter.toJSON(temp));
			}
		}catch(Exception e){}
			
		dataString = TextFormatter.toJSON(data);
	}
	
	public String getUptime()
	{
		String uptime = "";
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        int secsIn = (int) (rb.getUptime() / 1000);
        int hours = secsIn / 3600, remainder = secsIn % 3600, minutes = remainder / 60,	seconds = remainder % 60;
        
        uptime += ( (hours < 10 ? "0" : "") + hours + " h " + (minutes < 10 ? "0" : "") + minutes + " min " + (seconds< 10 ? "0" : "") + seconds + " sec.");
        
        return uptime;
	}
	
    public String getTPS()
    {
    	HashMap<String, String> data = new HashMap();
    	for (int id : ConfigSnooper.TPSList)
    	{
    		if(server.worldTickTimes.containsKey(id))
    		{
    			data.put("dim " + id, "" + getTPSFromData(server.worldTickTimes.get(id)));
    		}
    	}
    	return TextFormatter.toJSON(data);
    }
	
    /*
     * TPS needed functions
     */
	
	private static final DecimalFormat DF = new DecimalFormat("########0.000");
	/**
	 * 
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

        return (((double)var2 / (double)par1ArrayOfLong.length) * 1.0E-6D);
    }
    
	public static String getTPSFromData(long[] par1ArrayOfLong)
	{
		double tps = (func_79015_a(par1ArrayOfLong)); 
		if(tps < 50)
		{
			return "20";
		}
		else
		{
			return DF.format((1000/tps));
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
