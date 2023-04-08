package com.forgeessentials.core.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;

public class FEAliasesManager{

	private static ForgeConfigSpec Ailas_CONFIG;
	private static final ConfigData dataA = new ConfigData("FEAliases", Ailas_CONFIG, new ForgeConfigSpec.Builder());
	
	private static Map<String, ForgeConfigSpec.ConfigValue<List<? extends String>>> commandAlises = new HashMap<>();

	public static void loadCommandConfig(FECommandData commandData)
    {
        final ForgeConfigSpec.ConfigValue<List<? extends String>> aliases;
        String commandCat = "Command-" + commandData.getData().getName();
        //LoggingHandler.felog.info(commandCat);

        //load from command config names
        dataA.getSpecBuilder().push(commandCat);
        aliases = dataA.getSpecBuilder().defineList("aliases", (commandData.getData().getAliases()), ConfigBase.stringValidator);
        dataA.getSpecBuilder().pop();

        commandAlises.put(commandData.getData().getName(), aliases);
    }

    public static void bakeCommandConfig(FECommandData commandData) {
    	//LoggingHandler.felog.info(commandData.getData().getName());
        //load aliases and test for newMappings
        List<String> aliasesProperty = new ArrayList<>(commandAlises.get(commandData.getData().getName()).get());
        if (ModuleCommands.newMappings) {
            aliasesProperty.clear();
            for(String alias : commandData.getData().getAliases()){
                aliasesProperty.add(String.valueOf(alias));
                }
            
            final ForgeConfigSpec.ConfigValue<List<? extends String>> string = commandAlises.get(commandData.getData().getName());
            string.set(aliasesProperty);
            commandAlises.put(commandData.getData().getName(), string);
        }
        LoggingHandler.felog.info(aliasesProperty.toString());
        //set aliases
        commandData.getData().setAliases(aliasesProperty);
    }

	public static ConfigData returnData() 
	{
		LoggingHandler.felog.info("Returning data");
		return dataA;
	}
}
