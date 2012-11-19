package com.ForgeEssentials.permissions;

import java.util.HashMap;

import com.ForgeEssentials.core.OutputHandler;

import net.minecraftforge.event.Event.Result;

/**
 * @author AbrarSyed
 */
public class Permission
{
	private static HashMap<String, Permission> defaults = new HashMap<String, Permission>();
	
	public static Result getPermissionDefault(String name)
	{
		Permission perm = defaults.get(name);
		if (perm != null)
			return perm.allowed;
		else
			return Result.DENY;
	}
	
	/**
	 * This does NOT automatically register parents.
	 * @param perm Permission to be added
	 */
	protected static void addDefaultPermission(Permission perm)
	{
		assert !defaults.containsKey(perm.name) : new IllegalArgumentException("You cannot override a default Permission");
		defaults.put(perm.name, perm);
		OutputHandler.SOP("Permission Registerred: "+perm);
	}
	
	public String	name;
	public Result	allowed;
	
	/**
	 * should only be used for temporary Perm checking.
	 * @param qualifiedName
	 * @param allowed
	 */
	public Permission(String qualifiedName)
	{
		name = qualifiedName;
		this.allowed = Result.DEFAULT;
	}

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
	/**
	 * doesn't check teh result.. only the name.
	 */
	public boolean equals(Object object)
	{
		if (object instanceof Permission)
		{
			Permission perm = (Permission) object;
			return name.equals(perm.name);
		}
		if (object instanceof String)
		{
			return object.equals(name);
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
