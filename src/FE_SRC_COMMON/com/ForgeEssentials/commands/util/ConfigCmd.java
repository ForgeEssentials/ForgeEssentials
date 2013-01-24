package com.ForgeEssentials.commands.util;

import java.io.File;
import java.util.Set;

import javax.security.auth.callback.ConfirmationCallback;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.commands.CommandEnderchest;
import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandRules;
import com.ForgeEssentials.commands.CommandVirtualchest;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ConfigCmd extends ModuleConfigBase
{
	public Configuration config;
	
	public ConfigCmd(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);

		config.addCustomCategoryComment("general", "General Commands configuration.");
		ModuleCommands.removeDuplicateCommands = config.get("general", "removeDuplicateCommands", true,
				"Remove commands from the list if they already exist outside of FE.").getBoolean(true);
		config.save();
	}

	@Override
	public void forceSave()
	{
		// TODO: may have problems..
		String path = CommandRules.rulesFile.getPath();
		path = path.replace(ModuleCommands.cmddir.getPath(), "");
		
		config.addCustomCategoryComment("general", "General Commands configuration.");
		config.get("general", "removeDuplicateCommands", true, "Remove commands from the list if they already exist outside of FE.").value = ""
				+ ModuleCommands.removeDuplicateCommands;
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		ModuleCommands.removeDuplicateCommands = config.get("general", "removeDuplicateCommands", true).getBoolean(true);
		
		config.save();
		
		CommandRegistrar.commandConfigs(config);
	}
}
