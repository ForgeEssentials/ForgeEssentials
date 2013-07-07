package com.ForgeEssentials.core.compat;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;

public class CompatReiMinimap
{
	private static final String	base		= "reiminimap";

	public static final String	cavemap		= base + ".cavemap";
	public static final String	radarPlayer	= base + ".radarPlayer";
	public static final String	radarAnimal	= base + ".radarAnimal";
	public static final String	radarMod	= base + ".radarMod";
	public static final String	radarSlime	= base + ".radarSlime";
	public static final String	radarSquid	= base + ".radarSquid";
	public static final String	radarOther	= base + ".radarOther";

	public static String reimotd(EntityPlayer username)
	{
		try
		{
			String MOTD = "\u00a7e\u00a7f";

			if (APIRegistry.perms.checkPermAllowed(username, cavemap))
			{
				MOTD = "\u00a77" + MOTD;
			}
			if (APIRegistry.perms.checkPermAllowed(username, radarSquid))
			{
				MOTD = "\u00a76" + MOTD;
			}
			if (APIRegistry.perms.checkPermAllowed(username, radarSlime))
			{
				MOTD = "\u00a75" + MOTD;
			}
			if (APIRegistry.perms.checkPermAllowed(username, radarMod))
			{
				MOTD = "\u00a74" + MOTD;
			}
			if (APIRegistry.perms.checkPermAllowed(username, radarAnimal))
			{
				MOTD = "\u00a73" + MOTD;
			}
			if (APIRegistry.perms.checkPermAllowed(username, radarPlayer))
			{
				MOTD = "\u00a72" + MOTD;
			}
			if (APIRegistry.perms.checkPermAllowed(username, cavemap))
			{
				MOTD = "\u00a71" + MOTD;
			}

			MOTD = "\u00a70\u00a70" + MOTD;

			return MOTD;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

}
