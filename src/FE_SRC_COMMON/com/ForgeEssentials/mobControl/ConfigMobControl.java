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

		updateGlobal();
		
		config.save();
	}

	@Override
	public void forceSave()
	{	
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		updateGlobal();
		
		config.save();
	}

	public void updateGlobal()
	{
		String subcat = cat + ".gobalSpawn";
		config.addCustomCategoryComment(subcat, "Use this to disable spawning of sertain mobs");
		for(String name : ModuleMobControl.nameList.keySet())
		{
			ModuleMobControl.nameList.put(name, config.get(subcat, name, true).getBoolean(true));
		}
		config.save();
	}
}