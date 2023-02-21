package com.forgeessentials.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class DBConnector
{
    EnumDBType loadedType;
    private final DBConnector fallback;
    public final String name;
    private final EnumDBType defaultType;
    private EnumDBType type;
    private EnumDBType activeType;
    private final String dbDefault;
    private final String dbFileDefault;
    private boolean useParent;
    private HashMap<EnumDBType, DBConnectorData> data;
    private HashMap<EnumDBType, DBConnectorDataStatic> dataStatic;

    /**
     * @param name
     *            a name for the DB connector. to be used in Logging.
     * @param fallback
     *            The DBConnector from which to take information for a given type if loading that type from this config fails.
     * @param defaultType
     *            the default database type to use
     * @param dbDefault
     *            the default name for remote databases
     * @param dbFileDefault
     *            the default path for file databases. Relative to FEDIR
     * @paramuseFallbac if the Fallback should be used for remote Databases
     */
    public DBConnector(String name, DBConnector fallback, EnumDBType defaultType, String dbDefault, String dbFileDefault, boolean useFallback)
    {
        this.name = name;
        this.fallback = fallback;
        this.defaultType = type = activeType = defaultType;
        this.dbDefault = dbDefault;
        this.dbFileDefault = dbFileDefault;
        data = new HashMap<EnumDBType, DBConnectorData>();
        dataStatic = new HashMap<EnumDBType, DBConnectorDataStatic>();
        useParent = useFallback;
    }

    static ForgeConfigSpec.ConfigValue<String> FEchosenType;
    static BooleanValue FEcheckParent;
    /**
     * Forcibly writes everything to the config. the config's save() method is not called.
     *
     * @param config
     * @param category
     *            the category where everything regarding this connector will be.
     */
    public void write(String cat)
    {
        FEchosenType.set(type.toString());

        if (fallback != null)
        {
            FEcheckParent.set(useParent);
        }

        DBConnectorData props;
        DBConnectorDataStatic propsStatic;
        for (EnumDBType dbType : EnumDBType.values())
        {

            props = data.get(dbType);
            propsStatic = dataStatic.get(dbType);
            if (props == null)
            {
                continue;
            }

            if (dbType.isRemote)
            {
                propsStatic.setHost(props.getHost());
                propsStatic.setPort(props.getPort());
                propsStatic.setDatabase(props.getDatabase());
                propsStatic.setUser(props.getUser());
                propsStatic.setPass(props.getPass());
            }
            else
            {
                propsStatic.setDatabase(props.getDatabase());            }

        }
    }

    /**
     * Loads the the connector from the config for use. config load method is not called.
     *
     * @param config
     * @param category
     *            the category where everything regarding this connector will be.
     */
    public void loadOrGenerate(Builder BUILDER, String cat)
    {
        BUILDER.push(cat);
        FEchosenType = BUILDER.comment(" valid types: " + StringUtils.join(EnumDBType.values(), ", "))
                .define("chosenType", defaultType.toString());
        FEcheckParent = BUILDER.comment("If this is true, settings will be taken from the parent, most probably the Main or Core config. This is only taken into effect with remote databases.")
                .define("checkParent", useParent);
        BUILDER.pop();
        String newcat;
        DBConnectorDataStatic propsStatic;
        for (EnumDBType dbType : EnumDBType.values())
        {
            newcat = cat + "." + dbType;

            propsStatic = dataStatic.get(dbType);
            if (propsStatic == null)
            {
                propsStatic = new DBConnectorDataStatic();
                dataStatic.put(dbType, propsStatic);
            }

            if (dbType.isRemote)
            {
                BUILDER.push(newcat);
                propsStatic.setHost(BUILDER.define("host", "localhost"));
                propsStatic.setPort(BUILDER.defineInRange("port", 3306, 0, 65535));
                propsStatic.setDatabase(BUILDER.comment("this may be a file path as well for fileDatabases.")
                        .define("database", dbDefault));
                propsStatic.setUser(BUILDER.define("user", "FEUSER"));
                propsStatic.setPass(BUILDER.define("pass", "password"));
                BUILDER.pop();
            }
            else
            {
                BUILDER.push(newcat);
                propsStatic.setDatabase(BUILDER.comment("this may be a file path as well for fileDatabases.")
                        .define("database", dbDefault));
                BUILDER.pop();
            }
        }
    }
    public void bakeConfig(boolean isReload)
    {
        DBConnectorData props;
        DBConnectorDataStatic propsStatic;
        for (EnumDBType dbType : EnumDBType.values())
        {

            props = data.get(dbType);
            propsStatic = dataStatic.get(dbType);
            if (props == null)
            {
                props = new DBConnectorData();
                data.put(dbType, props);
            }

            if (dbType.isRemote)
            {
                props.setHost(propsStatic.getHost().get());
                props.setPort(propsStatic.getPort().get());
                props.setDatabase(propsStatic.getDatabase().get());
                props.setUser(propsStatic.getUser().get());
                props.setPass(propsStatic.getPass().get());
            }
            else
            {
                props.setDatabase(propsStatic.getDatabase().get());
            }
        }
        activeType = type = EnumDBType.valueOf(FEchosenType.get());
        if (fallback != null)
        {
            useParent = FEcheckParent.get();
        }
    }

    public Connection getChosenConnection()
    {
        DBConnectorData props;
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
                String host = props.getHost();
                int port = props.getPort();
                String database = props.getDatabase();
                String user = props.getUser();
                String pass = props.getPass();

                type.loadClass();
                String connect = type.getConnectionString(host, port, database);
                con = DriverManager.getConnection(connect, user, pass);
                return con;
            }
            else
            {
                // nonremote connections
                String database = props.getDatabase();
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
            activeType = defaultType;
            // try the default...
            props = data.get(defaultType);
            if (defaultType.isRemote)
            {
                // continue with stuff
                String host = props.getHost();
                int port = props.getPort();
                String database = props.getDatabase();
                String user = props.getUser();
                String pass = props.getPass();

                defaultType.loadClass();
                String connect = defaultType.getConnectionString(host, port, database);
                return DriverManager.getConnection(connect, user, pass);
            }
            else
            {
                // nonremote connections
                String database = props.getDatabase();
                String connect = defaultType.getConnectionString(database);
                return DriverManager.getConnection(connect);
            }
        }
        catch (SQLException e)
        {
            LoggingHandler.felog.error("[FE+SQL] " + name + " CATASTROPHIC DATABASE CONNECTION FAILIURE!!!");
            throw new RuntimeException(e);
        }
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

            DBConnectorData props = data.get(dbType);
            String host = props.getHost();
            int port = props.getPort();
            String database = props.getDatabase();
            String user = props.getUser();
            String pass = props.getPass();

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
        return activeType;
    }
    private class DBConnectorData {
        private String host;
        private int port;
        private String database;
        private String user;
        private String pass;
        private String chosenType;
        private boolean checkParent;

        public String getHost()
        {
            return host;
        }
        public void setHost(String host)
        {
            this.host = host;
        }
        public int getPort()
        {
            return port;
        }
        public void setPort(int port)
        {
            this.port = port;
        }
        public String getDatabase()
        {
            return database;
        }
        public void setDatabase(String database)
        {
            this.database = database;
        }
        public String getUser()
        {
            return user;
        }
        public void setUser(String user)
        {
            this.user = user;
        }
        public String getPass()
        {
            return pass;
        }
        public void setPass(String pass)
        {
            this.pass = pass;
        }
    }
    private static class DBConnectorDataStatic {
        static ForgeConfigSpec.ConfigValue<String> FEhost;
        static ForgeConfigSpec.IntValue FEport;
        static ForgeConfigSpec.ConfigValue<String> FEdatabase;
        static ForgeConfigSpec.ConfigValue<String> FEuser;
        static ForgeConfigSpec.ConfigValue<String> FEpass;

        /*Host*/
        public ConfigValue<String> getHost()
        {
            return FEhost;
        }
        public void setHost(ConfigValue<String> host)
        {
            FEhost = host;
        }
        public void setHost(String host)
        {
            FEhost.set(host);
        }

        /*Port*/
        public IntValue getPort()
        {
            return FEport;
        }
        public void setPort(IntValue port)
        {
            FEport = port;
        }
        public void setPort(int port)
        {
            FEport.set(port);
        }

        /*Database*/
        public ConfigValue<String> getDatabase()
        {
            return FEdatabase;
        }
        public void setDatabase(ConfigValue<String> database)
        {
            FEdatabase = database;
        }
        public void setDatabase(String database)
        {
            FEdatabase.set(database);
        }

        /*Username*/
        public ConfigValue<String> getUser()
        {
            return FEuser;
        }
        public void setUser(ConfigValue<String> user)
        {
            FEuser = user;
        }
        public void setUser(String user)
        {
            FEuser.set(user);
        }

        /*Password*/
        public ConfigValue<String> getPass()
        {
            return FEpass;
        }
        public void setPass(ConfigValue<String> pass)
        {
            FEpass = pass;
        }
        public void setPass(String pass)
        {
            FEpass.set(pass);
        }
    }
}