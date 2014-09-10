package com.forgeessentials.permissions.core;

import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;

public abstract class ZonePersistanceProvider {

	public abstract void save(ServerZone serverZone);
	
	public abstract ServerZone load();
	
}
