package com.ForgeEssentials.permission;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;

public class SqlHelper
{
	private Connection				db;
	private boolean					generate						= false;
	private static SqlHelper		instance;
	private Configuration			config;

	// tables
	private static final String		TABLE_ZONE						= "zones";
	private static final String		TABLE_GROUP						= "groups";
	private static final String		TABLE_GROUP_CONNECTOR			= "groupConnectors";
	private static final String		TABLE_LADDER					= "ladders";
	private static final String		TABLE_LADDER_NAME				= "ladderNames";
	private static final String		TABLE_PLAYER					= "players";
	private static final String		TABLE_PERMISSION				= "permissions";

	// columns for the zone table
	private static final String		COLUMN_ZONE_ZONEID				= "zoneID";
	private static final String		COLUMN_ZONE_NAME				= "zoneName";

	// columns for the group table
	private static final String		COLUMN_GROUP_GROUPID			= "groupID";
	private static final String		COLUMN_GROUP_NAME				= "groupName";
	private static final String		COLUMN_GROUP_PARENT				= "parent";
	private static final String		COLUMN_GROUP_PREFIX				= "prefix";
	private static final String		COLUMN_GROUP_SUFFIX				= "suffix";
	private static final String		COLUMN_GROUP_PRIORITY			= "priority";
	private static final String		COLUMN_GROUP_ZONE				= "zone";

	// group connector table.
	private static final String		COLUMN_GROUP_CONNECTOR_GROUPID	= "groupID";
	private static final String		COLUMN_GROUP_CONNECTOR_PLAYERID	= "playerID";
	private static final String		COLUMN_GROUP_CONNECTOR_ZONEID	= "zoneID";

	// ladder table
	private static final String		COLUMN_LADDER_LADDERID			= "ladderID";
	private static final String		COLUMN_LADDER_GROUPID			= "groupID";
	private static final String		COLUMN_LADDER_ZONEID			= "zoneID";
	private static final String		COLUMN_LADDER_RANK				= "rank";

	// ladderName table
	private static final String		COLUMN_LADDER_NAME_LADDERID		= "ladderID";
	private static final String		COLUMN_LADDER_NAME_NAME			= "ladderName";

	// player table
	private static final String		COLUMN_PLAYER_PLAYERID			= "playerID";
	private static final String		COLUMN_PLAYER_USERNAME			= "username";

	// permissions table
	private static final String		COLUMN_PERMISSION_TARGET		= "target";
	private static final String		COLUMN_PERMISSION_ISGROUP		= "isGroup";
	private static final String		COLUMN_PERMISSION_PERM			= "perm";
	private static final String		COLUMN_PERMISSION_ALLOWED		= "allowed";
	private static final String		COLUMN_PERMISSION_ZONEID		= "zoneID";

	// zones
	private final PreparedStatement	statementGetZoneIDFromName;							// zoneName >> zoneID
	private final PreparedStatement	statementGetZoneNameFromID;							// zoneID >> zoneName
	private final PreparedStatement	statementPutZone;										// $ ZoneName
	private final PreparedStatement	statementDelZone;										// X ZoneName

	// players
	private final PreparedStatement	statementGetPlayerIDFromName;							// playerName >> playerID
	private final PreparedStatement	statementGetPlayerNameFromID;							// playerID >> playerName
	private final PreparedStatement	statementPutPlayer;									// $ usernName

	// groups
	private final PreparedStatement	statementGetGroupIDFromName;							// groupName >> groupID
	private final PreparedStatement	statementGetGroupNameFromID;							// groupID >> groupName
	private final PreparedStatement	statementGetGroupFromName;								// groupName >> Group
	private final PreparedStatement	statementGetGroupFromID;								// groupID >> Group
	private final PreparedStatement	statementGetGroupsForPlayer;							// PlayerID, ZoneID >> Groups
	private final PreparedStatement	statementPutGroup;										// $ name, prefix, suffix, parent, priority, zone
	private final PreparedStatement	statementUpdateGroup;									// $ name, prefix, suffix, parent, priority, zone

	// ladders
	private final PreparedStatement	statementGetLadderIDFromName;							// ladderName >> ladderID
	private final PreparedStatement	statementGetLadderNameFromID;							// LadderID >> ladderName
	private final PreparedStatement	statementGetLadderIDFromGroup;							// groupID, zoneID >> ladderID
	private final PreparedStatement	statementGetLadderList;								// LadderID, ZoneID >> groupName, rank
	private final PreparedStatement	statementPutLadder;									// $ LadderName

	// permissions
	private final PreparedStatement	statementGetPermission;								// target, isgroup, perm, zone >> allowed
	private final PreparedStatement	statementGetPermissionForward;							// target, isgroup, perm, zone >> allowed
	private final PreparedStatement	statementPutPermission;								// $ , allowed, target, isgroup, perm, zone
	private final PreparedStatement	statementUpdatePermission;								// $ allowed, target, isgroup, perm, zone

	// dump statements... replace ALL ids with names...
	private final PreparedStatement	statementDumpGroups;
	private final PreparedStatement	statementDumpPlayers;
	private final PreparedStatement	statementDumpGroupPermissions;
	private final PreparedStatement	statementDumpPlayerPermissions;
	private final PreparedStatement	statementDumpGroupConnector;
	private final PreparedStatement	statementDumpLadders;

	public SqlHelper(ConfigPermissions config)
	{
		// set config.
		this.config = config.config;

		instance = this;
		connect();

		if (generate)
			generate();

		try
		{
			// statementGetLadderList
			StringBuilder query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_LADDER).append(".").append(COLUMN_LADDER_RANK)
					.append(" FROM ").append(TABLE_LADDER)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" WHERE ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=").append("?")
					.append(" AND ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_ZONEID).append("=").append("?")
					.append(" ORDER BY ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_RANK);
			statementGetLadderList = instance.db.prepareStatement(query.toString());

			// statementGetGroupFromName
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" WHERE ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append("=").append("'?'");
			statementGetGroupFromName = instance.db.prepareStatement(query.toString());

			// statementGetGroupFromID
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID)
					.append(" WHERE ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID).append("=").append("?");
			statementGetGroupFromID = instance.db.prepareStatement(query.toString());

			// statementGetGroupsForPlayer
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?")
					.append(" AND ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementGetGroupsForPlayer = instance.db.prepareStatement(query.toString());

			// statementUpdateGroup
			query = new StringBuilder("UPDATE ").append(TABLE_GROUP)
					.append(" SET ")
					.append(COLUMN_GROUP_NAME).append("=").append("'?', ")
					.append(COLUMN_GROUP_PREFIX).append("=").append("'?', ")
					.append(COLUMN_GROUP_SUFFIX).append("=").append("'?', ")
					.append(COLUMN_GROUP_PARENT).append("=").append("?, ")
					.append(COLUMN_GROUP_PRIORITY).append("=").append("?, ")
					.append(COLUMN_GROUP_ZONE).append("=").append("?")
					.append(" WHERE ")
					.append(COLUMN_GROUP_NAME).append("=").append("'?'");
			statementUpdateGroup = db.prepareStatement(query.toString());

			// statementGetPermission
			query = new StringBuilder("SELECT ").append(COLUMN_PERMISSION_ALLOWED)
					.append(" FROM ").append(TABLE_PERMISSION)
					.append(" WHERE ").append(COLUMN_PERMISSION_TARGET).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_PERM).append("=").append("'?'")
					.append(" AND ").append(COLUMN_PERMISSION_ZONEID).append("=").append("?");
			statementGetPermission = db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ").append(COLUMN_PERMISSION_ALLOWED)
					.append(" FROM ").append(TABLE_PERMISSION)
					.append(" WHERE ").append(COLUMN_PERMISSION_TARGET).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_PERM).append(" LIKE ").append("'?.%'")
					.append(" AND ").append(COLUMN_PERMISSION_ZONEID).append("=").append("?");
			statementGetPermissionForward = db.prepareStatement(query.toString());

			// statementUpdatePermission
			query = new StringBuilder("UPDATE ").append(TABLE_PERMISSION)
					.append(" SET ")
					.append(COLUMN_PERMISSION_ALLOWED).append("=").append("?")
					.append(" WHERE ").append(COLUMN_PERMISSION_TARGET).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_PERM).append("=").append("'?'")
					.append(" AND ").append(COLUMN_PERMISSION_ZONEID).append("=").append("?");
			statementUpdatePermission = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Helper Get Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementGetLadderFromID
			query = new StringBuilder("SELECT ").append(COLUMN_LADDER_NAME_NAME)
					.append(" FROM ").append(TABLE_LADDER_NAME)
					.append(" WHERE ").append(COLUMN_LADDER_NAME_LADDERID).append("=").append("?");
			statementGetLadderNameFromID = db.prepareStatement(query.toString());

			// statementGetLadderFromName
			query = new StringBuilder("SELECT ").append(COLUMN_LADDER_NAME_LADDERID)
					.append(" FROM ").append(TABLE_LADDER_NAME)
					.append(" WHERE ").append(COLUMN_LADDER_NAME_NAME).append("=").append("'?'");
			statementGetLadderIDFromName = db.prepareStatement(query.toString());

			// statementGetLadderFromGroup
			query = new StringBuilder("SELECT ").append(COLUMN_LADDER_LADDERID)
					.append(" FROM ").append(TABLE_LADDER)
					.append(" WHERE ").append(COLUMN_LADDER_GROUPID).append("=").append("'?'")
					.append(" AND ").append(COLUMN_LADDER_ZONEID).append("=").append("?");
			statementGetLadderIDFromGroup = db.prepareStatement(query.toString());

			// statementGetZoneFromID
			query = new StringBuilder("SELECT ")
					.append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_ZONE)
					.append(" WHERE ").append(COLUMN_ZONE_ZONEID).append("=").append("?");
			statementGetZoneNameFromID = db.prepareStatement(query.toString());

			// statementGetZoneFromName
			query = new StringBuilder("SELECT ")
					.append(COLUMN_ZONE_ZONEID)
					.append(" FROM ").append(TABLE_ZONE)
					.append(" WHERE ").append(COLUMN_ZONE_NAME).append("=").append("'?'");
			statementGetZoneIDFromName = db.prepareStatement(query.toString());

			// statementGetGroupFromID
			query = new StringBuilder("SELECT ")
					.append(COLUMN_GROUP_NAME)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" WHERE ").append(COLUMN_GROUP_GROUPID).append("=").append("?");
			statementGetGroupNameFromID = db.prepareStatement(query.toString());

			// statementGetGroupFromName
			query = new StringBuilder("SELECT ")
					.append(COLUMN_GROUP_GROUPID)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" WHERE ").append(COLUMN_GROUP_NAME).append("=").append("'?'");
			statementGetGroupIDFromName = db.prepareStatement(query.toString());

			// statementGetPlayerFromID
			query = new StringBuilder("SELECT ")
					.append(COLUMN_PLAYER_USERNAME)
					.append(" FROM ").append(TABLE_PLAYER)
					.append(" WHERE ").append(COLUMN_PLAYER_PLAYERID).append("=").append("?");
			statementGetPlayerNameFromID = db.prepareStatement(query.toString());

			// statementGetPlayerFromName
			query = new StringBuilder("SELECT ")
					.append(COLUMN_PLAYER_PLAYERID)
					.append(" FROM ").append(TABLE_PLAYER)
					.append(" WHERE ").append(COLUMN_PLAYER_USERNAME).append("=").append("'?'");
			statementGetPlayerIDFromName = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Helper Put Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementPutZone
			query = new StringBuilder("INSERT INTO ").append(TABLE_ZONE)
					.append(" (").append(COLUMN_ZONE_NAME).append(") ")
					.append(" VALUES ").append(" ('?') ");
			statementPutZone = db.prepareStatement(query.toString());

			// statementPutPlayer
			query = new StringBuilder("INSERT INTO ").append(TABLE_PLAYER)
					.append(" (").append(COLUMN_PLAYER_USERNAME).append(") ")
					.append(" VALUES ").append(" ('?') ");
			statementPutPlayer = db.prepareStatement(query.toString());

			// statementPutLadder
			query = new StringBuilder("INSERT INTO ").append(TABLE_LADDER_NAME)
					.append(" (").append(COLUMN_LADDER_NAME_NAME).append(") ")
					.append(" VALUES ").append(" ('?') ");
			statementPutLadder = db.prepareStatement(query.toString());

			// statementPutGroup
			query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP)
					.append(" (")
					.append(COLUMN_GROUP_NAME).append(", ")
					.append(COLUMN_GROUP_PREFIX).append(", ")
					.append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(COLUMN_GROUP_PARENT).append(", ")
					.append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(COLUMN_GROUP_ZONE).append(") ")
					.append(" VALUES ").append(" ('?', '?', '?', ?, ?, ?) ");
			statementPutGroup = db.prepareStatement(query.toString());

			// statementPutPermission
			query = new StringBuilder("INSERT INTO ").append(TABLE_PERMISSION)
					.append(" (")
					.append(COLUMN_PERMISSION_ALLOWED).append(", ")
					.append(COLUMN_PERMISSION_TARGET).append(", ")
					.append(COLUMN_PERMISSION_ISGROUP).append(", ")
					.append(COLUMN_PERMISSION_PERM).append(", ")
					.append(COLUMN_PERMISSION_ZONEID).append(") ")
					.append(" VALUES ").append(" (?, ?, ?, '?', ?) ");
			statementPutPermission = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Helper Delete Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementPutZone
			query = new StringBuilder("DELETE FROM ").append(TABLE_ZONE)
					.append(" WHERE ").append(COLUMN_ZONE_NAME).append("=").append("'?'");
			statementDelZone = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Dump Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementGetGroupFromID
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME);
			statementDumpGroups = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_PERM).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_ALLOWED)
					.append(" FROM ").append(TABLE_PERMISSION)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_TARGET).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpGroupPermissions = instance.db.prepareStatement(query.toString());

			query = (new StringBuilder("SELECT "))
					.append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_USERNAME).append(", ")
					.append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_PERM).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_ALLOWED)
					.append(" FROM ").append(TABLE_PERMISSION)
					.append(" INNER JOIN ").append(TABLE_PLAYER)
					.append(" ON ").append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_TARGET).append("=").append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_PLAYERID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpPlayerPermissions = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ")
					.append(COLUMN_PLAYER_USERNAME)
					.append(" FROM ").append(TABLE_PLAYER);
			statementDumpPlayers = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT DISTINCT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_USERNAME).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_PLAYER)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_PLAYERID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpGroupConnector = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_LADDER_NAME).append(".").append(COLUMN_LADDER_NAME_NAME).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_LADDER)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_LADDER_NAME)
					.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=").append(TABLE_LADDER_NAME).append(".").append(COLUMN_LADDER_NAME_LADDERID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpLadders = instance.db.prepareStatement(query.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		OutputHandler.SOP("Statement preperation succesfull");
	}

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------INIT ---- METHODS ------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	private void connect()
	{
		try
		{
			// "org.sqlite.JDBC";
			// DriverClass = "com.mysql.jdbc.Driver";

			String type = config.get("stuff", "databaseType", "SqLite").value;
			if (type.equalsIgnoreCase("mysql"))
			{
				// ------------------
				// MYSQL
				// ------------------
				String server, port, database, user, pass, connect;
				ResultSet set;

				database = config.get("MySQL", "database", "FE_Permissions").value;

				if (config.get("MySQL", "stealConfigFromCore", false).getBoolean(false))
				{
					Configuration fconfig = ForgeEssentials.config.config;
					server = fconfig.get("MySQL", "server", "server.example.com").value;

					if (!server.equalsIgnoreCase("server.example.com"))
					{
						port = fconfig.get("Data.SQL", "port", 3306).value;
						user = fconfig.get("Data.SQL", "username", " ").value;
						pass = fconfig.get("Data.SQL", "password", " ").value;

						Class driverClass = Class.forName("com.mysql.jdbc.Driver");
						connect = "jdbc:mysql://" + server + ":" + port + "/" + database;
						this.db = DriverManager.getConnection(connect, user, pass);

						// check table...
						DatabaseMetaData dbm = db.getMetaData();
						set = dbm.getTables(null, null, TABLE_PERMISSION, null);
						generate = !set.next();
						return;
					}
					else
						OutputHandler.SOP("Core SQL configuration is invalid.. defaulting to in-house configurations");
				}

				server = config.get("MySQL", "host", "server.example.com").value;
				port = config.get("MySQL", "port", 3306).value;
				user = config.get("MySQL", "username", "FEUser").value;
				pass = config.get("MySQL", "password", "@we$0mePa$$w0rd").value;

				if (!server.equalsIgnoreCase("server.example.com"))
				{
					Class driverClass = Class.forName("com.mysql.jdbc.Driver");
					connect = "jdbc:mysql://" + server + ":" + port + "/" + database;
					db = DriverManager.getConnection(connect, user, pass);

					// check table...
					DatabaseMetaData dbm = db.getMetaData();
					set = dbm.getTables(null, null, TABLE_PERMISSION, null);
					generate = !set.next();
					return;
				}
				else
					OutputHandler.SOP("SQL configuration is invalid.. defaulting to SqLite");
			}
			else if (!type.equalsIgnoreCase("sqlite"))
				OutputHandler.SOP("Permissions Database set to unknown type! defaulting to sqLite");

			// ------------------
			// SQLITE
			// ------------------

			String path = config.get("SqLite", "file", "permissions.db").value;
			boolean absolute = config.get("SqLite", "absolutePath", false).getBoolean(false);
			File file;
			if (absolute)
				file = new File(path);
			else
				file = new File(ModulePermissions.permsFolder, path);

			if (!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
				OutputHandler.SOP("Permissions SqLite db file not found, creating.");
				generate = true;
			}

			if (db != null)
			{
				db.close();
				db = null;
			}

			Class driverClass = Class.forName("org.sqlite.JDBC");

			this.db = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			throw new RuntimeException(e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the Database Driver! Does it exist in the lib directory?");
			throw new RuntimeException(e.getMessage());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	// create tables.
	private void generate()
	{
		try
		{
			// table creation statements.
			String zoneTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_ZONE).append("(")
					.append(this.COLUMN_ZONE_ZONEID).append(" INTEGER AUTO_INCREMENT, ")
					.append(this.COLUMN_ZONE_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_ZONE_ZONEID).append(") ")
					.append(")").toString();

			String groupTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
					.append(this.COLUMN_GROUP_GROUPID).append("  INTEGER AUTO_INCREMENT, ")
					.append(this.COLUMN_GROUP_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append(this.COLUMN_GROUP_PARENT).append(" INTEGER, ")
					.append(this.COLUMN_GROUP_PRIORITY).append(" SMALLINT NOT NULL, ")
					.append(this.COLUMN_GROUP_ZONE).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_GROUP_PREFIX).append(" VARCHAR(20) DEFAULT '', ")
					.append(this.COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) DEFAULT '', ")
					.append("PRIMARY KEY (").append(COLUMN_GROUP_GROUPID).append(") ")
					.append(") ").toString();

			String ladderTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER).append("(")
					.append(this.COLUMN_LADDER_LADDERID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_LADDER_GROUPID).append(" INTEGER NOT NULL,")
					.append(this.COLUMN_LADDER_ZONEID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_LADDER_RANK).append(" SMALLINT NOT NULL")
					.append(") ").toString();

			String ladderNameTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER_NAME).append("(")
					.append(this.COLUMN_LADDER_NAME_LADDERID).append(" INTEGER AUTO_INCREMENT, ")
					.append(this.COLUMN_LADDER_NAME_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_LADDER_NAME_LADDERID).append(") ")
					.append(")").toString();

			String playerTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PLAYER).append("(")
					.append(this.COLUMN_PLAYER_PLAYERID).append(" INTEGER AUTO_INCREMENT, ")
					.append(this.COLUMN_PLAYER_USERNAME).append(" VARCHAR(20) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_PLAYER_PLAYERID).append(") ")
					.append(")").toString();

			String groupConnectorTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP_CONNECTOR).append("(")
					.append(this.COLUMN_GROUP_CONNECTOR_GROUPID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_GROUP_CONNECTOR_PLAYERID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_GROUP_CONNECTOR_ZONEID).append(" INTEGER NOT NULL")
					.append(")").toString();

			String permissionTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PERMISSION).append("(")
					.append(this.COLUMN_PERMISSION_TARGET).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_PERMISSION_ISGROUP).append(" TINYINT(1) NOT NULL, ")
					.append(this.COLUMN_PERMISSION_PERM).append(" TEXT NOT NULL, ")
					.append(this.COLUMN_PERMISSION_ALLOWED).append(" TINYINT(1) NOT NULL, ")
					.append(this.COLUMN_PERMISSION_ZONEID).append(" INTEGER NOT NULL")
					.append(")").toString();

			// create the tables.
			db.createStatement().executeUpdate(zoneTable);
			db.createStatement().executeUpdate(groupTable);
			db.createStatement().executeUpdate(ladderTable);
			db.createStatement().executeUpdate(ladderNameTable);
			db.createStatement().executeUpdate(playerTable);
			db.createStatement().executeUpdate(groupConnectorTable);
			db.createStatement().executeUpdate(permissionTable);

			// DEFAULT group
			StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP)
					.append(" (")
					.append(COLUMN_GROUP_NAME).append(", ")
					.append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(COLUMN_GROUP_ZONE).append(") ")
					.append(" VALUES ").append(" (")
					.append("'").append(PermissionsAPI.DEFAULT.name).append("', ")
					.append("0, ")  // priority
					.append("0)"); // zone
			db.createStatement().executeUpdate(query.toString());

			// GLOBAL zone
			query = new StringBuilder("INSERT INTO ").append(TABLE_ZONE)
					.append(" (").append(COLUMN_ZONE_NAME).append(", ").append(COLUMN_ZONE_ZONEID).append(") ")
					.append(" VALUES ").append(" ('").append(ZoneManager.GLOBAL.getZoneID()).append("', 0) ");
			db.createStatement().executeUpdate(query.toString());

			// SUPER zone
			query = new StringBuilder("INSERT INTO ").append(TABLE_ZONE)
					.append(" (").append(COLUMN_ZONE_NAME).append(", ").append(COLUMN_ZONE_ZONEID).append(") ")
					.append(" VALUES ").append(" ('").append(ZoneManager.SUPER).append("', -1) ");
			db.createStatement().executeUpdate(query.toString());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// -------------------------------MAJOR ---- USAGE ---- METHODS --------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	/**
	 * @param groupName
	 * @param zoneName
	 * @return NULL if no ladder in existence.
	 */
	protected static synchronized PromotionLadder getLadderForGroup(String group, String zone)
	{
		try
		{
			// get other vars
			int groupID = getGroupIDFromGroupName(group);
			int zoneID = getZoneIDFromZoneName(zone);
			int ladderID = getLadderIdFromGroup(groupID, zoneID);
			String ladderName = getLadderNameFromLadderID(ladderID);

			// setup query for List
			instance.statementGetLadderList.setInt(1, ladderID);
			instance.statementGetLadderList.setInt(2, zoneID);
			ResultSet set = instance.statementGetLadderList.executeQuery();
			instance.statementGetLadderList.clearParameters();

			ArrayList<String> list = new ArrayList<String>();

			while (set.next())
			{
				list.add(set.getString(0));
			}

			PromotionLadder ladder = new PromotionLadder(ladderName, zone, list);
			return ladder;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param groupName
	 * @return NULL if no group in existence. or an SQL error hapenned.
	 */
	protected static synchronized Group getGroupForName(String group)
	{
		try
		{
			// setup query for List
			instance.statementGetGroupFromName.setString(1, group);
			ResultSet set = instance.statementGetGroupFromName.executeQuery();
			instance.statementGetGroupFromName.clearParameters();

			if (!set.next())
				return null;

			int priority = set.getInt(COLUMN_GROUP_PRIORITY);
			String parent = set.getString(COLUMN_GROUP_PARENT);
			String prefix = set.getString(COLUMN_GROUP_PREFIX);
			String suffix = set.getString(COLUMN_GROUP_SUFFIX);
			String zone = set.getString(COLUMN_ZONE_NAME);
			return new Group(group, prefix, suffix, parent, zone, priority);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param groupID
	 * @return NULL if no group in existence, or an SQL erorr happenend.
	 * TDOD: remove?? its unused...
	 */
	protected static synchronized Group getGroupForID(int group)
	{
		try
		{
			// setup query for List
			instance.statementGetGroupFromID.setInt(1, group);
			ResultSet set = instance.statementGetGroupFromID.executeQuery();
			instance.statementGetGroupFromID.clearParameters();

			if (!set.next())
				return null;

			int priority = set.getInt(COLUMN_GROUP_PRIORITY);
			String name = set.getString(COLUMN_GROUP_NAME);
			String parent = set.getString(COLUMN_GROUP_PARENT);
			String prefix = set.getString(COLUMN_GROUP_PREFIX);
			String suffix = set.getString(COLUMN_GROUP_SUFFIX);
			String zone = set.getString(COLUMN_ZONE_NAME);
			return new Group(name, prefix, suffix, parent, zone, priority);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * groups are in order of priority.
	 * @param username
	 * @param zone
	 * @return NULL if SQL exception. Empty if in no groups.
	 */
	protected static synchronized ArrayList<Group> getGroupsForPlayer(String username, String zone)
	{
		try
		{
			TreeSet<Group> set = new TreeSet<Group>();
			int pID = getPlayerIDFromPlayerName(username);
			int zID = getZoneIDFromZoneName(zone);

			instance.statementGetGroupsForPlayer.setInt(1, pID);
			instance.statementGetGroupsForPlayer.setInt(2, zID);
			ResultSet result = instance.statementGetGroupsForPlayer.executeQuery();
			instance.statementGetGroupsForPlayer.clearParameters();

			int priority;
			String name, parent, prefix, suffix;
			Group g;

			while (result.next())
			{
				priority = result.getInt(COLUMN_GROUP_PRIORITY);
				name = result.getString(COLUMN_GROUP_NAME);
				parent = result.getString(COLUMN_GROUP_PARENT);
				prefix = result.getString(COLUMN_GROUP_PREFIX);
				suffix = result.getString(COLUMN_GROUP_SUFFIX);
				g = new Group(name, prefix, suffix, parent, zone, priority);
				set.add(g);
			}

			ArrayList<Group> list = new ArrayList<Group>();
			list.addAll(set);
			return list;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param g
	 * @return FALSE if the group already exists, parent doesn't exist, zone doesn't exist, or if the INSERT failed.
	 */
	protected static synchronized boolean createGroup(Group g)
	{
		try
		{
			// check if group exists?
			if (getGroupIDFromGroupName(g.name) >= 0)
				return false;  // group exists

			int parent = -5;
			int zone = getZoneIDFromZoneName(g.zoneID);

			if (g.parent != null)
			{
				parent = getGroupIDFromGroupName(g.parent);
				if (parent == -5)
					return false;
			}

			if (zone < -4)
				return false;

			// my query
			// $ name, prefix, suffix, parent, priority, zone
			instance.statementPutGroup.setString(1, g.name);
			instance.statementPutGroup.setString(2, g.prefix);
			instance.statementPutGroup.setString(3, g.suffix);
			if (parent == -5)
				instance.statementPutGroup.setString(4, "NULL");
			else
				instance.statementPutGroup.setInt(4, parent);

			instance.statementPutGroup.setInt(5, g.priority);
			instance.statementPutGroup.setInt(6, zone);
			instance.statementPutGroup.executeUpdate();
			instance.statementPutGroup.clearParameters();

			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param g
	 * @return FALSE if the group already exists, parent doesn't exist, zone doesn't exist, or if the UPDATE failed.
	 */
	protected static synchronized boolean updateGroup(Group g)
	{
		try
		{
			// check if group exists?
			if (getGroupIDFromGroupName(g.name) < 0)
				return false;  // group doesn't exist

			int parent = -5;
			int zone = getZoneIDFromZoneName(g.zoneID);

			if (g.parent != null)
			{
				parent = getGroupIDFromGroupName(g.parent);
				if (parent == -5)
					return false;
			}

			if (zone < -4)
				return false;

			// my query
			instance.statementUpdateGroup.setString(1, g.name);
			instance.statementUpdateGroup.setString(2, g.prefix);
			instance.statementUpdateGroup.setString(3, g.suffix);
			if (parent == -5)
				instance.statementPutGroup.setString(4, "NULL");
			else
				instance.statementPutGroup.setInt(4, parent);
			instance.statementUpdateGroup.setInt(5, g.priority);
			instance.statementUpdateGroup.setInt(6, zone);
			instance.statementUpdateGroup.executeUpdate();
			instance.statementUpdateGroup.clearParameters();

			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param target (username or groupname)
	 * @param isGroup
	 * @param perm
	 * @return ALLOW/DENY if the permission or a parent is allowed/denied. UNKNOWN if nor it or any parents were not found.
	 * UNKNOWN also if the target or the zone do not exist.
	 */
	@SuppressWarnings("incomplete-switch")
	protected static synchronized PermResult getPermissionResult(String target, boolean isGroup, PermissionChecker perm, String zone, boolean checkForward)
	{
		try
		{
			int tID;
			int zID = getZoneIDFromZoneName(zone);
			int isG = isGroup ? 1 : 0;
			int allowed = -1;
			PreparedStatement statement = instance.statementGetPermission;
			ResultSet set;

			if (isGroup)
				tID = getGroupIDFromGroupName(target);
			else
				tID = getPlayerIDFromPlayerName(target);

			if (zID < -4 || tID < -4)
				return PermResult.UNKNOWN;

			// initial check.
			statement.setInt(1, tID);
			statement.setInt(2, isG);
			statement.setString(3, perm.name);
			statement.setInt(4, zID);
			set = statement.executeQuery();
			statement.clearParameters();

			PermResult initial = PermResult.UNKNOWN;
			if (set.next())
				return set.getInt(1) > 0 ? PermResult.ALLOW : PermResult.DENY;

			// if the stuff is FORWARD!
			if (checkForward)
			{
				statement = instance.statementGetPermissionForward;

				// target, isgroup, perm, zone >> allowed
				statement.setInt(1, tID);
				statement.setInt(2, isG);
				statement.setString(3, perm.name);
				statement.setInt(4, zID);
				set = statement.executeQuery();
				statement.clearParameters();

				boolean allow = false;
				boolean deny = false;

				switch (initial)
					{
						case ALLOW:
							allow = true;
							break;
						case DENY:
							deny = true;
							break;
					}

				while (set.next())
				{
					allowed = set.getInt(1); // allowed.. only 1 column.

					if (allowed == 0) // deny. 1 or 0
						deny = true;
					else
						allow = true;

					if (allow && deny)
					{
						instance.statementGetPermission.clearParameters();
						return PermResult.PARTIAL;
					}
				}

				if (allowed > -1)
				{
					instance.statementGetPermission.clearParameters();
					if (allow && !deny)
						return PermResult.DENY;
				}

				statement = instance.statementGetPermission;
			}

			if (!initial.equals(PermResult.UNKNOWN))
			{
				instance.statementGetPermission.clearParameters();
				return initial;
			}

			// normal checking of the parents now
			while (perm != null)
			{
				// params still set from initial
				statement.setString(3, perm.name);
				set = statement.executeQuery();

				if (set.next())
				{
					allowed = set.getInt(1); // allowed.. only 1 column.
					return allowed > 0 ? PermResult.ALLOW : PermResult.DENY;
				}

				if (!perm.hasParent())
					perm = null;
				else
					perm = new PermissionChecker(perm.getImmediateParent());
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}
		return PermResult.UNKNOWN;
	}

	/**
	 * Creates the permission if it doesn't exist.. updates it if it does.
	 * @param target
	 * @param isGroup
	 * @param perm
	 * @param zone
	 * @return FALSE if the group, or zone do not exist.
	 */
	protected static synchronized boolean setPermission(String target, boolean isGroup, Permission perm, String zone)
	{
		try
		{
			int tID;
			int zID = getZoneIDFromZoneName(zone);
			int isG = isGroup ? 1 : 0;
			int allowed = perm.allowed ? 1 : 0;

			if (isGroup)
				tID = getGroupIDFromGroupName(target);
			else
				tID = getPlayerIDFromPlayerName(target);

			if (zID < -4 || tID < -4)
				return false;

			PreparedStatement use;

			// check permission existence...
			instance.statementGetPermission.setInt(1, tID);
			instance.statementGetPermission.setInt(2, isG);
			instance.statementGetPermission.setString(3, perm.name);
			instance.statementGetPermission.setInt(4, zID);
			ResultSet set = instance.statementGetPermission.executeQuery();
			instance.statementGetPermission.clearParameters();

			// allowed, target, isgroup, perm, zone
			if (set.next())
				use = instance.statementUpdatePermission; // exists
			else
				use = instance.statementPutPermission; // does not exist.

			use.setInt(1, allowed);
			use.setInt(2, tID);
			use.setInt(3, isG);
			use.setString(4, perm.name);
			use.setInt(5, zID);
			use.executeUpdate();
			use.clearParameters();

			return true;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	protected static synchronized boolean delZone(String name)
	{
		try
		{
			instance.statementDelZone.setString(1, name);
			instance.statementDelZone.executeUpdate();
			instance.statementDelZone.clearParameters();

			return true;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	protected static synchronized boolean createZone(String name)
	{
		try
		{
			instance.statementPutZone.setString(1, name);
			instance.statementPutZone.executeUpdate();
			instance.statementPutZone.clearParameters();

			return true;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	protected static synchronized boolean doesZoneExist(String name)
	{
		try
		{
			int ID = getZoneIDFromZoneName(name);

			return ID > 0;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * queries the entire DB and make it into nonDB format..
	 * @return
	 * "players" >> arraylist<String>
	 * "groups" >> arrayList<Group>
	 *  "playerPerms" >> arrayList<permHolder>
	 *  "groupPerms" >> arrayList<permHolder>
	 *  "groupConnectors" >> HashMap<String, HashMap<String, ArrayList<String>>>
	 *  "ladders" >> arraylist<PromotionLadder>
	 */
	protected HashMap<String, Object> dump()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		ResultSet set;
		ArrayList list;

		// DUMP PLAYERS! ---------------------------------
		try
		{
			set = instance.statementDumpPlayers.executeQuery();

			list = new ArrayList<String>();

			while (set.next())
				list.add(set.getInt(1));

			map.put("players", list);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Player dump for export failed!");
			e.printStackTrace();
			list = null;
		}

		// DUMP GROUPS! -------------------------------------
		try
		{
			set = instance.statementDumpGroups.executeQuery();

			list = new ArrayList<Group>();

			int priority;
			String parent, prefix, suffix, zone, name;
			Group g;
			while (set.next())
			{
				priority = set.getInt(COLUMN_GROUP_PRIORITY);
				name = set.getString(COLUMN_GROUP_NAME);
				parent = set.getString(COLUMN_GROUP_PARENT);
				prefix = set.getString(COLUMN_GROUP_PREFIX);
				suffix = set.getString(COLUMN_GROUP_SUFFIX);
				zone = set.getString(COLUMN_ZONE_NAME);
				g = new Group(name, prefix, suffix, parent, zone, priority);
				list.add(g);
			}

			map.put("groups", list);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Group dump for export failed!");
			e.printStackTrace();
			list = null;
		}

		// DUMP PLAYER PERMISSIONS! ------------------------------
		try
		{
			set = instance.statementDumpPlayerPermissions.executeQuery();

			list = new ArrayList<PermissionHolder>();

			boolean allowed;
			String target, zone, perm;
			PermissionHolder holder;
			while (set.next())
			{
				target = set.getString(COLUMN_PLAYER_USERNAME);
				zone = set.getString(COLUMN_ZONE_NAME);
				perm = set.getString(COLUMN_PERMISSION_PERM);
				allowed = set.getBoolean(COLUMN_PERMISSION_ALLOWED);
				holder = new PermissionHolder(target, perm, allowed, zone);
				list.add(holder);
			}

			map.put("playerPerms", list);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Player Permission dump for export failed!");
			e.printStackTrace();
			list = null;
		}

		// DUMP GROUP PERMISSIONS! ------------------------------
		try
		{		
			set = instance.statementDumpGroupPermissions.executeQuery();

			list = new ArrayList<PermissionHolder>();

			boolean allowed;
			String target, zone, perm;
			PermissionHolder holder;
			while (set.next())
			{
				target = set.getString(COLUMN_GROUP_NAME);
				zone = set.getString(COLUMN_ZONE_NAME);
				perm = set.getString(COLUMN_PERMISSION_PERM);
				allowed = set.getBoolean(COLUMN_PERMISSION_ALLOWED);
				holder = new PermissionHolder(target, perm, allowed, zone);
				list.add(holder);
			}

			map.put("groupPerms", list);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Group Permission dump for export failed!");
			e.printStackTrace();
			list = null;
		}

		// DUMP GROUP CONNECTORS! ------------------------------
		try
		{
			set = instance.statementDumpGroupConnector.executeQuery();

			HashMap<String, HashMap<String, ArrayList<String>>> uberMap = new HashMap<String, HashMap<String, ArrayList<String>>>(); 

			String group, zone, player;
			HashMap<String, ArrayList<String>> gMap;
			while (set.next())
			{
				group = set.getString(COLUMN_GROUP_NAME);
				zone = set.getString(COLUMN_ZONE_NAME);
				player = set.getString(COLUMN_PLAYER_USERNAME);
				
				gMap = uberMap.get(zone);
				if (gMap == null)
				{
					gMap = new HashMap<String, ArrayList<String>>();
					uberMap.put(zone, gMap);
				}
				
				list = gMap.get(group);
				if (list == null)
				{
					list = new ArrayList<String>();
					gMap.put(group, list);
				}
				
				list.add(player);					
			}
			
			map.put("groupConnectors", uberMap);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Group Connection dump for export failed!");
			e.printStackTrace();
			list = null;
		}
		
		// DUMP LADDERS! ------------------------------
		try
		{
			set = instance.statementDumpLadders.executeQuery();

			// zone, ladder, groupnames
			HashMap<String, HashMap<String, ArrayList<String>>> uberMap = new HashMap<String, HashMap<String, ArrayList<String>>>(); 

			String ladder, zone, group;
			HashMap<String, ArrayList<String>> gMap;
			while (set.next())
			{
				ladder = set.getString(COLUMN_LADDER_NAME_NAME);
				zone = set.getString(COLUMN_ZONE_NAME);
				group = set.getString(COLUMN_GROUP_NAME);
				
				gMap = uberMap.get(zone);
				if (gMap == null)
				{
					gMap = new HashMap<String, ArrayList<String>>();
					uberMap.put(zone, gMap);
				}
				
				list = gMap.get(ladder);
				if (list == null)
				{
					list = new ArrayList<String>();
					gMap.put(ladder, list);
				}
				
				list.add(group);		
			}
			
			list = new ArrayList<PromotionLadder>();
			
			PromotionLadder lad;
			String[] holder = new String[] {};
			for (Entry<String, HashMap<String, ArrayList<String>>>  entry1 : uberMap.entrySet())
				for (Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet())
				{
					if (entry2.getValue().isEmpty())
						continue;
					
					lad = new PromotionLadder(entry2.getKey(), entry1.getKey(), entry2.getValue().toArray(holder));
				}
			
			map.put("ladders", list);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Ladder dump for export failed!");
			e.printStackTrace();
			list = null;
		}

		// ALL usernames..
		// FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getAllUsernames()

		return map;
	}

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// -------------------------------ID <<>> NAME METHODS ----------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	/**
	 * @param zone
	 * @return -5 if the Zone does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getZoneIDFromZoneName(String zone) throws SQLException
	{
		if (zone.equals(ZoneManager.GLOBAL.getZoneID()))
			return 0;
		else if (zone.equals(ZoneManager.SUPER))
			return -1;

		instance.statementGetZoneIDFromName.setString(1, zone);
		ResultSet set = instance.statementGetZoneIDFromName.executeQuery();
		instance.statementGetZoneIDFromName.clearParameters();

		if (!set.next())
			return -5;

		return set.getInt(1);
	}

	/**
	 * @param zoneID
	 * @return null if the Zone does not exist.
	 * @throws SQLException
	 */
	private static synchronized String getZoneNameFromZoneID(int zoneID) throws SQLException
	{
		instance.statementGetZoneNameFromID.setInt(1, zoneID);
		ResultSet set = instance.statementGetZoneNameFromID.executeQuery();
		instance.statementGetZoneNameFromID.clearParameters();

		if (!set.next())
			return null;

		return set.getString(1);
	}

	/**
	 * @param ladder
	 * @return -5 if the Ladder does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getLadderIDFromLadderName(String ladder) throws SQLException
	{
		instance.statementGetLadderIDFromName.setString(1, ladder);
		ResultSet set = instance.statementGetLadderIDFromName.executeQuery();
		instance.statementGetLadderIDFromName.clearParameters();

		if (!set.next())
			return -5;

		return set.getInt(1);
	}

	/**
	 * @param ladderID
	 * @return null if the Ladder does not exist.
	 * @throws SQLException
	 */
	private static synchronized String getLadderNameFromLadderID(int ladderID) throws SQLException
	{
		instance.statementGetLadderNameFromID.setInt(1, ladderID);
		ResultSet set = instance.statementGetLadderNameFromID.executeQuery();
		instance.statementGetLadderNameFromID.clearParameters();

		if (!set.next())
			return null;

		return set.getString(1);
	}

	/**
	 * @param ladderID
	 * @return null if the Ladder does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getLadderIdFromGroup(int groupID, int zoneID) throws SQLException
	{
		instance.statementGetLadderIDFromGroup.setInt(1, groupID);
		instance.statementGetLadderIDFromGroup.setInt(2, zoneID);
		ResultSet set = instance.statementGetLadderIDFromGroup.executeQuery();
		instance.statementGetLadderIDFromGroup.clearParameters();

		if (!set.next())
			return -5;

		return set.getInt(1);
	}

	/**
	 * @param group
	 * @return -5 if the Group does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getGroupIDFromGroupName(String group) throws SQLException
	{
		if (group.equals(PermissionsAPI.DEFAULT.name))
			return 0;

		instance.statementGetGroupIDFromName.setString(1, group);
		ResultSet set = instance.statementGetGroupIDFromName.executeQuery();
		instance.statementGetGroupIDFromName.clearParameters();

		if (!set.next())
			return -5;

		return set.getInt(1);
	}

	/**
	 * @param groupID
	 * @return null if the Group does not exist.
	 * @throws SQLException
	 */
	private static synchronized String getGroupNameFromGroupID(int groupID) throws SQLException
	{
		instance.statementGetGroupNameFromID.setInt(1, groupID);
		ResultSet set = instance.statementGetGroupNameFromID.executeQuery();
		instance.statementGetGroupNameFromID.clearParameters();

		if (!set.next())
			return null;

		return set.getString(1);
	}

	/**
	 * @param player
	 * @return Creates the player if it does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getPlayerIDFromPlayerName(String player) throws SQLException
	{
		instance.statementGetPlayerIDFromName.setString(1, player);
		ResultSet set = instance.statementGetPlayerIDFromName.executeQuery();

		if (!set.next())
		{
			// doesn't exist.. create the player...
			instance.statementPutPlayer.setString(1, player);
			instance.statementPutPlayer.executeUpdate();
			instance.statementPutPlayer.clearParameters();

			set = instance.statementGetPlayerIDFromName.executeQuery();
		}

		instance.statementGetPlayerIDFromName.clearParameters();

		return set.getInt(1);
	}

	/**
	 * @param playerID
	 * @return null if the Player does not exist.
	 * @throws SQLException
	 */
	private static synchronized String getPlayerNameFromPlayerID(int playerID) throws SQLException
	{
		instance.statementGetPlayerNameFromID.setInt(1, playerID);
		ResultSet set = instance.statementGetPlayerNameFromID.executeQuery();
		instance.statementGetPlayerNameFromID.clearParameters();

		if (!set.next())
			return null;

		return set.getString(1);
	}
}
