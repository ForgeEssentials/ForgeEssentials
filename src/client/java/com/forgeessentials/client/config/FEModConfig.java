package com.forgeessentials.client.config;

import java.nio.file.Path;
import java.util.function.Function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class FEModConfig extends ModConfig {
	private static final FEConfigFileTypeHandler FE_TOML = new FEConfigFileTypeHandler();

	private final IFEConfig feConfig;

	public FEModConfig(ModContainer container, IFEConfig config) {
		super(config.getConfigType(), config.getConfigSpec(), container, config.getFileName() + ".toml");
		this.feConfig = config;
	}

	@Override
	public ConfigFileTypeHandler getHandler() {
		return FE_TOML;
	}

	public void clearListenerCache() {
		feConfig.clearListenerCache();
	}

	private static class FEConfigFileTypeHandler extends ConfigFileTypeHandler {

		private static Path getPath(Path configBasePath) {
			// Intercept server config path reading for FE configs and reroute it to the
			// normal config directory
			if (configBasePath.endsWith("serverconfig")) {
				return FMLPaths.CONFIGDIR.get();
			}
			return configBasePath;
		}

		@Override
		public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
			return super.reader(getPath(configBasePath));
		}

		@Override
		public void unload(Path configBasePath, ModConfig config) {
			super.unload(getPath(configBasePath), config);
		}
	}
}
