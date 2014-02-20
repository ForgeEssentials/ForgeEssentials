package com.forgeessentials.WorldControl;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.util.OutputHandler;

public class ConfigWorldControl extends ModuleConfigBase
{
	private Configuration	config;
	public static int		blocksPerTick;

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
		OutputHandler.felog.info("Setting blocks/tick to: " + blocksPerTick);

		config.save();
	}

	@Override
	public void forceSave()
	{
		Property prop = config.get("WorldControl", "BlocksPerTick", 20);
		prop.comment = "Specifies the maximum blocks/tick that can be changed via the WorldControl functions. Powerful computers may set it higher, servers may want to keep it lower.";
		prop.set(blocksPerTick);

		config.save();

	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		blocksPerTick = config.get("WorldControl", "BlocksPerTick", 20).getInt();
		OutputHandler.felog.fine("Setting blocks/tick to: " + blocksPerTick);
	}

}
