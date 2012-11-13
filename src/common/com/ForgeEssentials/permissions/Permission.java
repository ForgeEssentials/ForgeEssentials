package com.ForgeEssentials.permissions;

import java.util.HashMap;

import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.Event.*;

/**
 * @author AbrarSyed
 */
public class Permission
{
	private static HashMap<String, Permission> defaults;
	
	public static Result getPermissionDefault(String name)
	{
		Permission perm = defaults.get(name);
		if (perm != null)
			return perm.allowed;
		else
			return Result.DENY;
	}
	
	public static void addDefaultPermission(Permission perm)
	{
		defaults.put(perm.name, perm);
	}
	
	private String	name;
	private Result	allowed;

	public Permission(String qualifiedName, Boolean allowed)
	{
		name = qualifiedName;
		this.allowed = allowed ? Result.ALLOW : Result.DENY;
	}

	/**
	 * @return the qualified full name of the parent of this permission's parent. returns "" if there is no parent.
	 */
	public String getParent()
	{
		return name.substring(0, name.lastIndexOf('.') >= 0 ? name.lastIndexOf('.') : 0);
	}

	/**
	 * @return the modID of the mod that added this permission. returns "" if there is none.
	 */
	public String getMod()
	{
		return name.split(".")[0];
	}

	/**
	 * @return if this permission has a parent.
	 */
	public boolean hasParent()
	{
		return name.contains(".");
	}

	/**
	 * @return if this Permission is a child of the given Permission.
	 */
	public boolean isChild(Permission perm)
	{
		String[] here = name.split(".");
		String[] there = perm.name.split(".");

		if (here.length <= there.length)
			return false;

		boolean worked = true;
		for (int i = 0; i < there.length; i++)
		{
			worked = here[i].equals(there[i]);

			if (!worked)
				break;
		}

		return false;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Permission)
		{
			Permission perm = (Permission) object;
			return perm.name.equals(name) && allowed == perm.allowed;
		}
		return false;
	}

	/**
	 * checks if the given Permission can allow/deny this permission.
	 * @param perm
	 * @return if the given Perm can allow/deny this perm.
	 */
	public boolean matches(Permission perm)
	{
		if (this.equals(perm))
			return true;
		else if (this.isChild(perm))
			return true;

		return false;
	}
	
	@Override
	public String toString()
	{
		return name+" : "+allowed;
	}
}
