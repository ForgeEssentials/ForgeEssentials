package com.ForgeEssentials.permission;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.util.OutputHandler;
import com.google.common.collect.HashMultimap;

import net.minecraft.src.BaseMod;
import net.minecraftforge.event.Event;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;

public class PermissionRegistrationEvent implements IPermRegisterEvent
{
	protected HashMultimap<RegGroup, Permission> perms;
	
	protected PermissionRegistrationEvent()
	{
		perms = HashMultimap.create();
	}

	@Override
	public void registerPermissionLevel(String permission, RegGroup group)
	{
		Permission deny = new Permission(permission, group != null);
		Permission allow = new Permission(permission, true);
		
		if (group == null)
			perms.put(RegGroup.ZONE, deny);
		else if (group == RegGroup.ZONE)
			perms.put(RegGroup.ZONE, allow);
		else
		{
			perms.put(group, allow);
			for (RegGroup g: getHigherGroups(group))
				perms.put(g, allow);
			
			for (RegGroup g: getLowerGroups(group))
				perms.put(g, deny);
		}
	}
	
	private RegGroup[] getHigherGroups(RegGroup g)
	{
		switch(g)
		{
			case GUESTS: return new RegGroup[] {RegGroup.MEMBERS, RegGroup.ZONE_ADMINS, RegGroup.OWNERS};
			case MEMBERS: return new RegGroup[] {RegGroup.ZONE_ADMINS, RegGroup.OWNERS};
			case ZONE_ADMINS: return new RegGroup[] {RegGroup.OWNERS};
			default: return new RegGroup[] {};
		}
	}
	
	private RegGroup[] getLowerGroups(RegGroup g)
	{
		switch(g)
		{
			case MEMBERS: return new RegGroup[] {RegGroup.GUESTS};
			case ZONE_ADMINS: return new RegGroup[] {RegGroup.MEMBERS, RegGroup.GUESTS};
			case OWNERS: return new RegGroup[] {RegGroup.MEMBERS, RegGroup.GUESTS, RegGroup.ZONE_ADMINS};
			default: return new RegGroup[] {};
		}
	}
}
