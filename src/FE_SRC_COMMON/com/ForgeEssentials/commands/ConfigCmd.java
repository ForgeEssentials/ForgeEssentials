package com.ForgeEssentials.commands;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;

public class ConfigCmd {
	public static final File cmddir = new File(ForgeEssentials.FEDIR, "commands/");
	public static final File cmdconfig = new File(cmddir, "commands.cfg");
	public final Configuration config;
	public ConfigCmd()
	{
		config = new Configuration(cmdconfig, true);
		
		CommandMotd.motd = config.get(config.CATEGORY_GENERAL, "motd", "Welcome to a server running ForgeEssentials", "Specify the message that greets players when they log in to your server.").value;
		CommandRules.rulesFile = new File(cmddir, config.get(config.CATEGORY_GENERAL, "RulesFile", "rules.txt", "Specify the file where the rules will read from and written to. This path is relative to this folder.").value);
		CommandEnderchest.useAlias = config.get(config.CATEGORY_GENERAL, "useEnderChestAlias", true, "Use the alisa '/echest' for the command '/enderchest'.").getBoolean(true);
		CommandVirtualchest.useAlias = config.get(config.CATEGORY_GENERAL, "useVirtualChestAlias", true, "Use the alisa '/vchest' for the command '/virtualchest'.").getBoolean(true);
		CommandVirtualchest.size = config.get(config.CATEGORY_GENERAL, "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").getInt(6) * 9;
		CommandVirtualchest.name = config.get(config.CATEGORY_GENERAL, "VirtualChestName", "Vault 13", "Don't use special stuff....").value;
		
		config.addCustomCategoryComment("Backups", "Configure the backup system.");
		CommandBackup.backupName = config.get("Backups", "name", "%world-%year-%month-%day_%hour-%min", "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world").value;
		CommandBackup.backupdir = ForgeEssentials.fedirloc + config.get("Backups", "folder", "backups/", "The path to the backup folder.").value;
		
		config.save();
	}

}
