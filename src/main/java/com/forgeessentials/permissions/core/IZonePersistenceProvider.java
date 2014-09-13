package com.forgeessentials.permissions.core;

import com.forgeessentials.api.permissions.ServerZone;

public interface IZonePersistenceProvider {

	public void save(ServerZone serverZone);
	
	public ServerZone load();
	
}
