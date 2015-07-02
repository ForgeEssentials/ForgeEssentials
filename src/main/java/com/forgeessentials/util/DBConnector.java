package com.forgeessentials.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Throwables;

public class DBConnector
{
    EnumDBType loadedType;
    private final DBConnector fallback;
    public final String name;
    private final EnumDBType dType;
    private EnumDBType type;
    private EnumDBType tempType;
    private final String dbDefault;
    private final String dbFileDefault;
    private boolean useParent;
    private HashMap<EnumDBType, HashMap<String, Property>> data;

    /**
     * @param name
     *            a name for the DB connector. to be used in Logging.
     * @param fallback
     *            The DBConnector from which to take information for a given type if loading that type from this config
     *            fails.
     * @param dType
     *            the default database type to use
     * @param dbDefault
     *            the default name for remote databases
     * @param dbFileDefault
     *            the default path for file databases. Relative to FEDIR
     * @paramuseFallbac if the Fallback should be used for remote Databases
     */
    public DBConnector(String name, DBConnector fallback, EnumDBType dType, String dbDefault, String dbFileDefault, boolean useFallback)
    {
        this.name = name;
        this.fallback = fallback;
        this.dType = type = tempType = dType;
        this.dbDefault = dbDefault;
        this.dbFileDefault = dbFileDefault;
        data = new HashMap<EnumDBType, HashMap<String, Property>>();
        useParent = useFallback;
    }

    /**
     * Forcibly writes everything to the config. the config's save() method is not called.
     *
     * @param config
     * @param category
     *            the category where everything regarding this connector will be.
     */
    public void write(Configuration config, String cat)
    {
        config.get(cat, "chosenType", dType.toString(), " valid types: " + StringUtils.join(EnumDBType.values(), ", ")).set(type.toString());

        if (fallback != null)
        {
            config.get(cat, "checkParent", useParent,
                    "If this is true, settings will be taken from the parent, most probably the Main or Core config. This is only taken into effect with remote databases.")
                    .set(useParent);
        }

        String newcat;
        HashMap<String, Property> props;
        for (EnumDBType dbType : EnumDBType.values())
        {
            newcat = cat + "." + dbType;

            props = data.get(dbType);
            if (props == null)
            {
                continue;
            }

            if (dbType.isRemote)
            {
                config.get(newcat, "host", "localhost").set(props.get("host").getString());
                config.get(newcat, "port", 3360).set(props.get("port").getString());
                config.get(newcat, "database", dbDefault).set(props.get("database").getString());
                config.get(newcat, "user", "FEUSER").set(props.get("user").getString());
                config.get(newcat, "pass", "password").set(props.get("pass").getString());
            }
            else
            {
                config.get(newcat, "database", dbFileDefault, "this may be a file path as well.").set(props.get("database").getString());
            }

        }
    }

    /**
     * Loads the the connector from the config for use. config load method is not called.
     *
     * @param config
     * @param category
     *            the category where everything regarding this connector will be.
     */
    public void loadOrGenerate(Configuration config, String cat)
    {
        try
        {
            tempType = type = EnumDBType.valueOf(config.get(cat, "chosenType", dType.toString()).getString());
            if (fallback != null)
            {
                useParent = config.get(cat, "checkParent", false).getBoolean(false);
            }
        }
        catch (Exception e)
        {
            tempType = type = dType; // reset to default..
        }

        String newcat;
        HashMap<String, Property> props;
        for (EnumDBType dbType : EnumDBType.values())
        {
            newcat = cat + "." + dbType;

            props = data.get(dbType);
            if (props == null)
            {
                props = new HashMap<String, Property>();
                data.put(dbType, props);
            }

            if (dbType.isRemote)
            {
                props.put("host", config.get(newcat, "host", "localhost"));
                props.put("port", config.get(newcat, "port", 3306));
                props.put("database", config.get(newcat, "database", dbDefault));
                props.put("user", config.get(newcat, "user", "FEUSER"));
                props.put("pass", config.get(newcat, "pass", "password"));
            }
            else
            {
                props.put("database", config.get(newcat, "database", dbFileDefault, "this may be a file path as well."));
            }

        }

        config.get(cat, "chosenType", type.toString(), " valid types: " + StringUtils.join(EnumDBType.values(), ", "));
        config.get(cat, "checkParent", useParent,
                "If this is true, settings will be taken from the parent, most probably the Main or Core config. This is only taken into effect with remote databases.");
    }

    public Connection getChosenConnection()
    {
        HashMap<String, Property> props;
        Connection con;
        try
        {
            props = data.get(type);

            if (type.isRemote)
            {
                // check fallback
                if (useParent)
                {
                    con = fallback.getSpecificConnection(type);
                    if (con != null)
                    {
                        return con;
                    }
                    else
                    {
                        LoggingHandler.felog.warn("[FE+SQL] " + name + " Parent check failed, going to in-house.");
                    }
                }

                // continue with stuff
                String host = props.get("host").getString();
                int port = props.get("port").getInt();
                String database = props.get("database").getString();
                String user = props.get("user").getString();
                String pass = props.get("pass").getString();

                type.loadClass();
                String connect = type.getConnectionString(host, port, database);
                con = DriverManager.getConnection(connect, user, pass);
                return con;
            }
            else
            {
                // nonremote connections
                String database = props.get("database").getString();
                String connect = type.getConnectionString(database);
                con = DriverManager.getConnection(connect);
                return con;
            }
        }
        catch (Exception e)
        {
            LoggingHandler.felog.log(Level.WARN, "[FE+SQL] " + name + " In-House check failed, going to default.", e);
        }

        try
        {
            tempType = dType;
            // try the default...
            props = data.get(dType);
            if (dType.isRemote)
            {
                // continue with stuff
                String host = props.get("host").getString();
                int port = props.get("port").getInt();
                String database = props.get("database").getString();
                String user = props.get("user").getString();
                String pass = props.get("pass").getString();

                dType.loadClass();
                String connect = dType.getConnectionString(host, port, database);
                return DriverManager.getConnection(connect, user, pass);
            }
            else
            {
                // nonremote connections
                String database = props.get("database").getString();
                String connect = dType.getConnectionString(database);
                return DriverManager.getConnection(connect);
            }
        }
        catch (SQLException e)
        {
            LoggingHandler.felog.error("[FE+SQL] " + name + " CATASTROPHIC DATABASE CONNECTION FAILIURE!!!");
            Throwables.propagate(e);
        }

        return null;
    }

    /**
     * @param dbType
     *            Only use this for remote types.
     * @return NULL if some error occurred.
     * @throws IllegalArgumentException
     *             if the type is not remote
     */
    private Connection getSpecificConnection(EnumDBType dbType) throws IllegalArgumentException
    {
        if (!dbType.isRemote)
        {
            throw new IllegalArgumentException("Non remote type " + dbType + " is asking for parent config!");
        }

        try
        {

            HashMap<String, Property> props = data.get(dbType);
            String host = props.get("host").getString();
            int port = props.get("port").getInt();
            String database = props.get("database").getString();
            String user = props.get("user").getString();
            String pass = props.get("pass").getString();

            dbType.loadClass();
            String connect = dbType.getConnectionString(host, port, database);
            return DriverManager.getConnection(connect, user, pass);
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("[FE+SQL] " + name + " Failed parent check: " + e);
            return null;
        }
    }

    public EnumDBType getActiveType()
    {
        return tempType;
    }
}