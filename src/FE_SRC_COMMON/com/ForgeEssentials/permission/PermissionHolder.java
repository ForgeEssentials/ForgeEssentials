package com.ForgeEssentials.permission;

public class PermissionHolder extends Permission
{
	public final String target;
	public final String zone;

	public PermissionHolder(String target, String qualifiedName, Boolean allowed, String zone)
	{
		super(qualifiedName, allowed);
		this.target = target;
		this.zone = zone;
	}

}
