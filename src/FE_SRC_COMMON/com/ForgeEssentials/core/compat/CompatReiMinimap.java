package com.ForgeEssentials.core.compat;

import com.ForgeEssentials.util.OutputHandler;

public class CompatReiMinimap {
	
	public static boolean cavemap;

	public static boolean radarPlayer;

	public static boolean radarAnimal;

	public static boolean radarMod;

	public static boolean radarSlime;

	public static boolean radarSquid;

	public static boolean radarOther;
	
	public static String reimotd()
	{
		try
		{	
			String MOTD = "\u00a7e\u00a7f";
			
			if(radarOther) MOTD 	= "\u00a77" + MOTD;
			if(radarSquid) MOTD 	= "\u00a76" + MOTD;
			if(radarSlime) MOTD 	= "\u00a75" + MOTD;
			if(radarMod) MOTD 		= "\u00a74" + MOTD;
			if(radarAnimal) MOTD 	= "\u00a73" + MOTD;
			if(radarPlayer) MOTD 	= "\u00a72" + MOTD;
			if(cavemap) MOTD 		= "\u00a71" + MOTD;
			
			MOTD = "\u00a70\u00a70" + MOTD;
			
			OutputHandler.debug("Rei's minimap settings: " + MOTD.replaceAll("\u00a7", "&"));
			
			return MOTD;
		}
		catch (Exception e)
		{e.printStackTrace();}
		return "";
	}

}
