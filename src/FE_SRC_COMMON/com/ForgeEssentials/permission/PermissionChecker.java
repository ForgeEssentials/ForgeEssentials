package com.ForgeEssentials.permission;

public class PermissionChecker
{
	/**
	 * fully qualified name in format ModName.parent1.parent2.parentN.name
	 */
	public String name;
	public final boolean isAll;

	/**
	 * should only be used for temporary Perm checking.
	 * 
	 * @param qualifiedName
	 * @param allowed
	 */
	public PermissionChecker(String qualifiedName)
	{
		if (name.endsWith("."+Permission.ALL))
		{
			isAll = true;
			name.replace("."+Permission.ALL, "");
		}
		else
		{
			name = qualifiedName;
			isAll = false;
		}
	}

	/**
	 * @return the qualified full name of the parent of this permission's parent. returns "_ALL_" if there is no parent.
	 */
	public String getImmediateParent()
	{
		if (!hasParent())
			return Permission.ALL;
		return name.substring(0, name.lastIndexOf('.') >= 0 ? name.lastIndexOf('.') : 0);
	}
	
	/**
	 * @return the fully qualified name of the parent + _ALL_. unless this perm has no parent, in which case it returns _ALL_
	 */
	public String getAllParent()
	{
		if (!hasParent())
			return Permission.ALL;
		return name.substring(0, name.lastIndexOf('.') >= 0 ? name.lastIndexOf('.') : 0)+"."+Permission.ALL;
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
	 * @return if this Permission is a child of the given Permission. Only works for ALL permisisons.
	 */
	public boolean isChildOf(PermissionChecker perm)
	{
		if (!perm.isAll)
			return false;
		
		String[] here = name.split(".");
		String[] there = perm.name.split(".");

		if (here.length <= there.length)
		{
			return false;
		}

		boolean worked = true;
		for (int i = 0; i < there.length; i++)
		{
			worked = here[i].equals(there[i]);

			if (!worked)
			{
				break;
			}
		}

		return false;
	}

	@Override
	/**
	 * doesn't check the result.. only the name.
	 */
	public boolean equals(Object object)
	{
		if (object instanceof PermissionChecker)
		{
			return name.equals(((PermissionChecker) object).name);
		}
		else if (object instanceof String)
		{
			return object.equals(name);
		}
		return false;
	}

	/**
	 * checks if this Permission can determine the result of the given Permission AKA: checks this permission AND parents.
	 * 
	 * @param perm
	 * @return True if THIS can determine the result of the given permission
	 */
	public boolean matches(PermissionChecker perm)
	{
		if (equals(perm))
		{
			return true;
		}
		else if (perm.isChildOf(this))
		{
			return true;
		}

		return false;
	}

	@Override
	public String toString()
	{
		if (isAll)
			return name + "." + Permission.ALL;
		else
			return name;
	}
	
	public String getAllVersion()
	{
		return name+"."+Permission.ALL;
	}
}
