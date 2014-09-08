package com.forgeessentials.playerlogger;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.PreInit;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerInit;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerStop;
import com.forgeessentials.playerlogger.rollback.CommandPl;
import com.forgeessentials.playerlogger.rollback.CommandRollback;
import com.forgeessentials.playerlogger.rollback.EventHandler;
import com.forgeessentials.playerlogger.types.blockChangeLog;
import com.forgeessentials.playerlogger.types.commandLog;
import com.forgeessentials.playerlogger.types.logEntry;
import com.forgeessentials.playerlogger.types.playerTrackerLog;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class, configClass = ConfigPlayerLogger.class)
public class ModulePlayerLogger {

    @FEModule.Config
    public static ConfigPlayerLogger config;

    public static String url;
    public static String username;
    public static String password;
    public static boolean ragequitOn;
    public static boolean enable = false;
    public static EventLogger eLogger;
    public static HashSet<logEntry> logTypes = new HashSet<logEntry>();

    static
    {
        logTypes.add(new playerTrackerLog());
        logTypes.add(new commandLog());
        logTypes.add(new blockChangeLog());
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

    public static ArrayList<blockChange> getBlockChangesWithinParameters(String username, boolean undo, int timeBack, WorldPoint p, int rad)
    {
        ArrayList<blockChange> data = new ArrayList<blockChange>();
        try
        {
            Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
            Statement st = connection.createStatement();

            String sql = "SELECT * FROM  `blockChange` WHERE  `player` LIKE  '" + username + "'";

            if (timeBack != 0)
            {
                Date date = new Date();
                Timestamp time = new Timestamp(date.getTime());
                //                                 Hours,  mins, sec, nano
                time.setNanos(time.getNanos() - (timeBack * 60 * 60 * 1000 * 1000));
                sql = sql + " AND `time` = '" + time.toString() + "'";
            }

            if (p != null && rad != 0)
            {
                sql = sql + " AND `Dim` = " + p.dim;
                sql = sql + " AND `X` BETWEEN " + (p.x - rad) + " AND " + (p.x + rad);
                sql = sql + " AND `Z` BETWEEN " + (p.z - rad) + " AND " + (p.z + rad);
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
                data.add(new blockChange(rs.getInt("X"), rs.getInt("Y"), rs.getInt("Z"), rs.getInt("dim"),
                        blockChangeLog.blockChangeLogCategory.valueOf(rs.getString("category")).ordinal(), rs.getString("block"), rs.getBlob("te")));
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

    @PreInit
    public void preLoad(FEModulePreInitEvent e)
    {
        if (!enable)
        {
            return;
        }
        OutputHandler.felog.info("PlayerLogger module is enabled. Loading...");
    }

    @Init
    public void load(FEModuleInitEvent e)
    {
        for (String name : EventLogger.exempt_groups)
        {
            if (APIRegistry.perms.getGroupForName(name) == null)
            {
                throw new RuntimeException("Group '" + name + "' doesn't exist. Used in " + config.getFile().getName());
            }
        }

        if (!enable)
        {
            return;
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

    @ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        if (!enable)
        {
            return;
        }
        e.registerServerCommand(new CommandPl());
        e.registerServerCommand(new CommandRollback());
        try
        {
            connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
            Statement s = connection.createStatement();

			/*
             * if (DEBUG && false) { for (logEntry type : logTypes) {
			 * s.execute("DROP TABLE IF EXISTS " + type.getName()); } }
			 */

            for (logEntry type : logTypes)
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

    @ServerStop
    public void serverStopping(FEModuleServerStopEvent e)
    {
        if (!enable)
        {
            return;
        }
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
