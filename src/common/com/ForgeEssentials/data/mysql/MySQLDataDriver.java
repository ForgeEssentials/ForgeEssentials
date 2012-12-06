package com.ForgeEssentials.data.mysql;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.data.DataDriver;

public class MySQLDataDriver extends DataDriver
{

	@Override
	public void parseConfigs(Configuration config, String worldName)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerAdapters()
	{
		this.map.put(PlayerInfo.class, new PlayerInfoDataAdapter());

	}

}
