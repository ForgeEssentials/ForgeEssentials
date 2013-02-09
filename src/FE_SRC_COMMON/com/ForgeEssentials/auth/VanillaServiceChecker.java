package com.ForgeEssentials.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumSet;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class VanillaServiceChecker implements IScheduledTickHandler
{
	public static boolean	online	= true;
	public static boolean	oldOnline;

	public VanillaServiceChecker()
	{
		try
		{
			URL var2 = new URL("http://session.minecraft.net/game/checkserver.jsp");
			BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.openStream()));
			String var4 = var3.readLine();
			var3.close();
			if (!var4.equals("NOT YET"))
			{
				online = false;
			}
		}
		catch (Exception e)
		{
			online = false;
			// e.printStackTrace();
		}
		oldOnline = online;
		OutputHandler.info("VanillaServiceChecker initialized. Vanilla online mode: '" + ModuleAuth.getVanillaOnlineMode() + "' Mojang login servers: '" + online + "'");
		ModuleAuth.FEAuth(online);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		check();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "VanillaAuthServiceChecker";
	}

	@Override
	public int nextTickSpacing()
	{
		// TODO: make configureable
		return 20 * 5;
	}

	private static void check()
	{
		oldOnline = online;
		online = true;
		try
		{
			URL var2 = new URL("http://session.minecraft.net/game/checkserver.jsp");
			BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.openStream()));
			String var4 = var3.readLine();
			var3.close();
			if (!var4.equals("NOT YET"))
			{
				online = false;
			}
		}
		catch (Exception e)
		{
			online = false;
			// e.printStackTrace();
		}

		if (online != oldOnline)
		{
			OutputHandler.warning("MC login changed status, now " + online + "!");
			ModuleAuth.FEAuth(online);
		}
	}

	public static boolean forceCheck()
	{
		check();
		return online;
	}

}
