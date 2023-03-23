package com.forgeessentials.playerlogger;

import java.util.Date;
import java.util.concurrent.TimeUnit;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.playerlogger.command.CommandPlayerlogger;
import com.forgeessentials.playerlogger.command.CommandRollback;
import com.forgeessentials.playerlogger.command.CommandTestPlayerlogger;
import com.forgeessentials.playerlogger.remote.serializer.BlockDataType;
import com.forgeessentials.playerlogger.remote.serializer.PlayerDataType;
import com.forgeessentials.playerlogger.remote.serializer.WorldDataType;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleRegisterCommandsEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class)
public class ModulePlayerLogger
{
    private static ForgeConfigSpec PLAYERLOGGER_CONFIG;
	public static final ConfigData data = new ConfigData("PlayerLogger", PLAYERLOGGER_CONFIG, new ForgeConfigSpec.Builder());
	
    public static final String PERM = "fe.pl";
    public static final String PERM_WAND = PERM + ".wand";
    public static final String PERM_COMMAND = PERM + ".cmd";

    private static PlayerLogger logger;

    //private PlayerLoggerEventHandler eventHandler;

    /*
    @Preconditions
    public boolean checkLibraries()
    {
        String[] compulsoryLibs = { "antlr.Version", "org.dom4j.Text", "org.hibernate.annotations.common.Version", "org.hibernate.Version",
                "org.hibernate.jpa.AvailableSettings", "javax.persistence.Version", "org.jboss.jandex.Main", "javassist.CtClass", "org.jboss.logging.Logger",
                "org.jboss.logging.annotations.Message", "javax.transaction.Status" };
        List<String> erroredLibs = new ArrayList<String>();
        for (String clazz : compulsoryLibs)
        {
            try
            {
                Launch.classLoader.findClass(clazz);
            }
            catch (ClassNotFoundException cnfe)
            {
                erroredLibs.add(clazz);
                cnfe.printStackTrace();
            }
        }
        if (!erroredLibs.isEmpty())
        {
            LoggingHandler.felog.error("[ForgeEssentials] You are missing the following library files.");
            for (Object error : erroredLibs.toArray())
            {
                System.err.println(error);
            }

            LoggingHandler.felog.error("[PlayerLogger] As the necessary files could not be loaded, PlayerLogger will be disabled.");
            LoggingHandler.felog.error("[PlayerLogger] Please verify that the necessary files are present if you wish to use PlayerLogger.");
            return false;
        }
        return true;
    }
    */

    @SubscribeEvent
    public void load(FEModuleCommonSetupEvent e)
    {
        DataManager.addDataType(new WorldDataType());
        DataManager.addDataType(new PlayerDataType());
        DataManager.addDataType(new BlockDataType());
        
        logger = new PlayerLogger();
        eventHandler = new PlayerLoggerEventHandler();
        ForgeEssentials.getConfigManager().registerSpecs("PlayerLogger", new PlayerLoggerConfig());
    }

    @SubscribeEvent
    private void registerCommands(FEModuleRegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandRollback(true), dispatcher);
        FECommandManager.registerCommand(new CommandPlayerlogger(true), dispatcher);
        
        CommandTestPlayerlogger test = new CommandTestPlayerlogger(true);
        FECommandManager.registerCommand(test, dispatcher);
        MinecraftForge.EVENT_BUS.register(test);
    }

    @SubscribeEvent
    public void serverPreInit(FEModuleServerAboutToStartEvent e)
    {
        registerPermissions();
        logger.loadDatabase();
    }

    @SubscribeEvent
    public void serverPostInit(FEModuleServerStartedEvent e)
    {
        if (PlayerLoggerConfig.logDuration > 0)
        {
            final Date startTime = new Date();
            startTime.setTime(startTime.getTime() - TimeUnit.DAYS.toMillis(PlayerLoggerConfig.logDuration));
            final String startTimeStr = startTime.toString();

            LoggingHandler.felog.info(String.format("Purging all playerlogger log data before %s. The server may lag while this is being done.", startTimeStr));
            getLogger().purgeOldData(startTime);
        }
    }

    private void registerPermissions()
    {
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Player logger permisssions");
        APIRegistry.perms.registerPermission(PERM_WAND, DefaultPermissionLevel.OP, "Allow usage of player loggger wand (clock)");
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        if (logger != null)
            logger.close();
    }

    public static PlayerLogger getLogger()
    {
        return logger;
    }

}
