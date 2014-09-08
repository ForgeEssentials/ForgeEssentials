package com.forgeessentials.permissions.core;

public abstract class ZonePersistanceProvider {

	private ZonedPermissionManager permissionManager;
	
	public ZonePersistanceProvider(ZonedPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	
	public abstract void save();
	
	public abstract void load();
	
}
