package com.forgeessentials.playerlogger;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.moduleLauncher.config.ConfigManager;
import com.forgeessentials.playerlogger.network.S2PacketPlayerLogger;
import com.forgeessentials.playerlogger.network.S3PacketRollback;
import com.forgeessentials.playerlogger.rollback.CommandPl;
import com.forgeessentials.playerlogger.rollback.CommandRollback;
import com.forgeessentials.playerlogger.rollback.EventHandler;
import com.forgeessentials.playerlogger.types.BlockChangeType;
import com.forgeessentials.playerlogger.types.CommandType;
import com.forgeessentials.playerlogger.types.LogType;
import com.forgeessentials.playerlogger.types.PlayerTrackerType;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.commons.selections.WorldPoint;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class)
public class ModulePlayerLogger {

    public static String url;
    public static String username;
    public static String password;
    public static boolean ragequitOn;
    public static boolean enable = false;
    public static EventLogger eLogger;
    public static HashSet<LogType> logTypes = new HashSet<LogType>();

    static
    {
        logTypes.add(new PlayerTrackerType());
        logTypes.add(new CommandType());
        logTypes.add(new BlockChangeType());
    }

    private static Connection connection;

    public ModulePlayerLogger()
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static Connection getConnection()
    {
        try
        {
            if (connection.isClosed())
            {
                connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return connection;
    }

    public static void error(Exception e)
    {
        if (ragequitOn)
        {
            MinecraftServer.getServer().stopServer();
        }
        else
        {
            OutputHandler.felog.severe("PlayerLogger error: " + e.getLocalizedMessage());
        }
    }

    public static ArrayList<com.forgeessentials.playerlogger.BlockChange> getBlockChangesWithinParameters(String playername, boolean undo, int timeBack,
            WorldPoint p, int rad)
    {
        ArrayList<com.forgeessentials.playerlogger.BlockChange> data = new ArrayList<com.forgeessentials.playerlogger.BlockChange>();
        try
        {
            Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
            Statement st = connection.createStatement();

            String sql = "SELECT * FROM  `blockChange` WHERE  `player` LIKE  '" + playername + "'";

            if (timeBack != 0)
            {
                Date date = new Date();
                Timestamp time = new Timestamp(date.getTime());
                // Hours, mins, sec, nano
                time.setNanos(time.getNanos() - (timeBack * 60 * 60 * 1000 * 1000));
                sql = sql + " AND `time` = '" + time.toString() + "'";
            }

            if (p != null && rad != 0)
            {
                sql = sql + " AND `Dim` = " + p.getDimension();
                sql = sql + " AND `X` BETWEEN " + (p.getX() - rad) + " AND " + (p.getX() + rad);
                sql = sql + " AND `Z` BETWEEN " + (p.getZ() - rad) + " AND " + (p.getZ() + rad);
            }

            if (undo)
            {
                sql = sql + " ORDER BY time ASC";
            }
            else
            {
                sql = sql + " ORDER BY time DESC";
            }

            st.execute(sql);
            ResultSet rs = st.getResultSet();

            while (rs.next())
            {
                data.add(new com.forgeessentials.playerlogger.BlockChange(rs.getInt("X"), rs.getInt("Y"), rs.getInt("Z"), rs.getInt("dim"),
                        BlockChangeType.blockChangeLogCategory.valueOf(rs.getString("category")).ordinal(), rs.getString("block"), rs.getBlob("te")));
            }

            rs.close();
            st.close();
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        ForgeEssentials.getConfigManager().registerLoader("PlayerLogger", new ConfigPlayerLogger());
        if (!enable)
        {
            ModuleLauncher.instance.unregister("PlayerLogger");
        }
        OutputHandler.felog.info("PlayerLogger module is enabled. Loading...");
        FunctionHelper.netHandler.registerMessage(S2PacketPlayerLogger.class, S2PacketPlayerLogger.class, 2, Side.CLIENT);
        FunctionHelper.netHandler.registerMessage(S3PacketRollback.class, S3PacketRollback.class, 3, Side.CLIENT);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        for (String name : EventLogger.exempt_groups)
        {
            if (!APIRegistry.perms.groupExists(name))
            {
                throw new RuntimeException("Group '" + name + "' doesn't exist.");
            }
        }
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException error)
        {
            throw new RuntimeException("Could not find MySQL JDBC Driver.");
        }
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandPl());
        FunctionHelper.registerServerCommand(new CommandRollback());
        try
        {
            connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
            Statement s = connection.createStatement();

            /*
             * if (DEBUG && false) { for (logEntry type : logTypes) { s.execute("DROP TABLE IF EXISTS " + type.getName()); } }
             */

            for (LogType type : logTypes)
            {
                s.execute(type.getTableCreateSQL());
            }

            s.close();
            eLogger = new EventLogger();
        }
        catch (SQLException e1)
        {
            OutputHandler.felog.info("Could not connect to database! Wrong credentials?");
            OutputHandler.felog.info(e1.getMessage());
            e1.printStackTrace();
        }
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        try
        {
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000 * 5);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        connection.close();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (Exception ex)
        {
            OutputHandler.felog.warning("WARNING! MySQLConnector for playerLogger failed!");
            ex.printStackTrace();
        }
    }
}
