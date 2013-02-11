package com.ForgeEssentials.data.typeInfo;

import java.util.HashMap;

import com.ForgeEssentials.api.data.ITypeInfo;

public final class SpecialSaveableInfo
{
	/**
	 * In order to stop people from trying to instantiate
	 */
	private SpecialSaveableInfo() {}

	private static final HashMap<Class, Class<? extends ITypeInfo>>	typeMap	= new HashMap<Class, Class<? extends ITypeInfo>>();

	static
	{
		//registerOverride(TypeDataOverrideMap.class, java.util.Map.class);
	}

	/**
	 * Returns the Override class of provided class. If an override for this
	 * specific class is not found, its SuperClasses are checked recursively.
	 * 
	 * @param c
	 * Class of which to get the override
	 * @return NULL if neither the class nor any of its SuperClasses have an
	 * override counterpart registered
	 */
	public static Class<? extends ITypeInfo> getOverrideType(Class c)
	{
		Class override = typeMap.get(c);
		while (override == null && c != null)
		{
			c = c.getSuperclass();
			override = typeMap.get(c);
		}
		return override;
	}

	/**
	 * Returns a class
	 * 
	 * @param c
	 * Class of which to see if an Override counterpart exists
	 * @param parentCheck
	 * If this method should return true if a superClass of the
	 * provided class is registered
	 * @return FALSE if the class does not have an override counterpart
	 * registered
	 */
	public static boolean hasOverrideType(Class c, boolean parentCheck)
	{
		boolean hasOverride = typeMap.containsValue(c);
		while (parentCheck && hasOverride == false && c != null)
		{
			c = c.getSuperclass();
			hasOverride = typeMap.containsValue(c);
		}
		return hasOverride;
	}

	/**
	 * It is encouraged that any and all classes that have similar data patterns
	 * be grouped under the same Override. eg: the java.util.Map interface
	 * simply has a bunch of keys and values. A new Override is not necessary
	 * for each implementation of it. The same Override class may be used with
	 * HashMaps, TreeMaps, and anything else. Super and Base classes ARE NOT
	 * automatically registered. It is unnecessary to register SubClasses after
	 * registering a SuperClass.
	 * 
	 * @param override
	 * The Override Class.
	 * @param others
	 * The classes which the provided override should be mapped to.
	 */
	public static void registerOverride(Class<? extends ITypeInfo> override, Class... others)
	{
		for (Class c : others)
			typeMap.put(override, c);
	}
}
