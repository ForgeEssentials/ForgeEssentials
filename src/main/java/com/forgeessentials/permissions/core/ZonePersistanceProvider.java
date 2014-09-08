package com.forgeessentials.permissions.core;

import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;

public abstract class ZonePersistanceProvider {

	private ZonedPermissionHelper permissionManager;
	
	public ZonePersistanceProvider(ZonedPermissionHelper permissionManager) {
		this.permissionManager = permissionManager;
	}
	
	public abstract void save(RootZone rootZone);
	
	public abstract RootZone load();
	
}
