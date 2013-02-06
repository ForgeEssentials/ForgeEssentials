package com.ForgeEssentials.permission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.util.OutputHandler;
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
		PermissionRegistrationEvent event = new PermissionRegistrationEvent();

		Class c = null;
		Method m = null;
		String className, methodName, modid;
		for (ASMData asm : data)
		{
			className = asm.getClassName();
			methodName = asm.getObjectName();
			modid = asm.getAnnotationInfo().get("ident").toString();

			try
			{
				c = Class.forName(className);
				m = c.getDeclaredMethod(methodName, IPermRegisterEvent.class);
				m.setAccessible(true);

				if (!m.getReturnType().equals(void.class))
					throw new RuntimeException(m.getName() + " must return void!");
				if (!Modifier.isStatic(m.getModifiers()))
					throw new RuntimeException(m.getName() + " must be static!");

				m.invoke(null, event);
			}
			catch (ClassNotFoundException e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				OutputHandler.severe("Error trying to load permissions from \"" + modid + "\"!");
				e.printStackTrace();
			}
		}

		return event.perms;
	}

	private class PermissionRegistrationEvent implements IPermRegisterEvent
	{
		protected HashMultimap<RegGroup, Permission>	perms;

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
						return new RegGroup[] { RegGroup.MEMBERS, RegGroup.ZONE_ADMINS, RegGroup.OWNERS };
					case MEMBERS:
						return new RegGroup[] { RegGroup.ZONE_ADMINS, RegGroup.OWNERS };
					case ZONE_ADMINS:
						return new RegGroup[] { RegGroup.OWNERS };
					default:
						return new RegGroup[] {};
				}
		}

		private RegGroup[] getLowerGroups(RegGroup g)
		{
			switch (g)
				{
					case MEMBERS:
						return new RegGroup[] { RegGroup.GUESTS };
					case ZONE_ADMINS:
						return new RegGroup[] { RegGroup.MEMBERS, RegGroup.GUESTS };
					case OWNERS:
						return new RegGroup[] { RegGroup.MEMBERS, RegGroup.GUESTS, RegGroup.ZONE_ADMINS };
					default:
						return new RegGroup[] {};
				}
		}
	}
}
