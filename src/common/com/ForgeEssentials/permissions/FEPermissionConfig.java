package com.ForgeEssentials.permissions;

import java.io.File;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.config.Configuration;

public class FEPermissionConfig
{
	public static File permissionsFile = new File(ForgeEssentials.FEDIR, "permissions.txt");
	
	public Configuration config;
	
	public FEPermissionConfig()
	{
		config = new Configuration(permissionsFile, true);
	}
	
}
