package com.ForgeEssentials.permission;

/**
 * @author AbrarSyed
 */
public class Permission extends PermissionChecker
{
	public static final String	ALL	= "_ALL_";

	public boolean				allowed;

	public Permission(String qualifiedName, Boolean allowed)
	{
		super(qualifiedName);
		this.allowed = allowed;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof Permission)
		{
			Permission perm = (Permission) object;
			return name.equals(perm.name) && allowed == perm.allowed;
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
