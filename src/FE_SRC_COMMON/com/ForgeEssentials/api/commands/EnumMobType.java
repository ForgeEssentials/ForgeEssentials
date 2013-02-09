package com.ForgeEssentials.api.commands;

public enum EnumMobType
{
	BOSS, GOLEM, HOSTILE, PASSIVE, VILLAGER, TAMEABLE;

	public static boolean isMobType(String type)
	{
		try
		{
			EnumMobType.valueOf(type.toUpperCase());
			return true;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}
}
