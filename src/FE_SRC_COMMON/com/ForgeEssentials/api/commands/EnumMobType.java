package com.ForgeEssentials.api.commands;

public enum EnumMobType
{
	BOSS("boss"),
	GOLEM("golem"),
	HOSTILE("hostile"),
	PASSIVE("hostile"),
	VILLAGER("villager"),
	TAMEABLE("tameable");
	
	String name;
	
	EnumMobType(String name)
	{
		this.name = name;
	}

	public static boolean isMobType(String type)
	{
		try
		{
			EnumMobType.valueOf(type);
			return true;
		}
		catch(IllegalArgumentException e)
		{
			return false;
		}
	}
}
