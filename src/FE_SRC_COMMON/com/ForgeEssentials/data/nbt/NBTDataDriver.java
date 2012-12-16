package com.ForgeEssentials.data.nbt;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

public class NBTDataDriver extends DataDriver
{
	private String baseFilePath;

	@Override
	public boolean parseConfigs(Configuration config, String worldName)
{
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

	public String getBaseBath()
	{
		return this.baseFilePath;
	}
}
