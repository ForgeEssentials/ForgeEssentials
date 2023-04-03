package com.forgeessentials.core.misc;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

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

    protected static Map<String, ForgeConfigSpec.ConfigValue<List<? extends String>>> commandAlises = new HashMap<>();//fine

    protected static Set<FEcommandData> loadedFEcommands = new HashSet<>();
    protected static Set<String> registeredFEcommands = new HashSet<>();
    protected static Set<String> registeredAiliases = new HashSet<>();

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
        //for (ForgeEssentialsCommandBuilder command : commands.values())
        //    loadCommandConfig(command);
    }

	@Override
	public ConfigData returnData() {
		return data;
	}

    private static void loadCommandConfig(FEcommandData commandData)
    {
        //Create commandConfig
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        String category = "Command-" + commandData.getData().getName();

        //load from command config names
        configBuilder.push(category);
        final ForgeConfigSpec.ConfigValue<List<? extends String>> aliases;
        aliases = configBuilder.define("aliases", (commandData.getAliases()));
        configBuilder.pop();
        commandAlises.put(commandData.getData().getName(), aliases);

        //load additional config items
        if (commandData instanceof ConfigurableCommand)
            ((ConfigurableCommand) commandData).loadConfig(configBuilder, category);

        //register the config
        FileUtils.getOrCreateDirectory(FMLPaths.GAMEDIR.get().resolve("ForgeEssentials/CommandSettings"), "ForgeEssentials/CommandSettings");
        ConfigBase.registerConfigManual(configBuilder.build(), Paths.get(ForgeEssentials.getFEDirectory()+"/CommandSettings/"+commandData.getData().getName()+".toml"),true);

        //load aliases and test for newMappings
        List<String> aliasesProperty = new ArrayList<>(commandAlises.getOrDefault(commandData.getData().getName(), aliases).get());
        if (newMappings) {
            aliasesProperty.clear();
            for(String alias : commandData.getAliases()){
                aliasesProperty.add(String.valueOf(alias));
                }
        }

        //set aliases
        commandData.setAliases(aliasesProperty);

        //bake the configs
        if (commandData instanceof ConfigurableCommand)
            ((ConfigurableCommand) commandData).bakeConfig(false);

    }

    public static void registerCommand(ForgeEssentialsCommandBuilder commandBuilder, CommandDispatcher<CommandSource> dispatcher)
    {
        registerCommand(commandBuilder, false, dispatcher);
    }

    public static void registerCommand(ForgeEssentialsCommandBuilder commandBuilder, boolean registerNow, CommandDispatcher<CommandSource> dispatcher)
    {
        FEcommandData command = new FEcommandData(commandBuilder, dispatcher);
        loadedFEcommands.add(command);
        if (useSingleConfigFile = false)
        {
            loadCommandConfig(command);
        }
        if (registerNow)
            register(command);
    }

    public static void deegisterCommand(String name)
    {
        FEcommandData command=null;
        for(FEcommandData cmd : loadedFEcommands) {
            if(cmd.getData().getName()== name) {
                command = cmd;
                break;
            }
        }

        if (command != null) {
            registeredFEcommands.remove(name);
            deregister(command);
        }
        else {
            LoggingHandler.felog.error(String.format("Tried to deregister command %s, but got a nullpointer", name));
        }
    }

    public static void registerAndLoadCommands()
    {
        LoggingHandler.felog.info("ForgeEssentials: Registering known commands");
        for (FEcommandData command : loadedFEcommands)
            if (!registeredFEcommands.contains(command.getData().getName()))
            {
                register(command);
                if (command.getData() instanceof ConfigurableCommand)
                    ((ConfigurableCommand) command.getData()).loadData();
            }
        LoggingHandler.felog.info("Registered "+Integer.toString(registeredFEcommands.size()+registeredAiliases.size())+" commands");
        //CommandFeSettings.getInstance().loadSettings();
    }

    public static void clearRegisteredCommands()
    {
        LoggingHandler.felog.debug("ForgeEssentials clearing commands");
        loadedFEcommands.clear();
        registeredFEcommands.clear();
        registeredAiliases.clear();
    }

    /**
     * Registers this command and it's permission node
     */
    public static void register(FEcommandData commandData)
    {
        
        if (ServerLifecycleHooks.getCurrentServer() == null)
            return;

        String name = commandData.getData().getName();
        if(commandData.isRegistered()) {
            LoggingHandler.felog.error(String.format("Tried to register command %s, but it is alredy registered", name));
            return;
        }
        if(commandData.getData().setExecution() == null) {
            LoggingHandler.felog.error(String.format("Tried to register command %s with null execution", name));
            return;
        }
        if (commandData.getData().isEnabled()) 
        {
            
            CommandDispatcher<CommandSource> dispatcher = commandData.getDisp();
            if(registeredFEcommands.contains(name)) {
                LoggingHandler.felog.error(String.format("Command %s already registered!", name));
                return;
            }

            dispatcher.register(commandData.getData().getMainBuilder());
            LoggingHandler.felog.info("Registered Command: "+name);
            if(commandData.getData().getBuilders() != null && !commandData.getData().getBuilders().isEmpty()) {
                try {
                    for (LiteralArgumentBuilder<CommandSource> builder : commandData.getData().getBuilders()) {
                        if(registeredAiliases.contains(builder.getLiteral())) {
                            LoggingHandler.felog.error(String.format("Command alias %s already registered!", builder.getLiteral()));
                            continue;
                        }
                        dispatcher.register(builder);
                        LoggingHandler.felog.info("Registered Command: "+name+"'s alias: "+builder.getLiteral());
                        registeredAiliases.add(builder.getLiteral());
                    }
                }catch(NullPointerException e) {
                    LoggingHandler.felog.error("Failed to register aliases for command: "+name);
                }
            }
            commandData.setRegistered(true);
            registeredFEcommands.add(name);
        }
        PermissionManager.registerCommandPermission(name, commandData.getData().getPermissionNode(), commandData.getData().getPermissionLevel());
        commandData.getData().registerExtraPermissions();
    }

    public static void deregister(FEcommandData commandData)
    {
        /*
        if (ServerLifecycleHooks.getCurrentServer() == null)
            return;
        CommandHandler cmdHandler = (CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        Map<String, ICommand> commandMap = cmdHandler.getCommands();
        Set<ICommand> commandSet = cmdHandler.commandSet;

        String commandName = getName();
        List<String> commandAliases = getAliases();
        commandSet.remove(this);
        if (commandName != null)
            commandMap.remove(commandName);
        if (commandAliases != null && !commandAliases.isEmpty())
        {
            for (String alias : commandAliases)
            {
                commandMap.remove(alias);
            }
        }*/
    }
}
