package com.ForgeEssentials.api;

import com.ForgeEssentials.api.economy.IEconManager;
import com.ForgeEssentials.api.permissions.IPermissionsHelper;

public class APIRegistry {
	
	// Use this to call API functions available in the economy module.
	public static IEconManager	wallet;
	
	// Use as an alternative way to call API functions from the permissions module.
	public static IPermissionsHelper perms;

}
