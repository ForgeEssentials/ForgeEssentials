package com.ForgeEssentials.commands.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTameable;

import com.ForgeEssentials.api.commands.EnumMobType;
import com.google.common.collect.HashMultimap;

public class MobTypeRegistry
{
	private static final HashMultimap<EnumMobType, String>	MobTypeRegistry	= HashMultimap.create();
	private static final HashMap<String, String>			tameableChecks	= new HashMap<String, String>();

	public static final void addMob(EnumMobType type, String className)
	{
		MobTypeRegistry.put(type, className);
	}

	public static final void addMob(EnumMobType type, String className, String tameableCheckObject)
	{
		tameableChecks.put(className, tameableCheckObject);
		MobTypeRegistry.put(type, className);
	}

	public static Set<String> getCollectionForMobType(EnumMobType type)
	{
		return MobTypeRegistry.get(type);
	}

	public static boolean isTamed(EntityLiving mob)
	{
		if (mob instanceof EntityTameable)
			return ((EntityTameable) mob).isTamed();
		else if (MobTypeRegistry.get(EnumMobType.TAMEABLE).contains(mob.getClass().getName()))
		{
			try
			{
				Class c = mob.getClass();
				String obj = tameableChecks.get(c.getName());
				boolean isMethod = obj.endsWith("()");
				obj = obj.replace("()", "");  // the () is just to mark it..

				if (isMethod)
				{
					Method m = c.getDeclaredMethod(obj, new Class[] {});
					m.setAccessible(true);
					return (Boolean) m.invoke(mob, new Object[] {});
				}
				else
				{
					Field f = c.getDeclaredField(obj);
					f.setAccessible(true);
					return f.getBoolean(mob);
				}
			}
			catch (Exception e)
			{
				return false;
			}
		}
		else
			return false;
	}
}
