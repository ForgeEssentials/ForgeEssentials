package com.ForgeEssentials.api;

import com.ForgeEssentials.api.permissions.IPermissionsHelper;
import com.ForgeEssentials.api.permissions.IZoneManager;

public class APIRegistry {
	
	// Use this to call API functions available in the economy module.
	public static IEconManager	wallet;
	
	// Use to call API functions from the permissions module.
	public static IPermissionsHelper perms;
	
	// Use to access the zone manager.
	public static IZoneManager zones;

}
