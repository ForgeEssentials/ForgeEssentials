package com.forgeessentials.permissions.core;

public abstract class ZonePersistanceProvider {

	private ZonedPermissionHelper permissionManager;
	
	public ZonePersistanceProvider(ZonedPermissionHelper permissionManager) {
		this.permissionManager = permissionManager;
	}
	
	public abstract void save();
	
	public abstract void load();
	
}
