package com.forgeessentials.core.config;

public interface ConfigSaver extends ConfigLoader {

	void save(boolean reload);

}
