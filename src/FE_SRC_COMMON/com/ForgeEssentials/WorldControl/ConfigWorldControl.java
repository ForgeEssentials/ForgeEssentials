package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;

public class ConfigWorldControl extends ModuleConfigBase
{
	private Configuration config;
	public static int blocksPerTick;

	public ConfigWorldControl(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);
		config.load();

		Property prop = config.get("WorldControl", "BlocksPerTick", 20);
		prop.comment = "Specifies the maximum blocks/tick that can be changed via the WorldControl functions. Powerful computers may set it higher, servers may want to keep it lower.";
		blocksPerTick = prop.getInt();
		OutputHandler.SOP("Setting blocks/tick to: " + blocksPerTick);

		config.save();
	}

	@Override
	public void forceSave()
	{
		Property prop = config.get("WorldControl", "BlocksPerTick", 20);
		prop.comment = "Specifies the maximum blocks/tick that can be changed via the WorldControl functions. Powerful computers may set it higher, servers may want to keep it lower.";
		prop.value = ""+blocksPerTick;

		config.save();

	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		blocksPerTick = config.get("WorldControl", "BlocksPerTick", 20).getInt();
		OutputHandler.SOP("Setting blocks/tick to: " + blocksPerTick);
	}

}
