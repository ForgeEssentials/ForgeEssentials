package com.ForgeEssentials.util;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import com.google.common.base.Throwables;

public class DBConnector
{
	EnumDBType												loadedType;
	private final DBConnector								fallback;
	public final String										name;
	private final EnumDBType								dType;
	private EnumDBType										type;
	private final String									dbDefault;
	private final String									dbFileDefault;
	private boolean											useFallback;
	private HashMap<EnumDBType, HashMap<String, Property>>	data;

	/**
	 * @param name a name for the DB connector. to be used in Logging.
	 * @param fallback The DBConnector from which to take information for a given type if loading that type from this config fails.
	 * @param dType the default database type to use
	 * @param dbDefault the default name for remote databases
	 * @param dbFileDefault the default path for file databases. Relative to FEDIR 
	 * @paramuseFallbac if the Fallback should be used for remote Databases
	 */
	public DBConnector(String name, DBConnector fallback, EnumDBType dType, String dbDefault, String dbFileDefault, boolean useFallback)
	{
		this.name = name;
		this.fallback = fallback;
		this.dType = type = dType;
		this.dbDefault = dbDefault;
		this.dbFileDefault = dbFileDefault;
		data = new HashMap<EnumDBType, HashMap<String, Property>>();
		this.useFallback = useFallback;
	}

	/**
	 * Forcibly writes everything to the config. the config's save() method is not called.
	 * @param config
	 * @param category the category where everything regarding this connector will be.
	 */
	public void write(Configuration config, String cat)
	{
		config.get(cat, "chosenType", dType.toString(), " valid types: " + EnumDBType.getAll(" ")).value = type.toString();

		if (fallback != null)
			config.get(cat, "checkParent", useFallback, "If this is true, settings will be taken from tha parent, most probably the Main or Core config. This is only taken into effect with remote databases.").value = "" + useFallback;

		String newcat;
		HashMap<String, Property> props;
		for (EnumDBType type : EnumDBType.values())
		{
			newcat = cat + "." + type;

			props = data.get(type);
			if (props == null)
				continue;

			if (type.isRemote)
			{
				config.get(newcat, "host", "localhost").value = props.get("host").value;
				config.get(newcat, "port", 3360).value = props.get("port").value;
				config.get(newcat, "database", dbDefault).value = props.get("database").value;
				config.get(newcat, "user", "FEUSER").value = props.get("user").value;
				config.get(newcat, "pass", "password").value = props.get("pass").value;
			}
			else
			{
				config.get(newcat, "database", dbDefault, "this may be a file path as well.").value = props.get("database").value;
			}

		}
	}

	/**
	 * Loads the the connector from the config for use. config load method is not called.
	 * @param config
	 * @param category the category where everything regarding this connector will be.
	 */
	public void loadOrGenerate(Configuration config, String cat)
	{
		try
		{
			type = type.valueOf(config.get(cat, "chosenType", dType.toString()).value);
			if (fallback != null)
				useFallback = config.get(cat, "checkParent", false).getBoolean(false);
		}
		catch (Exception e)
		{
			type = dType; // reset to default..
		}

		String newcat;
		HashMap<String, Property> props;
		for (EnumDBType type : EnumDBType.values())
		{
			newcat = cat + "." + type;

			props = data.get(type);
			if (props == null)
				props = new HashMap<String, Property>();

			if (type.isRemote)
			{
				props.put("host", config.get(newcat, "host", "localhost"));
				props.put("port", config.get(newcat, "port", 3360));
				props.put("database", config.get(newcat, "database", dbDefault));
				props.put("user", config.get(newcat, "user", "FEUSER"));
				props.put("pass", config.get(newcat, "pass", "password"));
			}
			else
			{
				props.put("database", config.get(newcat, "database", dbDefault, "this may be a file path as well."));
			}
		}


		config.get(cat, "chosenType", dType.toString(), " valid types: " + EnumDBType.getAll(" "));
		config.get(cat, "checkParent", useFallback, "If this is true, settings will be taken from tha parent, most probably the Main or Core config. This is only taken into effect with remote databases.");
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
				if (useFallback)
				{
					con = fallback.getSpecificConnection(type);
					if (con != null)
						return con;
					else
						OutputHandler.SOP("Fallback check and parent check failed, goin to in-house.");
				}

				// continue with stuff
				String host = props.get("host").value;
				int port = props.get("port").getInt();
				String database = props.get("database").value;
				String user = props.get("user").value;
				String pass = props.get("pass").value;

				type.loadClass();
				String connect = type.getConnectionString(host, port, database);
				con = DriverManager.getConnection(connect, user, pass);
				return con;
			}
			else
			{
				// nonremote connections
				String database = props.get("database").value;
				String connect = type.getConnectionString(database);
				con = DriverManager.getConnection(connect);
				return con;
			}
		}
		catch (Exception e)
		{
			OutputHandler.SOP("In-House check failed, going to default.");
		}

		try
		{
			// try the default...
			props = data.get(dType);
			if (dType.isRemote)
			{
				// continue with stuff
				String host = props.get("host").value;
				int port = props.get("port").getInt();
				String database = props.get("database").value;
				String user = props.get("user").value;
				String pass = props.get("pass").value;

				dType.loadClass();
				String connect = dType.getConnectionString(host, port, database);
				return DriverManager.getConnection(connect, user, pass);
			}
			else
			{
				// nonremote connections
				String database = props.get("database").value;
				String connect = dType.getConnectionString(database);
				return DriverManager.getConnection(connect);
			}
		}
		catch(SQLException e)
		{
			OutputHandler.SOP("CATASTROPHIC DATABASE CONNECTION FAILIURE!!!");
			Throwables.propagate(e);
		}

		return null;
	}

	/**
	 * @param type Only use this for remote types.
	 * @return NULL if some error occurred.
	 * @throws IllegalArgumentException if the type is not remote
	 */
	private Connection getSpecificConnection(EnumDBType type) throws IllegalArgumentException
	{
		if (!type.isRemote)
			throw new IllegalArgumentException("Non remote type "+type+" is asking for parent config!");

		try
		{
			HashMap<String, Property> props = data.get(type);
			String host = props.get("host").value;
			int port = props.get("port").getInt();
			String database = props.get("database").value;
			String user = props.get("user").value;
			String pass = props.get("pass").value;

			type.loadClass();
			String connect = type.getConnectionString(host, port, database);
			return DriverManager.getConnection(connect, user, pass);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public EnumDBType getType()
	{
		return type;
	}
}