package com.ForgeEssentials.api.permissions;

public interface IPermRegisterEvent
{
	/**
	 * This method will register the permission and its default level. This
	 * should be done with ALL permissions as well. If a group is registered with a given permission,
	 * all higher groups will be allowed the permission, while all lower groups will be denied it.
	 * @param permission Qualified permission node
	 * @param group NULL will deny the permission for everyone.
	 */
	public void registerPermissionLevel(String permission, RegGroup group);

	/**
	 * This method will register the permission and its default level. This
	 * should be done with ALL permissions
	 * @param permission Qualified permission node
	 * @param group NULL will deny the permission for everyone.
	 * @Param alone will only set this permission to this group. Other groups will be unaffected.
	 */
	public void registerPermissionLevel(String permission, RegGroup group, boolean alone);

	/**
	 * Registers a PermissionProperty for use with a default value.
	 * PermissionProperties not registered will be ignored.
	 * @param permission permission node to save the property under.
	 * @param globalDefault the value saved.
	 */
	public void registerPermissionProp(String permission, String globalDefault);

	/**
	 * Registers a PermissionProperty for use with a default value.
	 * PermissionProperties not registered will be ignored.
	 * @param permission permission node to save the property under.
	 * @param globalDefault the value saved.
	 */
	public void registerPermissionProp(String permission, int globalDefault);

	/**
	 * Registers a PermissionProperty for use with a default value.
	 * PermissionProperties not registered will be ignored.
	 * @param permission permission node to save the property under.
	 * @param globalDefault the value saved.
	 */
	public void registerPermissionProp(String permission, float globalDefault);

	/**
	 * Sets a PermissionProperty for the provided group.
	 * @param permission
	 * @param value
	 * @param group
	 */
	public void registerGroupPermissionprop(String permission, String value, RegGroup group);

	/**
	 * Sets a PermissionProperty for the provided group.
	 * @param permission
	 * @param value
	 * @param group
	 */
	public void registerGroupPermissionprop(String permission, int value, RegGroup group);

	/**
	 * Sets a PermissionProperty for the provided group.
	 * @param permission
	 * @param value
	 * @param group
	 */
	public void registerGroupPermissionprop(String permission, float value, RegGroup group);
}
