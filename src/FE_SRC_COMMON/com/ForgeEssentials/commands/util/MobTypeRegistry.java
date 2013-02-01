package com.ForgeEssentials.commands.util;

import java.util.Set;

import com.ForgeEssentials.api.commands.EnumMobType;
import com.google.common.collect.HashMultimap;

public class MobTypeRegistry
{
	private static final HashMultimap<EnumMobType, String> MobTypeRegistry = HashMultimap.create();
	
	public static final void addMob(EnumMobType type, String className)
	{
		MobTypeRegistry.put(type, className);
	}
	
	public static final Set<String> getCollectionForMobType(EnumMobType type)
	{
		return MobTypeRegistry.get(type);
	}
}
