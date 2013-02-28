package com.ForgeEssentials.api.permissions;

public interface IPermRegisterEvent
{
	/**
	 * This method will register the permission and its default level. This
	 * should be done with ALL permissions
	 * @param permission
	 *            Qualified permission node
	 * @param group
	 *            NULL will deny the permission for everyone.
	 */
	public void registerPermissionLevel(String permission, RegGroup group);
}
