package com.forgeessentials.core.config;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.forgeessentials.commands.server.CommandHelp;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.perftools.PerfToolsModule;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.forgeessentials.core.ForgeEssentials.MODID)
public class ConfigBase {

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec MAIN_CONFIG;

    public static void registerConfig(){
        FEConfig.load(SERVER_BUILDER);
        ForgeEssentials.load(SERVER_BUILDER, true);//always true since We can't detect reloads?
        PerfToolsModule.load(SERVER_BUILDER);
        CommandHelp.load(SERVER_BUILDER);
        MAIN_CONFIG = SERVER_BUILDER.build();
    }




    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}
