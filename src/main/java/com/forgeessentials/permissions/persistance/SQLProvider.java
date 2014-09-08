package com.forgeessentials.permissions.persistance;

import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.permissions.core.ZonePersistanceProvider;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;

public class SQLProvider extends ZonePersistanceProvider {
	
	public SQLProvider(ZonedPermissionHelper permissionManager)
	{
		super(permissionManager);
	}


	@Override
	public void save(RootZone rootZone)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public RootZone load()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
