package com.ForgeEssentials.mobControl;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigMobControl extends ModuleConfigBase
{
	public Configuration	config;
	String					cat	= "MobControl";

	public ConfigMobControl(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		OutputHandler.finer("Loading MobControl Config");
		config = new Configuration(file, true);

		config.save();
	}

	@Override
	public void forceSave()
	{

	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		config.save();
	}
}