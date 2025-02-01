package com.forgeessentials.playerlogger;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.playerlogger.command.CommandPlayerlogger;
import com.forgeessentials.playerlogger.command.CommandRollback;
import com.forgeessentials.playerlogger.remote.serializer.BlockDataType;
import com.forgeessentials.playerlogger.remote.serializer.PlayerDataType;
import com.forgeessentials.playerlogger.remote.serializer.WorldDataType;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.output.LoggingHandler;



@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class)
public class ModulePlayerLogger
{

    public static final String PERM = "fe.pl";
    public static final String PERM_WAND = PERM + ".wand";
    public static final String PERM_COMMAND = PERM + ".cmd";

    private static PlayerLogger logger;

    @SuppressWarnings("unused")
    private PlayerLoggerEventHandler eventHandler;

//    @FEModule.Preconditions
//    public boolean checkLibraries()
//    {
//        String[] compulsoryLibs = { "antlr.Version", "org.dom4j.Text", "org.hibernate.annotations.common.Version", "org.hibernate.Version",
//                "org.hibernate.jpa.AvailableSettings", "javax.persistence.Version", "org.jboss.jandex.Main", "javassist.CtClass", "org.jboss.logging.Logger",
//                "org.jboss.logging.annotations.Message", "javax.transaction.Status" };
//        List<String> erroredLibs = new ArrayList<String>();
//        for (String clazz : compulsoryLibs)
//        {
//            try
//            {
//                Launch.classLoader.findClass(clazz);
//            }
//            catch (ClassNotFoundException cnfe)
//            {
//                erroredLibs.add(clazz);
//                cnfe.printStackTrace();
//            }
//        }
//        if (!erroredLibs.isEmpty())
//        {
//            LoggingHandler.felog.error("[ForgeEssentials] You are missing the following library files.");
//            for (Object error : erroredLibs.toArray())
//            {
//                System.err.println(error);
//            }
//
//            LoggingHandler.felog.error("[PlayerLogger] As the necessary files could not be loaded, PlayerLogger will be disabled.");
//            LoggingHandler.felog.error("[PlayerLogger] Please verify that the necessary files are present if you wish to use PlayerLogger.");
//            return false;
//        }
//        return true;
//    }


    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        DataManager.addDataType(new WorldDataType());
        DataManager.addDataType(new PlayerDataType());
        DataManager.addDataType(new BlockDataType());
        
        logger = new PlayerLogger();
        eventHandler = new PlayerLoggerEventHandler();
        ForgeEssentials.getConfigManager().registerLoader("PlayerLogger", new PlayerLoggerConfig());

        FECommandManager.registerCommand(new CommandRollback());
        FECommandManager.registerCommand(new CommandPlayerlogger());
        // FECommandManager.registerCommand(new CommandTestPlayerlogger());
    }

    @SubscribeEvent
    public void serverPreInit(FEModuleServerPreInitEvent e)
    {
        registerPermissions();
        logger.loadDatabase();
    }

    @SubscribeEvent
    public void serverPostInit(FEModuleServerPostInitEvent e)
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
        APIRegistry.perms.registerPermission(PERM, PermissionLevel.OP, "Player logger permisssions");
        APIRegistry.perms.registerPermission(PERM_WAND, PermissionLevel.OP, "Allow usage of player loggger wand (clock)");
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
