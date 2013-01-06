package com.ForgeEssentials.commands.util;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.commands.CommandEnderchest;
import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandRules;
import com.ForgeEssentials.commands.CommandVirtualchest;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IModuleConfig;

public class ConfigCmd implements IModuleConfig
{
	public static final File cmddir = new File(ForgeEssentials.FEDIR,
			"commands/");
	public static final File cmdconfig = new File(cmddir, "commands.cfg");
	public Configuration config;

	@Override
	public void setGenerate(boolean generate)
	{

	}

	@Override
	public void init()
	{
		config = new Configuration(cmdconfig, true);

		config.addCustomCategoryComment("general",
				"General Commands configuration.");
		ModuleCommands.removeDuplicateCommands = config
				.get("general", "removeDuplicateCommands", true,
						"Remove commands from the list if they already exist outside of FE.")
				.getBoolean(true);
		CommandMotd.motd = config
				.get("general", "motd",
						"Welcome to a server running ForgeEssentials",
						"Specify the message that greets players when they log in to your server.").value;
		CommandRules.rulesFile = new File(
				cmddir,
				config.get(
						"general",
						"RulesFile",
						"rules.txt",
						"Specify the file where the rules will read from and written to. This path is relative to this folder.").value);
		CommandEnderchest.useAlias = config.get("general",
				"useEnderChestAlias", true,
				"Use the alisa '/echest' for the command '/enderchest'.")
				.getBoolean(true);
		CommandVirtualchest.useAlias = config.get("general",
				"useVirtualChestAlias", true,
				"Use the alias '/vchest' for the command '/virtualchest'.")
				.getBoolean(true);
		CommandVirtualchest.size = config.get("general", "VirtualChestRows", 6,
				"1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).")
				.getInt(6) * 9;
		CommandVirtualchest.name = config.get("general", "VirtualChestName",
				"Vault 13", "Don't use special stuff....").value;
		config.save();
	}

	@Override
	public void forceSave()
	{
		// TODO: may have problems..
		String path = CommandRules.rulesFile.getPath();
		path = path.replace(cmddir.getPath(), "");
		config.get(
				"general",
				"RulesFile",
				"rules.txt",
				"Specify the file where the rules will read from and written to. This path is relative to this folder.").value = path;

		config.addCustomCategoryComment("general",
				"General Commands configuration.");
		config.get("general", "removeDuplicateCommands", true,
				"Remove commands from the list if they already exist outside of FE.").value = ""
				+ ModuleCommands.removeDuplicateCommands;
		config.get("general", "motd",
				"Welcome to a server running ForgeEssentials",
				"Specify the message that greets players when they log in to your server.").value = CommandMotd.motd;
		config.get("general", "useEnderChestAlias", true,
				"Use the alisa '/echest' for the command '/enderchest'.").value = ""
				+ CommandEnderchest.useAlias;
		config.get("general", "useVirtualChestAlias", true,
				"Use the alias '/vchest' for the command '/virtualchest'.").value = ""
				+ CommandVirtualchest.useAlias;
		config.get("general", "VirtualChestRows", 6,
				"1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").value = ""
				+ CommandVirtualchest.size / 9;
		config.get("general", "VirtualChestName", "Vault 13",
				"Don't use special stuff....").value = CommandVirtualchest.name;
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		ModuleCommands.removeDuplicateCommands = config.get("general",
				"removeDuplicateCommands", true).getBoolean(true);
		CommandMotd.motd = config.get("general", "motd",
				"Welcome to a server running ForgeEssentials").value;
		CommandRules.rulesFile = new File(cmddir, config.get("general",
				"RulesFile", "rules.txt").value);
		CommandEnderchest.useAlias = config.get("general",
				"useEnderChestAlias", true).getBoolean(true);
		CommandVirtualchest.useAlias = config.get("general",
				"useVirtualChestAlias", true).getBoolean(true);
		CommandVirtualchest.size = config.get("general", "VirtualChestRows", 6)
				.getInt(6) * 9;
		CommandVirtualchest.name = config.get("general", "VirtualChestName",
				"Vault 13").value;
	}

	@Override
	public File getFile()
	{
		return cmdconfig;
	}

}
