package com.forgeessentials.core.config;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.forgeessentials.auth.ModuleAuth;
import com.forgeessentials.chat.Censor;
import com.forgeessentials.chat.ChatConfig;
import com.forgeessentials.chat.command.CommandTimedMessages;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.commands.server.CommandHelp;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.shop.ShopManager;
import com.forgeessentials.perftools.PerfToolsModule;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.permissions.core.ItemPermissionManager;
import com.forgeessentials.playerlogger.PlayerLoggerConfig;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.servervote.ConfigServerVote;
import com.forgeessentials.signtools.SignToolsModule;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.tickets.ModuleTickets;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.forgeessentials.core.ForgeEssentials.MODID)
public class ConfigBase {

    private static final ForgeConfigSpec.Builder MAIN_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder AUTH_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder PERMISSIONS_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder PLAYERLOGGER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder REMOTE_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder TELEPORT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder TICKETS_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CHAT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder ECONOMY_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder COMMAND_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SIGN_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVERVOTE_BUILDER = new ForgeConfigSpec.Builder();


    public static ForgeConfigSpec MAIN_CONFIG;
    public static ForgeConfigSpec AUTH_CONFIG;
    public static ForgeConfigSpec CHAT_CONFIG;
    public static ForgeConfigSpec ECONOMY_CONFIG;
    public static ForgeConfigSpec TICKETS_CONFIG;
    public static ForgeConfigSpec COMMAND_CONFIG;
    public static ForgeConfigSpec REMOTE_CONFIG;
    public static ForgeConfigSpec TELEPORT_CONFIG;
    public static ForgeConfigSpec SIGN_CONFIG;
    public static ForgeConfigSpec SERVERVOTE_CONFIG;
    public static ForgeConfigSpec PERMISSIONS_CONFIG;
    public static ForgeConfigSpec PLAYERLOGGER_CONFIG;
    
/*
    private static class ConfigFile
    {

        public ConfigFile(File path)
        {
            config = path;
        }
        public File config;

        public Set<ConfigLoader> loaders = new HashSet<>();

        public Set<ConfigLoader> loaded = new HashSet<>();

    }*/
    @SuppressWarnings("unused")
	private File rootDirectory;

    //private Map<String, ConfigFile> configFiles = new HashMap<>();

    private String mainConfigName;

    public ConfigBase(File rootDirectory, String mainConfigName)
    {
        this.rootDirectory = rootDirectory;
        this.mainConfigName = mainConfigName;
        //load(false);
    }
    
    public static void registerConfigs(){
    	//MAIN
        FEConfig.load(MAIN_BUILDER);
        ForgeEssentials.load(MAIN_BUILDER);
        PerfToolsModule.load(MAIN_BUILDER);
        CommandHelp.load(MAIN_BUILDER);
        ChatOutputHandler.load(MAIN_BUILDER);
        ItemPermissionManager.load(MAIN_BUILDER);
        MAIN_CONFIG = MAIN_BUILDER.build();
        
        //AUTH
        ModuleAuth.load(AUTH_BUILDER);
        AUTH_CONFIG = AUTH_BUILDER.build();
        
        //CHAT
        ChatConfig.load(CHAT_BUILDER);
        Censor.load(CHAT_BUILDER);
        IrcHandler.load(CHAT_BUILDER);
        CommandTimedMessages.load(CHAT_BUILDER);
        CHAT_CONFIG = CHAT_BUILDER.build();
        
        //Economy
        ModuleEconomy.load(ECONOMY_BUILDER);///NEEDS FIXING!
        ShopManager.load(ECONOMY_BUILDER);
        ECONOMY_CONFIG = ECONOMY_BUILDER.build();
        
        //Tickets
        ModuleTickets.load(TICKETS_BUILDER);
        TICKETS_CONFIG = TICKETS_BUILDER.build();
        
        //Command
        FECommandManager.load(COMMAND_BUILDER);
        COMMAND_CONFIG = COMMAND_BUILDER.build();
        
        //Remote
        ModuleRemote.load(REMOTE_BUILDER);
        REMOTE_CONFIG = REMOTE_BUILDER.build();
        
        //Teleport
        TeleportModule.load(TELEPORT_BUILDER);
        TELEPORT_CONFIG =TELEPORT_BUILDER.build();
        
        //Signs
        SignToolsModule.load(SIGN_BUILDER);
        SIGN_CONFIG = SIGN_BUILDER.build();
        
        //ServerVote
        ConfigServerVote.load(SERVERVOTE_BUILDER);
        SERVERVOTE_CONFIG = SERVERVOTE_BUILDER.build();
        
        //Permissions
        ModulePermissions.load(PERMISSIONS_BUILDER); //needs finishing DB connector
        PERMISSIONS_CONFIG = PERMISSIONS_BUILDER.build();
        
        //PlayerLogger
        PlayerLoggerConfig.load(PLAYERLOGGER_BUILDER);
        PLAYERLOGGER_CONFIG = PLAYERLOGGER_BUILDER.build();
        }
    public static void LoadConfigs(){
    	//MAIN
        FEConfig.bakeConfig(false);
        ForgeEssentials.bakeConfig(false);
        PerfToolsModule.bakeConfig(false);
        CommandHelp.bakeConfig(false);
        ChatOutputHandler.bakeConfig(false);
        ItemPermissionManager.bakeConfig(false);
        //AUTH
        ModuleAuth.bakeConfig(false);
        //CHAT
        ChatConfig.bakeConfig(false);
        Censor.bakeConfig(false);
        IrcHandler.bakeConfig(false);
        CommandTimedMessages.bakeConfig(false);
        //Economy
        ModuleEconomy.bakeConfig(false);///////HELP!
        ShopManager.bakeConfig(false);
        //Tickets
        ModuleTickets.bakeConfig(false);
        //Command
        FECommandManager.bakeConfig(false);
        //Remote
        ModuleRemote.bakeConfig(false);
        //Teleport
        TeleportModule.bakeConfig(false);
        //Signs
        SignToolsModule.bakeConfig(false);
        //ServerVote
        ConfigServerVote.bakeConfig(false);
        //Permissions
        ModulePermissions.bakeConfig(false); //needs finishing DB connector
        //PlayerLogger
        PlayerLoggerConfig.bakeConfig(false);
    }
    public static void SaveConfigs(){
    	ModuleTickets.save();
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
    
    public String getMainConfigName()
    {
        return mainConfigName;
    }
    
    /*
    private ConfigFile getConfigFile(String configName)
    {
        ConfigFile loaders = configFiles.get(configName);
        if (loaders == null)
        {
            loaders = new ConfigFile(new File(this.rootDirectory, configName + ".toml"));
            configFiles.put(configName, loaders);
        }
        return loaders;
    }*/
}
