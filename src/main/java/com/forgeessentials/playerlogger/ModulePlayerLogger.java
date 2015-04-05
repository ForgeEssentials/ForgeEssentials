package com.forgeessentials.playerlogger;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.playerlogger.network.S2PacketPlayerLogger;
import com.forgeessentials.playerlogger.network.S3PacketRollback;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class)
public class ModulePlayerLogger {

    private PlayerLogger logger;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        logger = new PlayerLogger();
        ForgeEssentials.getConfigManager().registerLoader("PlayerLogger", new PlayerLoggerConfig());
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        FunctionHelper.netHandler.registerMessage(S2PacketPlayerLogger.class, S2PacketPlayerLogger.class, 2, Side.CLIENT);
        FunctionHelper.netHandler.registerMessage(S3PacketRollback.class, S3PacketRollback.class, 3, Side.CLIENT);
    }

    @SubscribeEvent
    public void serverPreInit(FEModuleServerPreInitEvent e)
    {
        logger.loadDatabase();
       
        //FunctionHelper.registerServerCommand(new CommandPl());
        //FunctionHelper.registerServerCommand(new CommandRollback());
//        try
//        {
//            connection = DriverManager.getConnection(ModuleLogger.url, ModuleLogger.username, ModuleLogger.password);
//            Statement s = connection.createStatement();
//
//            /*
//             * if (DEBUG && false) { for (logEntry type : logTypes) { s.execute("DROP TABLE IF EXISTS " + type.getName()); } }
//             */
//
//            for (LogType type : logTypes)
//            {
//                s.execute(type.getTableCreateSQL());
//            }
//
//            s.close();
//            eLogger = new EventLogger();
//        }
//        catch (SQLException e1)
//        {
//            OutputHandler.felog.info("Could not connect to database! Wrong credentials or no credentials!");
//            OutputHandler.felog.info(e1.getMessage());
//            e1.printStackTrace();
//            ModuleLauncher.instance.unregister("PlayerLogger");
//        }
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        logger.close();
    }

}
