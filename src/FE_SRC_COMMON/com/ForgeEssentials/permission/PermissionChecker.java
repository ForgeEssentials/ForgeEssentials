package com.ForgeEssentials.permission;

public class PermissionChecker
{
	/**
	 * fully qualified name in format ModName.parent1.parent2.parentN.name
	 */
	public String	name;

	/**
	 * should only be used for temporary Perm checking.
	 * @param qualifiedName
	 * @param allowed
	 */
	public PermissionChecker(String qualifiedName)
	{
		name = qualifiedName;
	}

	/**
	 * @return the qualified full name of the parent of this permission's parent. returns "" if there is no parent.
	 */
	public String getImmediateParent()
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
	public boolean isChildOf(PermissionChecker perm)
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
	 * doesn't check the result.. only the name.
	 */
	public boolean equals(Object object)
	{
		if (object instanceof PermissionChecker)
		{
			return name.equals(((PermissionChecker)object).name);
		}
		else if (object instanceof String)
			return object.equals(name);
		return false;
	}

	/**
	 * checks if this Permission can determine the result of the given Permission
	 * AKA: checks this permission AND parents.
	 * @param perm
	 * @return True if THIS can determine the result of the given permission
	 */
	public boolean matches(PermissionChecker perm)
	{
		if (equals(perm))
			return true;
		else if (perm.isChildOf(this))
			return true;

		return false;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
