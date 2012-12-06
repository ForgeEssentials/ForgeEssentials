package com.ForgeEssentials.data.nbt;

import java.sql.DriverManager;
import java.sql.SQLException;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

public class NBTDataDriver extends DataDriver {
	private String baseFilePath;
	public NBTDataDriver()
	{
		super();
		
		DataDriver.instance = this;
	}
	@Override
	public boolean parseConfigs(Configuration config, String worldName) {
	Property prop;
		
		prop = config.get("Data.NBT", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";
		
		boolean useFEDir = prop.getBoolean(false);
		
		if (useFEDir)
		{
			this.baseFilePath = ForgeEssentials.FEDIR.toString() + "saves/" + worldName + "/";
		}
		else
		{
			if (Side.CLIENT == FMLCommonHandler.instance().getEffectiveSide())
			{
				this.baseFilePath = "./saves/" + worldName + "/";
			}
			else
			{
				this.baseFilePath = "./" + worldName +"/";
			}
		}
		
		config.save();
		
		// Nothing to fail on.
		return true;
	}

	@Override
	protected void registerAdapters() {
		this.map.put(PlayerInfo.class, new PlayerInfoDataAdapter());

		
	}

	public String getBaseBath()
	{
		return this.baseFilePath;
	}


}
