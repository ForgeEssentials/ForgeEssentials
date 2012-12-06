package com.ForgeEssentials.permissions;

import java.util.HashMap;

import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.core.CoreConfig;
import com.ForgeEssentials.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.util.OutputHandler;

/**
 * @author AbrarSyed
 */
public class Permission extends PermissionChecker
{
	private static HashMap<String, Permission>	defaults	= new HashMap<String, Permission>();

	public static PermResult getPermissionDefault(String name)
	{
		Permission perm = defaults.get(name);
		if (perm != null)
			return perm.allowed;
		else if (name.isEmpty())
			return PermResult.ALLOW;
		else
			return getPermissionDefault(new PermissionChecker(name).getImmediateParent());
	}

	/**
	 * This does NOT automatically register parents.
	 * @param perm Permission to be added
	 */
	protected static void addDefaultPermission(Permission perm)
	{
		assert !defaults.containsKey(perm.name) : new IllegalArgumentException("You cannot override a default Permission");
		defaults.put(perm.name, perm);
		if (CoreConfig.verbose)
			OutputHandler.SOP("Permission Registered: " + perm);
	}

	public PermResult	allowed;

	public Permission(String qualifiedName, Boolean allowed)
	{
		super(qualifiedName);
		this.allowed = allowed ? PermResult.ALLOW : PermResult.DENY;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Permission)
		{
			Permission perm = (Permission) object;
			return name.equals(perm.name) && allowed.equals(perm.allowed);
		}
		else if (object instanceof String)
			return object.equals(name);
		return false;
	}

	@Override
	public String toString()
	{
		return name + " : " + allowed;
	}
}
