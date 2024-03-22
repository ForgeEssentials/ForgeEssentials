package com.forgeessentials.core.config;

import java.nio.file.Path;
import java.util.function.Function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.forgeessentials.core.ForgeEssentials;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class FEModConfig extends ModConfig
{
    private static final FEConfigFileTypeHandler FE_TOML = new FEConfigFileTypeHandler();

    private final boolean autoSave;

    public FEModConfig(ModContainer container, Type type, ForgeConfigSpec spec, String name, boolean autoSave)
    {
        super(type, spec, container, name + ".toml");
        this.autoSave = autoSave;
    }

    @Override
    public ConfigFileTypeHandler getHandler()
    {
        return FE_TOML;
    }

    public boolean isAutoSave()
    {
        return autoSave;
    }

    private static class FEConfigFileTypeHandler extends ConfigFileTypeHandler
    {

        private static Path getPath(Path configPath)
        {
            // Intercept server config path reading for FE configs and reroute it to the
            // normal config directory
            if (configPath.endsWith("serverconfig") || FMLPaths.CONFIGDIR.get() == configPath)
            {
                return ForgeEssentials.getFEDirectory().toPath();
            }
            return FMLPaths.CONFIGDIR.get();
        }

        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath)
        {
            return super.reader(getPath(configBasePath));
        }

        @Override
        public void unload(Path configBasePath, ModConfig config)
        {
            super.unload(getPath(configBasePath), config);
        }
    }
}
