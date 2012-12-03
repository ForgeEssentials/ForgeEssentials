package com.ForgeEssentials.permissions;

import java.util.HashMap;

import com.ForgeEssentials.core.CoreConfig;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraftforge.event.Event.Result;

/**
 * @author AbrarSyed
 */
public class Permission extends PermissionChecker
{
	private static HashMap<String, Permission>	defaults		= new HashMap<String, Permission>();

	public static final Result getPermissionDefault(String name)
	{
		Permission perm = defaults.get(name);
		if (perm != null)
		{
			return perm.allowed;
		}
		else if (name.isEmpty())
		{
			return Result.ALLOW;
		}
		else
		{
			return getPermissionDefault((new PermissionChecker(name).getImmediateParent()));
		}
	}

	/**
	 * This does NOT automatically register parents.
	 * @param perm Permission to be added
	 */
	protected static void addDefaultPermission(Permission perm)
	{
		assert !defaults.containsKey(perm.name) : new IllegalArgumentException("You cannot override a default Permission");
		defaults.put(perm.name, perm);
		if (ModulePermissions.permsVerbose)
			OutputHandler.SOP("Permission Registered: " + perm);
	}

	public Result	allowed;

	public Permission(String qualifiedName, Boolean allowed)
	{
		super(qualifiedName);
		this.allowed = allowed ? Result.ALLOW : Result.DENY;
	}

	public boolean equals(Object object)
	{
		if (object instanceof Permission)
		{
			Permission perm = (Permission) object;
			return name.equals(perm.name) && this.allowed.equals(perm.allowed);
		}
		else if (object instanceof String)
		{
			return object.equals(name);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return name + " : " + allowed;
	}
}
