package com.forgeessentials.core.misc;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;

public class FECommandManager implements ConfigLoader
{
    private static ForgeConfigSpec COMMAND_CONFIG;
	private static final ConfigData data = new ConfigData("Commands", COMMAND_CONFIG, new ForgeConfigSpec.Builder());
	
    public static interface ConfigurableCommand
    {

        public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category);

        public void loadData();

        public void bakeConfig(boolean reload);
        
    }

    public static final int COMMANDS_VERSION = 5;

    protected static Map<String, ForgeEssentialsCommandBuilder> commands = new HashMap<>();

    protected static Map<String, ForgeConfigSpec.ConfigValue<List<String>>> commandAlises = new HashMap<>();

    protected static Set<ForgeEssentialsCommandBuilder> registeredCommands = new HashSet<>();

    protected static boolean useSingleConfigFile = false;
    
    protected static boolean newMappings;

    public FECommandManager()
    {
        ForgeEssentials.getConfigManager().registerSpecs("Commands", this);
    }

    static ForgeConfigSpec.IntValue FECversion;

	@Override
	public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push("CommandsConfig");
        FECversion = BUILDER.defineInRange("version", COMMANDS_VERSION, 0, Integer.MAX_VALUE);
        BUILDER.pop();
    }

	@Override
	public void bakeConfig(boolean reload)
    {
        if (FECversion.get() < COMMANDS_VERSION)
        {
            newMappings = true;
            FECversion.set(COMMANDS_VERSION);
        }
        for (ForgeEssentialsCommandBuilder command : commands.values())
            loadCommandConfig(command);
    }

	@Override
	public ConfigData returnData() {
		return data;
	}

    private static void loadCommandConfig(ForgeEssentialsCommandBuilder command)
    {
        //Create commandConfig
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        String category = "Commands_" + command.getName();

        //load from command config names
        configBuilder.push(category);
        final ForgeConfigSpec.ConfigValue<List<String>> aliases;
        aliases = configBuilder.define("aliases", new ArrayList<String>(Arrays.asList(command.getDefaultAliases())));
        configBuilder.pop();
        commandAlises.put(command.getName(), aliases);

        //load additional config items
        if (command instanceof ConfigurableCommand)
            ((ConfigurableCommand) command).loadConfig(configBuilder, category);

        //register the config
        FileUtils.getOrCreateDirectory(FMLPaths.GAMEDIR.get().resolve("ForgeEssentials/CommandSettings"), "ForgeEssentials/CommandSettings");
        ConfigBase.registerConfigManual(configBuilder.build(), Paths.get(ForgeEssentials.getFEDirectory()+"/CommandSettings/"+command.getName()+".toml"),true);

        //load aliases and test for newMappings
        List<String> aliasesProperty = commandAlises.getOrDefault(command.getName(),  aliases).get();
        if (newMappings) {
            aliasesProperty.clear();
            for(String alias : command.getDefaultAliases()){
                aliasesProperty.add(String.valueOf(alias));
                }
        }

        //set aliases
        command.setAliases(aliasesProperty);

        //bake the configs
        if (command instanceof ConfigurableCommand)
            ((ConfigurableCommand) command).bakeConfig(false);

    }

    public static void registerCommand(ForgeEssentialsCommandBuilder command)
    {
        registerCommand(command, false);
    }

    public static void registerCommand(ForgeEssentialsCommandBuilder command, boolean registerNow)
    {
        commands.put(command.getName(), command);
        if (useSingleConfigFile = false)
        {
            loadCommandConfig(command);
        }
        if (registerNow)
            command.register();
    }

    public static void deegisterCommand(String name)
    {
        ForgeEssentialsCommandBuilder command = commands.remove(name);
        if (command != null)
            command.deregister();
    }

    public static void registerCommands()
    {
        for (ForgeEssentialsCommandBuilder command : commands.values())
            if (!registeredCommands.contains(command))
            {
                registeredCommands.add(command);
                command.register();
                if (command instanceof ConfigurableCommand)
                    ((ConfigurableCommand) command).loadData();
            }
        CommandFeSettings.getInstance().loadSettings();
    }

    public static void clearRegisteredCommands()
    {
        registeredCommands.clear();
    }
}
