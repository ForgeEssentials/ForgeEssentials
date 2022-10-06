package com.forgeessentials.core.misc;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigSaver;

public class FECommandManager implements ConfigSaver
{
    private static ForgeConfigSpec COMMAND_CONFIG;
	private static final ConfigData data = new ConfigData("Commands", COMMAND_CONFIG, new ForgeConfigSpec.Builder());
	
    public static interface ConfigurableCommand
    {

        public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category);

        public void loadData();

        public void bakeConfig(boolean reload);
        
    }

    public static final int COMMANDS_VERSION = 4;

    protected static Map<String, ForgeEssentialsCommandBase> commands = new HashMap<>();

    protected static Set<ForgeEssentialsCommandBase> registeredCommands = new HashSet<>();

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
        for (ForgeEssentialsCommandBase command : commands.values())
            loadCommandConfig(command);
    }

	@Override
	public ConfigData returnData() {
		return data;
	}

	@Override
	public void save(boolean reload) {
		// TODO Auto-generated method stub
		
	}

    private static void loadCommandConfig(ForgeEssentialsCommandBase command)
    {

        ForgeConfigSpec.Builder configBuilder;
    	
        String category = "Commands_" + command.getName();
        Property aliasesProperty = config.get(category, "aliases", command.getDefaultAliases());

        if (newMappings)
            aliasesProperty.set(command.getDefaultAliases());
        command.setAliases(aliasesProperty.getStringList());

        if (command instanceof ConfigurableCommand)
            ((ConfigurableCommand) command).loadConfig(configBuilder, category);
        FileUtils.getOrCreateDirectory(FMLPaths.GAMEDIR.get().resolve("ForgeEssentials/CommandSettings"), "ForgeEssentials/CommandSettings");
        ConfigBase.registerConfigManual(configBuilder.build(), Paths.get(ForgeEssentials.getFEDirectory()+"/CommandSettings/"+command.getName()+".toml"),true);
    }

    public static void registerCommand(ForgeEssentialsCommandBase command)
    {
        registerCommand(command, false);
    }

    public static void registerCommand(ForgeEssentialsCommandBase command, boolean registerNow)
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
        ForgeEssentialsCommandBase command = commands.remove(name);
        if (command != null)
            command.deregister();
    }

    public static void registerCommands()
    {
    	bakeConfig(true);
        for (ForgeEssentialsCommandBase command : commands.values())
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
