package com.forgeessentials.core.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class FEAliasesManager implements ConfigLoader{

	private static ForgeConfigSpec Ailas_CONFIG;
	private static final ConfigData dataA = new ConfigData("FEAliases", Ailas_CONFIG, new ForgeConfigSpec.Builder());
	
	private static Map<String, ForgeConfigSpec.ConfigValue<List<? extends String>>> commandAlises = new HashMap<>();

	public static void loadCommandConfig(FECommandData commandData)
    {
        final ForgeConfigSpec.ConfigValue<List<? extends String>> aliases;
        String commandCat = "Command-" + commandData.getData().getName();

        //load from command config names
        dataA.getSpecBuilder().push(commandCat);
        aliases = dataA.getSpecBuilder().defineList("aliases", (commandData.getData().getAliases()), ConfigBase.stringValidator);
        dataA.getSpecBuilder().pop();

        commandAlises.put(commandData.getData().getName(), aliases);

        //load additional config items
        if (commandData.getData() instanceof ConfigurableCommand)
            ((ConfigurableCommand) commandData.getData()).loadConfig(dataA.getSpecBuilder(), commandCat);
    }

    public static void bakeCommandConfig(FECommandData commandData) {
        //load aliases and test for newMappings
        List<String> aliasesProperty = new ArrayList<>(commandAlises.get(commandData.getData().getName()).get());
        if (FECommandManager.newMappings) {
            aliasesProperty.clear();
            for(String alias : commandData.getData().getAliases()){
                aliasesProperty.add(String.valueOf(alias));
                }
            
            final ForgeConfigSpec.ConfigValue<List<? extends String>> string = commandAlises.get(commandData.getData().getName());
            string.set(aliasesProperty);
            commandAlises.put(commandData.getData().getName(), string);
        }

        //set aliases
        commandData.getData().setAliases(aliasesProperty);

        //bake the configs
        if (commandData.getData() instanceof ConfigurableCommand)
            ((ConfigurableCommand) commandData.getData()).bakeConfig(false);
    }

	@Override
	public void load(Builder BUILDER, boolean isReload) {		
	}

	@Override
	public void bakeConfig(boolean reload) {
	}

	@Override
	public ConfigData returnData() {
		return dataA;
	}
}
