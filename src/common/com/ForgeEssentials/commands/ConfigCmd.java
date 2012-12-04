package com.ForgeEssentials.commands;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigCmd {
	public static final File cmddir = new File(ForgeEssentials.FEDIR, "commands/");
	public static final File cmdconfig = new File(cmddir, "commands.cfg");
	public final Configuration config;
	public ConfigCmd()
	{
		config = new Configuration(cmdconfig, true);
		config.addCustomCategoryComment("Commands", "Configure ForgeEssentials Commands.");

		Property prop = config.get("Commands", "motd", "Welcome to a server running ForgeEssentials");
		prop.comment = "Specify the message that greets players when they log in to your server.";
		CommandMotd.motd = prop.value;

		prop = config.get("Commands", "RulesFile", "rules.txt");
		prop.comment = "Specify the file where the rules will read from and written to. This path is relative to this folder.";
		CommandRules.rulesFile = new File(cmddir, prop.value);
		
		prop = config.get("Commands", "backupName", "%world_%month/%day_%hour:%min"); 
		prop.comment = "The name config for the backup zip. You can use the following variables: %day, %month, %year, %hour, %min, %world";
		CommandBackup.backupName = prop.value;
		
		prop = config.get("Commands", "backupdir", "backups/");
		prop.comment = "The path to the backup folder.";
		CommandBackup.backupdir = ForgeEssentials.fedirloc + prop.value;
		config.save();
	}

}
