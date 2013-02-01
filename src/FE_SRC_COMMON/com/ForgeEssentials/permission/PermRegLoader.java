package com.ForgeEssentials.permission;

import java.util.HashSet;
import java.util.Set;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.google.common.collect.HashMultimap;

import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

public class PermRegLoader
{
	protected HashSet<String>	mods;
	private Set<ASMData>		data;

	public PermRegLoader(Set<ASMData> asm)
	{
		mods = new HashSet<String>();
		data = asm;
	}

	protected HashMultimap<RegGroup, Permission> loadAllPerms()
	{
		HashMultimap<RegGroup, Permission> map = HashMultimap.create();
		return map;
	}
}
