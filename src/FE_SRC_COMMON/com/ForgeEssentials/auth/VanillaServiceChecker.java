package com.ForgeEssentials.auth;

import java.util.EnumSet;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class VanillaServiceChecker implements IScheduledTickHandler
{
	public static boolean online;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		check();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData){}

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
		return 0;
	}
	
	private static void check()
	{
		// TODO: implement
	}
	
	public static boolean forceCheck()
	{
		check();
		return online;
	}

}
