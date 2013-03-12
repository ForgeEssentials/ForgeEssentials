package com.ForgeEssentials.permission;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.ForgeEssentials.api.modules.CallableMap.FECallable;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.util.OutputHandler;
import com.google.common.collect.HashMultimap;

public class PermRegLoader
{
	protected HashSet<String>	mods;
	private Set<FECallable>		data;

	public PermRegLoader(Set<FECallable> calls)
	{
		mods = new HashSet<String>();
		data = calls;
	}

	protected HashMultimap<RegGroup, PermissionChecker> loadAllPerms()
	{
		PermissionRegistrationEvent event = new PermissionRegistrationEvent();

		Class c = null;
		Method m = null;
		String className, methodName, modid;
		for (FECallable call : data)
		{
			modid = call.getIdent();
			mods.add(modid);

			try
			{
				call.call(event);
			}
			catch (Exception e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
		}

		return event.perms;
	}

	private class PermissionRegistrationEvent implements IPermRegisterEvent
	{
		protected HashMultimap<RegGroup, PermissionChecker>		perms;

		protected PermissionRegistrationEvent()
		{
			perms = HashMultimap.create();
		}

		@Override
		public void registerPermissionLevel(String permission, RegGroup group)
		{
			registerPermissionLevel(permission, group, false);
		}
		
		@Override
		public void registerPermissionLevel(String permission, RegGroup group, boolean alone)
		{
			Permission deny = new Permission(permission, false);
			Permission allow = new Permission(permission, true);

			if (group == null)
			{
				perms.put(RegGroup.ZONE, deny);
			}
			else if (group == RegGroup.ZONE)
			{
				perms.put(RegGroup.ZONE, allow);
			}
			else
			{
				perms.put(group, allow);
				
				if (alone)
					return;
				
				for (RegGroup g : getHigherGroups(group))
				{
					perms.put(g, allow);
				}

				for (RegGroup g : getLowerGroups(group))
				{
					perms.put(g, deny);
				}
			}
		}

		private RegGroup[] getHigherGroups(RegGroup g)
		{
			switch (g)
				{
					case GUESTS:
						return new RegGroup[]
						{ RegGroup.MEMBERS, RegGroup.ZONE_ADMINS, RegGroup.OWNERS };
					case MEMBERS:
						return new RegGroup[]
						{ RegGroup.ZONE_ADMINS, RegGroup.OWNERS };
					case ZONE_ADMINS:
						return new RegGroup[]
						{ RegGroup.OWNERS };
					default:
						return new RegGroup[] {};
				}
		}

		private RegGroup[] getLowerGroups(RegGroup g)
		{
			switch (g)
				{
					case MEMBERS:
						return new RegGroup[]
						{ RegGroup.GUESTS };
					case ZONE_ADMINS:
						return new RegGroup[]
						{ RegGroup.MEMBERS, RegGroup.GUESTS };
					case OWNERS:
						return new RegGroup[]
						{ RegGroup.MEMBERS, RegGroup.GUESTS, RegGroup.ZONE_ADMINS };
					default:
						return new RegGroup[] {};
				}
		}

		@Override
		public void registerPermissionProp(String permission, String globalDefault)
		{
			PermissionProp prop = new PermissionProp(permission, globalDefault);
			perms.put(RegGroup.ZONE, prop);
		}

		@Override
		public void registerPermissionProp(String permission, int globalDefault)
		{
			PermissionProp prop = new PermissionProp(permission, ""+globalDefault);
			perms.put(RegGroup.ZONE, prop);
		}

		@Override
		public void registerPermissionProp(String permission, float globalDefault)
		{
			PermissionProp prop = new PermissionProp(permission, ""+globalDefault);
			perms.put(RegGroup.ZONE, prop);
		}

		@Override
		public void registerGroupPermissionprop(String permission, String value, RegGroup group)
		{
			PermissionProp prop = new PermissionProp(permission, ""+value);
			perms.put(group, prop);
		}

		@Override
		public void registerGroupPermissionprop(String permission, int value, RegGroup group)
		{
			PermissionProp prop = new PermissionProp(permission, ""+value);
			perms.put(group, prop);
		}

		@Override
		public void registerGroupPermissionprop(String permission, float value, RegGroup group)
		{
			PermissionProp prop = new PermissionProp(permission, ""+value);
			perms.put(group, prop);
		}
	}
}
