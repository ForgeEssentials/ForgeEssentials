package com.forgeessentials.teleport.util;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.teleport.TeleportModule;

public class ConfigTeleport extends ModuleConfigBase{

	private Configuration config;
	
	public ConfigTeleport(File file) {
		super(file);
	}

	@Override
	public void init() {
		config = new Configuration (file, true);
		TeleportModule.timeout = config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
		config.save();

	}

	@Override
	public void forceSave() {
		config = new Configuration (file, true);
		config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").set(TeleportModule.timeout);
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender) {
		config = new Configuration (file, true);
		TeleportModule.timeout = config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
		config.save();
	}
	
}