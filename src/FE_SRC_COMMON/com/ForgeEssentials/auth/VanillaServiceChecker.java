package com.ForgeEssentials.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumSet;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class VanillaServiceChecker implements IScheduledTickHandler
{
	private boolean				online		= true;
	private boolean				oldOnline;

	private static final String	MC_SERVER	= "http://session.minecraft.net/game/checkserver.jsp";
	private static final String	ONLINE		= "NOT YET";

	public VanillaServiceChecker()
	{
		online = oldOnline = check();
		OutputHandler.info("VanillaServiceChecker initialized. Vanilla online mode: '" + ModuleAuth.vanillaMode() + "' Mojang login servers: '" + online + "'");
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		oldOnline = online;
		online = check();

		if (oldOnline != online)
		{
			FMLCommonHandler.instance().getSidedDelegate().getServer().setOnlineMode(online);
			ModuleAuth.enabled = !online;
		}
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

	private static boolean check()
	{
		try
		{
			URL url = new URL(MC_SERVER);
			BufferedReader stream = new BufferedReader(new InputStreamReader(url.openStream()));
			String input = stream.readLine();
			stream.close();

			return ONLINE.equals(input);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean isServiceUp()
	{
		return online;
	}

}
