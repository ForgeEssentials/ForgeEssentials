package com.ForgeEssentials.WorldControl.weintegration;

import com.sk89q.worldedit.util.PropertiesConfiguration;
import java.io.File;

public class LocalConfig extends PropertiesConfiguration {

	public File mcdir;

	public LocalConfig(File mcdir) {
		super(new File(mcdir, "mods/console/worldedit.properties"));
		this.mcdir = mcdir;
	}

	@Override
	public File getWorkingDirectory() {
		return this.mcdir;
	}
}