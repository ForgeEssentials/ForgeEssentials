package com.forgeessentials.permissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.util.EnumDBType;
import com.forgeessentials.util.OutputHandler;
import com.google.common.base.Throwables;

/**
 * Usernames are only kept for ease of updating - this class should be receiving UUIDs that have been toString'ed.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SqlHelper {

	private static SqlHelper INSTANCE;

	// Table constants
	private static final String TABLE_PREFIX = "fepermissions_";

	// tables
	private static final String TABLE_META = TABLE_PREFIX + "meta";
	private static final String TABLE_PLAYER = TABLE_PREFIX + "player";
	private static final String TABLE_GROUP = TABLE_PREFIX + "group";
	private static final String TABLE_AREA = TABLE_PREFIX + "area";
	private static final String TABLE_PLAYER_PERMISSION = TABLE_PREFIX + "perm_player";
	private static final String TABLE_GROUP_PERMISSION = TABLE_PREFIX + "perm_group";

	private static final String TABLE_GROUP_CONNECTOR = TABLE_PREFIX + "groupConnectors";
	private static final String TABLE_LADDER = TABLE_PREFIX + "ladders";
	private static final String TABLE_LADDER_NAME = TABLE_PREFIX + "ladderNames";
	private static final String TABLE_PERMPROP = TABLE_PREFIX + "permProps";

	// TABLE_META
	private static final String COLUMN_META_VERSION = "meta_version";

	// TABLE_PLAYER
	private static final String COLUMN_PLAYER_ID = "player_id";
	private static final String COLUMN_PLAYER_UUID = "player_uuid";
	private static final String COLUMN_PLAYER_NAME = "player_name";

	// TABLE_GROUP
	private static final String COLUMN_GROUP_ID = "group_id";
	private static final String COLUMN_GROUP_NAME = "group_name";
	private static final String COLUMN_GROUP_PARENT = "group_parent";
	private static final String COLUMN_GROUP_PREFIX = "group_prefix";
	private static final String COLUMN_GROUP_SUFFIX = "group_suffix";
	private static final String COLUMN_GROUP_PRIORITY = "group_priority";

	// TABLE_AREA
	private static final String COLUMN_AREA_ID = "area_id";
	private static final String COLUMN_AREA_NAME = "area_name";
	private static final String COLUMN_AREA_WORLD = "area_world";
	private static final String COLUMN_AREA_XYZ_PREFIX = "area_";

	// TABLE_PLAYER_PERMISSION
	private static final String COLUMN_PLAYER_PERMISSION_PLAYER = "player_id";
	private static final String COLUMN_PLAYER_PERMISSION_AREA = "area_id";
	private static final String COLUMN_PLAYER_PERMISSION_ALLOW = "perm_allow";
	private static final String COLUMN_PLAYER_PERMISSION_NODE = "perm_node";

	// TABLE_GROUP_PERMISSION
	private static final String COLUMN_GROUP_PERMISSION_ID = "group_id";
	private static final String COLUMN_GROUP_PERMISSION_AREA = "area_id";
	private static final String COLUMN_GROUP_PERMISSION_ALLOW = "perm_allow";
	private static final String COLUMN_GROUP_PERMISSION_NODE = "perm_node";

	public static final String VERSION = "1.0";

	private Connection db;
	private EnumDBType dbType;
	private String version;

	private PreparedStatement stmtGetGroupByName;
	private PreparedStatement stmtGetGroupByID;
	private PreparedStatement stmtGetGroups;
	private PreparedStatement semtGetGroupNameByID;

	public static SqlHelper getInstance()
	{
		if (ModulePermissions.config == null)
		{
			throw new RuntimeException("Permissions configuration not loaded!");
		}
		try
		{
			if (INSTANCE == null || INSTANCE.db.isClosed())
			{
				INSTANCE = new SqlHelper();
			}
		}
		catch (SQLException e)
		{
			OutputHandler.felog.severe("Permissions connection database unable to be reset!");
			Throwables.propagate(e);
		}
		return INSTANCE;
	}

	private SqlHelper()
	{
		db = ModulePermissions.config.connector.getChosenConnection();
		dbType = ModulePermissions.config.connector.getActiveType();

		checkVersion();
		prepareStatements(dbType);
	}

	private void checkVersion()
	{
		try
		{
			StringBuilder query = new StringBuilder("SELECT ").append(COLUMN_META_VERSION).append(" FROM ").append(TABLE_META);
			Statement stmt = db.createStatement();
			ResultSet result = stmt.executeQuery(query.toString());
			version = result.getString(1);
			if (version == null || !version.equals(VERSION))
			{
				LogManager.getLogger().warn("Version of permission database incorrect. May not load permissions correctly!");
				createTables();
			}
		}
		catch (SQLException e)
		{
			createTables();
		}
	}

	private void createTable(String query)
	{
		try
		{
			db.createStatement().executeUpdate(query);
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
	}

	private void createTables()
	{
		createTable(new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_META).append(" (").append(COLUMN_META_VERSION).append(" VARCHAR(6) )")
				.toString());

		// try
		// {
		// // DEFAULT group
		// StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (").append(COLUMN_GROUP_ID).append(", ")
		// .append(COLUMN_GROUP_NAME).append(", ").append(COLUMN_GROUP_PRIORITY).append(", ").append(COLUMN_GROUP_AREA).append(") ")
		// .append(" VALUES ").append(" (").append(DEFAULT_ID).append(", ") // groupID
		// .append("'").append(APIRegistry.perms.getDEFAULT().name).append("', ").append("0, ").append(GLOBAL_ID).append(")"); // priority, zone
		// db.createStatement().executeUpdate(query.toString());
		//
		// // GLOBAL zone
		// query = new StringBuilder("INSERT INTO ").append(TABLE_AREA).append(" (").append(COLUMN_AREA_NAME).append(", ").append(COLUMN_AREA_ID)
		// .append(") ").append(" VALUES ").append(" ('").append(APIRegistry.permissionManager.getGlobalZone().getName()).append("', ").append(GLOBAL_ID)
		// .append(")");
		// db.createStatement().executeUpdate(query.toString());
		//
		// // SUPER zone
		// query = new StringBuilder("INSERT INTO ").append(TABLE_AREA).append(" (").append(COLUMN_AREA_NAME).append(", ").append(COLUMN_AREA_ID)
		// .append(") ").append(" VALUES ").append(" ('").append(APIRegistry.permissionManager.getSUPER().getName()).append("', ").append(SUPER_ID)
		// .append(")");
		// db.createStatement().executeUpdate(query.toString());
		//
		// // Entry player...
		// query = new StringBuilder("INSERT INTO ").append(TABLE_PLAYER).append(" (").append(COLUMN_PLAYER_UUID).append(", ")
		// .append(COLUMN_PLAYER_PLAYERID).append(") ").append(" VALUES ").append(" ('").append(APIRegistry.perms.getEntryPlayer()).append("', ")
		// .append(ENTRY_PLAYER_ID).append(")");
		// db.createStatement().executeUpdate(query.toString());
		//
		// }
		// catch (SQLException e)
		// {
		// Throwables.propagate(e);
		// }
	}

	private PreparedStatement prepareStatement(String query)
	{
		try
		{
			return db.prepareStatement(query);
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
		return null;
	}

	/**
	 * Initialize all prepared statements
	 */
	private void prepareStatements(EnumDBType type)
	{
		stmtGetGroups = prepareStatement(new StringBuilder("SELECT * FROM ").append(TABLE_GROUP_CONNECTOR).toString());

		stmtGetGroupByName = prepareStatement(new StringBuilder("SELECT * FROM ").append(TABLE_GROUP).append(" WHERE ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_NAME).append("=?").toString());

		stmtGetGroupByID = prepareStatement(new StringBuilder("SELECT * FROM ").append(TABLE_GROUP).append(" WHERE ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_ID).append("=?").toString());

		semtGetGroupNameByID = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_GROUP_NAME).append(" FROM ").append(TABLE_GROUP).append(" WHERE ")
				.append(COLUMN_GROUP_ID).append("=?").toString());

		// ---------------------------------------------------------------------------------------------------

		stmtGetPlayersByGroup = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_PLAYER_UUID).append(" FROM ").append(TABLE_GROUP_CONNECTOR)
				.append(" INNER JOIN ").append(TABLE_PLAYER).append(" ON ").append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_UUID).append("=")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".")
				.append(COLUMN_GROUP_CONNECTOR_GROUP).append("=?").toString());

		// ---------------------------------------------------------------------------------------------------

		// statementGetLadderList
		statementGetLadderList = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
				.append(TABLE_LADDER).append(".").append(COLUMN_LADDER_RANK).append(" FROM ").append(TABLE_LADDER).append(" INNER JOIN ").append(TABLE_GROUP)
				.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append("=").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_ID).append(" WHERE ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=?").append(" AND ")
				.append(TABLE_LADDER).append(".").append(COLUMN_LADDER_AREA).append("=?").append(" ORDER BY ").append(TABLE_LADDER).append(".")
				.append(COLUMN_LADDER_RANK).toString());

		// statementGetGroupsFromLadder
		statementGetGroupsFromLadder = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
				.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX)
				.append(", ").append(TABLE_AREA).append(".").append(COLUMN_AREA_NAME).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_PRIORITY).append(" FROM ").append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_ID).append(" INNER JOIN ").append(TABLE_LADDER).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
				.append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append(" INNER JOIN ")
				.append(TABLE_AREA).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=").append(TABLE_AREA)
				.append(".").append(COLUMN_AREA_ID).append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER)
				.append("=?").append(" AND ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=?").toString());

		// statementGetGroupsFromLadderAndZone
		statementGetGroupsFromLadderAndZone = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME)
				.append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_SUFFIX).append(", ").append(TABLE_AREA).append(".").append(COLUMN_AREA_NAME).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_PRIORITY).append(" FROM ").append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_ID).append(" INNER JOIN ").append(TABLE_LADDER).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
				.append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append(" INNER JOIN ")
				.append(TABLE_AREA).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=").append(TABLE_AREA)
				.append(".").append(COLUMN_AREA_ID).append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER)
				.append("=?").append(" AND ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=?").append(" AND ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=?").toString());

		// statementGetGroupsFromZone
		statementGetGroupsFromZone = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
				.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX)
				.append(", ").append(TABLE_AREA).append(".").append(COLUMN_AREA_NAME).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_PRIORITY).append(" FROM ").append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_ID).append(" INNER JOIN ").append(TABLE_AREA).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
				.append(COLUMN_GROUP_CONNECTOR_AREA).append("=").append(TABLE_AREA).append(".").append(COLUMN_AREA_ID).append(" WHERE ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=?").append(" AND ").append(TABLE_GROUP_CONNECTOR)
				.append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=?").toString());

		// statementGetGroupsFromZone
		statementGetGroupsFromPlayer = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
				.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX)
				.append(", ").append(TABLE_AREA).append(".").append(COLUMN_AREA_NAME).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_PRIORITY).append(" FROM ").append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_ID).append(" INNER JOIN ").append(TABLE_AREA).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
				.append(COLUMN_GROUP_CONNECTOR_AREA).append("=").append(TABLE_AREA).append(".").append(COLUMN_AREA_ID).append(" WHERE ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=?").toString());

		// statementGetGroupsForPlayerInZone
		statementGetGroupsForPlayerInZone = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ID).append(", ")
				.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY)
				.append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_SUFFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(" FROM ")
				.append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
				.append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ID).append(" WHERE ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=?").append(" AND ").append(TABLE_GROUP_CONNECTOR)
				.append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=?").toString());

		// statementGetGroupsForPlayer
		statementGetAllGroupsForPlayer = prepareStatement(new StringBuilder("SELECT * ").append(" FROM ").append(TABLE_GROUP_CONNECTOR).append(" WHERE ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=?").toString());

		// statementUpdateGroup
		statementUpdateGroup = prepareStatement(new StringBuilder("UPDATE ").append(TABLE_GROUP).append(" SET ").append(COLUMN_GROUP_NAME).append("=")
				.append("?, ").append(COLUMN_GROUP_PREFIX).append("=").append("?, ").append(COLUMN_GROUP_SUFFIX).append("=").append("?, ")
				.append(COLUMN_GROUP_PARENT).append("=").append("?, ").append(COLUMN_GROUP_PRIORITY).append("=? ").append(" WHERE ").append(COLUMN_GROUP_NAME)
				.append("=?").toString());

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>
		// Helper Get Statements
		// <<<<<<<<<<<<<<<<<<<<<<<<<<

		// statementGetLadderFromID
		statementGetLadderNameFromID = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_LADDER_NAME_NAME).append(" FROM ").append(TABLE_LADDER_NAME)
				.append(" WHERE ").append(COLUMN_LADDER_NAME_ID).append("=?").toString());

		// statementGetLadderFromName
		statementGetLadderIDFromName = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_LADDER_NAME_ID).append(" FROM ").append(TABLE_LADDER_NAME)
				.append(" WHERE ").append(COLUMN_LADDER_NAME_NAME).append("=?").toString());

		// statementGetLadderFromGroup
		statementGetLadderIDFromGroup = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_LADDER_LADDERID).append(" FROM ").append(TABLE_LADDER)
				.append(" WHERE ").append(COLUMN_LADDER_GROUPID).append("=?").append(" AND ").append(COLUMN_LADDER_AREA).append("=").append("?").toString());

		// statementGetZoneFromID
		statementGetZoneNameFromID = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_AREA_NAME).append(" FROM ").append(TABLE_AREA)
				.append(" WHERE ").append(COLUMN_AREA_ID).append("=?").toString());

		// statementGetZoneFromName
		statementGetZoneIDFromName = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_AREA_ID).append(" FROM ").append(TABLE_AREA).append(" WHERE ")
				.append(COLUMN_AREA_NAME).append("=?").toString());

		// statementGetGroupFromName
		statementGetGroupIDFromName = prepareStatement(new StringBuilder("SELECT *") // .append(COLUMN_GROUP_ID)
				.append(" FROM ").append(TABLE_GROUP).append(" WHERE ").append(COLUMN_GROUP_NAME).append("=?").toString());

		// statementGetPlayerFromID
		statementGetPlayerNameFromID = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_PLAYER_ID).append(" FROM ").append(TABLE_PLAYER)
				.append(" WHERE ").append(COLUMN_PLAYER_UUID).append("=?").toString());

		// statementGetPlayerFromName
		statementGetPlayerIDFromName = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_PLAYER_ID).append(" FROM ").append(TABLE_PLAYER)
				.append(" WHERE ").append(COLUMN_PLAYER_NAME).append("=?").toString());

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>
		// Helper Put Statements
		// <<<<<<<<<<<<<<<<<<<<<<<<<<

		// statementPutZone
		statementPutZone = prepareStatement(new StringBuilder("INSERT INTO ").append(TABLE_AREA).append(" (").append(COLUMN_AREA_NAME).append(") ")
				.append(" VALUES ").append(" (?) ").toString());

		// statementPutPlayer
		statementPutPlayer = prepareStatement(new StringBuilder("INSERT INTO ").append(TABLE_PLAYER).append(" (").append(COLUMN_PLAYER_UUID).append(") ")
				.append(" VALUES ").append(" (?) ").toString());

		// statementPutLadderName
		statementPutLadderName = prepareStatement(new StringBuilder("INSERT INTO ").append(TABLE_LADDER_NAME).append(" (").append(COLUMN_LADDER_NAME_NAME)
				.append(") ").append(" VALUES ").append(" (?) ").toString());

		// statementPutLadder
		statementPutLadder = prepareStatement(new StringBuilder("INSERT INTO ").append(TABLE_LADDER).append(" (").append(COLUMN_LADDER_GROUPID).append(", ")
				.append(COLUMN_LADDER_AREA).append(", ").append(COLUMN_LADDER_RANK).append(", ").append(COLUMN_LADDER_LADDERID).append(") ").append(" VALUES ")
				.append(" (?, ?, ?, ?) ").toString());

		// statementPutGroup
		statementPutGroup = prepareStatement(new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (").append(COLUMN_GROUP_NAME).append(", ")
				.append(COLUMN_GROUP_PREFIX).append(", ").append(COLUMN_GROUP_SUFFIX).append(", ").append(COLUMN_GROUP_PARENT).append(", ")
				.append(COLUMN_GROUP_PRIORITY).append(") ").append(" VALUES ").append(" (?, ?, ?, ?, ?) ").toString());

		// statementPutPlayerInGroup
		statementPutPlayerInGroup = prepareStatement(new StringBuilder("INSERT INTO ").append(TABLE_GROUP_CONNECTOR).append(" (")
				.append(COLUMN_GROUP_CONNECTOR_GROUP).append(", ").append(COLUMN_GROUP_CONNECTOR_PLAYER).append(", ").append(COLUMN_GROUP_CONNECTOR_AREA)
				.append(") ").append(" VALUES ").append(" (?, ?, ?)").toString());

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>
		// Helper Delete Statements
		// <<<<<<<<<<<<<<<<<<<<<<<<<<

		// statementDeleteGroupInZone
		statementDeleteGroupInZone = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_GROUP).append(" WHERE ").append(COLUMN_GROUP_NAME)
				.append("=?").toString());

		// statementDelZone
		statementDelZone = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_AREA).append(" WHERE ").append(COLUMN_AREA_NAME).append("=?")
				.toString());

		// remove player from all groups in specified zone. used in /p user
		// <player> group set
		statementRemovePlayerGroups = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_GROUP_CONNECTOR).append(" WHERE ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=?").append(" AND ").append(TABLE_GROUP_CONNECTOR)
				.append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=?").toString());

		// remove player from specified group in specified zone. used in /p
		// user <player> group add
		statementRemovePlayerGroup = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_GROUP_CONNECTOR).append(" WHERE ")
				.append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=?").append(" AND ").append(COLUMN_GROUP_CONNECTOR_AREA).append("=?").append(" AND ")
				.append(COLUMN_GROUP_CONNECTOR_GROUP).append("=?").toString());

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>
		// Helper ZONE Delete Statements
		// <<<<<<<<<<<<<<<<<<<<<<<<<<

		// delete groups from zone
		statementDelGroupFromZone = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_GROUP).toString());

		// delete ladder from zone
		statementDelLadderFromZone = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_LADDER).append(" WHERE ").append(COLUMN_LADDER_AREA)
				.append("=?").toString());

		// delete group connectorsw from zone
		statementDelGroupConnectorsFromZone = prepareStatement(new StringBuilder("DELETE FROM ").append(TABLE_LADDER).append(" WHERE ")
				.append(COLUMN_GROUP_CONNECTOR_AREA).append("=?").toString());

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>
		// Dump Statements
		// <<<<<<<<<<<<<<<<<<<<<<<<<<

		// statementGetGroupFromID
		statementDumpGroups = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
				.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX)
				.append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ").append(TABLE_GROUP).append(".")
				.append(COLUMN_GROUP_PARENT).append(" FROM ").append(TABLE_GROUP).toString());

		statementDumpPlayers = prepareStatement(new StringBuilder("SELECT ").append(COLUMN_PLAYER_UUID).append(" FROM ").append(TABLE_PLAYER).toString());

		statementDumpGroupConnector = prepareStatement(new StringBuilder("SELECT DISTINCT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME)
				.append(", ").append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_UUID).append(", ").append(TABLE_AREA).append(".").append(COLUMN_AREA_NAME)
				.append(" FROM ").append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ").append(TABLE_GROUP_CONNECTOR)
				.append(".").append(COLUMN_GROUP_CONNECTOR_GROUP).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ID).append(" INNER JOIN ")
				.append(TABLE_PLAYER).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYER).append("=")
				.append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_ID).append(" INNER JOIN ").append(TABLE_AREA).append(" ON ")
				.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_AREA).append("=").append(TABLE_AREA).append(".")
				.append(COLUMN_AREA_ID).toString());

		statementDumpLadders = prepareStatement(new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
				.append(TABLE_LADDER_NAME).append(".").append(COLUMN_LADDER_NAME_NAME).append(", ").append(TABLE_AREA).append(".").append(COLUMN_AREA_NAME)
				.append(" FROM ").append(TABLE_LADDER).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ").append(TABLE_LADDER).append(".")
				.append(COLUMN_LADDER_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ID).append(" INNER JOIN ")
				.append(TABLE_LADDER_NAME).append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=").append(TABLE_LADDER_NAME)
				.append(".").append(COLUMN_LADDER_NAME_ID).append(" INNER JOIN ").append(TABLE_AREA).append(" ON ").append(TABLE_LADDER).append(".")
				.append(COLUMN_LADDER_AREA).append("=").append(TABLE_AREA).append(".").append(COLUMN_AREA_ID).toString());

		OutputHandler.felog.finer("Statement preparation successful");
	}

	// ------------------------------------------------------------
	// -- Groups
	// ------------------------------------------------------------

	private Group getGroup(ResultSet set, boolean next)
	{
		try
		{
			if (!next || set.next())
			{
				int id = set.getInt(COLUMN_GROUP_ID);
				String name = set.getString(COLUMN_GROUP_NAME);
				String parent = getGroupNameByID(set.getInt(COLUMN_GROUP_PARENT));
				String prefix = set.getString(COLUMN_GROUP_PREFIX);
				String suffix = set.getString(COLUMN_GROUP_SUFFIX);
				int priority = set.getInt(COLUMN_GROUP_PRIORITY);
				return new Group(name, prefix, suffix, parent, priority, id);
			}
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
		return null;
	}

	private Group getGroup(ResultSet set)
	{
		return getGroup(set, true);
	}

	public List<Group> getGroups()
	{
		try
		{
			List<Group> list = new ArrayList<Group>();
			ResultSet set = stmtGetGroups.executeQuery();
			while (set.next())
			{
				list.add(getGroup(set, false));
			}
			return list;
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
		return null;
	}

	/**
	 * @param name
	 * @return NULL if no group in existence. or an SQL error happened.
	 */
	public Group getGroupByName(String name)
	{
		try
		{
			stmtGetGroupByName.setString(1, name);
			return getGroup(stmtGetGroupByName.executeQuery());
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
		return null;
	}

	/**
	 * @param id
	 * @return Group on success, null otherwise
	 */
	protected Group getGroupByID(int id)
	{
		try
		{
			stmtGetGroupByID.setInt(1, id);
			return getGroup(stmtGetGroupByID.executeQuery());
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
		return null;
	}

	/**
	 * @param group_id
	 * @return null if the group does not exist.
	 * @throws SQLException
	 */
	private String getGroupNameByID(int group_id) throws SQLException
	{
		semtGetGroupNameByID.setInt(1, group_id);
		ResultSet set = semtGetGroupNameByID.executeQuery();
		if (set.next())
		{
			return set.getString(1);
		}
		return null;
	}

	// ------------------------------------------------------------
	// -- Players
	// ------------------------------------------------------------

	protected ArrayList<String> getPlayersByGroup(int group_id)
	{
		try
		{
			ArrayList<String> list = new ArrayList<String>();
			stmtGetPlayersByGroup.setInt(1, group_id);
			ResultSet set = stmtGetPlayersByGroup.executeQuery();
			while (set.next())
			{
				list.add(set.getString(1));
			}
			return list;
		}
		catch (SQLException e)
		{
			Throwables.propagate(e);
		}
		return null;
	}

	protected ArrayList<String> getPlayersForGroup(String group_name)
	{
		Group group = getGroupByName(group_name);
		if (group == null)
			return null;
		return getPlayersByGroup(group.getId());
	}

	// /**
	// * groups are in order of priority.
	// *
	// * @param username
	// * @param zone
	// * @return NULL if SQL exception. Empty if in no groups.
	// */
	// protected synchronized ArrayList<Group> getGroupsForPlayer(String username, String zone)
	// {
	// try
	// {
	// TreeSet<Group> set = new TreeSet<Group>();
	// int pID = getPlayerIDFromPlayerName(username);
	// int zID = getZoneIDFromZoneName(zone);
	//
	// statementGetGroupsForPlayerInZone.setInt(1, pID);
	// statementGetGroupsForPlayerInZone.setInt(2, zID);
	// ResultSet result = statementGetGroupsForPlayerInZone.executeQuery();
	// statementGetGroupsForPlayerInZone.clearParameters();
	//
	// int priority;
	// String name, parent, prefix, suffix;
	// Group g;
	//
	// while (result.next())
	// {
	// priority = result.getInt(COLUMN_GROUP_PRIORITY);
	// name = result.getString(COLUMN_GROUP_NAME);
	// parent = getGroupNameFromGroupID(result.getInt(COLUMN_GROUP_PARENT));
	// prefix = result.getString(COLUMN_GROUP_PREFIX);
	// suffix = result.getString(COLUMN_GROUP_SUFFIX);
	// g = new Group(name, prefix, suffix, parent, zone, priority);
	// set.add(g);
	// }
	//
	// ArrayList<Group> list = new ArrayList<Group>();
	// list.addAll(set);
	// return list;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	//
	// return new ArrayList<Group>();
	// }
	//
	// /**
	// * @param g
	// * @return FALSE if the group already exists, parent doesn't exist, zone doesn't exist, or if the INSERT failed.
	// */
	// protected synchronized boolean createGroup(Group g)
	// {
	// try
	// {
	// // check if group exists?
	// if (getGroupIDFromGroupName(g.name) >= 0)
	// {
	// return false; // group exists
	// }
	//
	// int parent = -5;
	// int zone = getZoneIDFromZoneName(g.zoneName);
	//
	// if (g.parent != null)
	// {
	// parent = getGroupIDFromGroupName(g.parent);
	// if (parent == -5)
	// {
	// return false;
	// }
	// }
	//
	// if (zone < -4)
	// {
	// return false;
	// }
	//
	// // my query
	// // $ name, prefix, suffix, parent, priority, zone
	// statementPutGroup.setString(1, g.name);
	// statementPutGroup.setString(2, g.prefix);
	// statementPutGroup.setString(3, g.suffix);
	// if (parent == -5)
	// {
	// statementPutGroup.setNull(4, java.sql.Types.INTEGER);
	// }
	// else
	// {
	// statementPutGroup.setInt(4, parent);
	// }
	//
	// statementPutGroup.setInt(5, g.priority);
	// statementPutGroup.setInt(6, zone);
	// statementPutGroup.executeUpdate();
	// statementPutGroup.clearParameters();
	//
	// return true;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// // ---------------------------------------------------------------------------------------------------
	// // ---------------------------------------------------------------------------------------------------
	// // -------------------------------MAJOR ---- USAGE ---- METHODS --------------------------------------
	// // ---------------------------------------------------------------------------------------------------
	// // ---------------------------------------------------------------------------------------------------
	//
	// /**
	// * @param g
	// * @return FALSE if the group already exists, parent doesn't exist, zone doesn't exist, or if the UPDATE failed.
	// */
	// protected synchronized boolean updateGroup(Group g)
	// {
	// try
	// {
	// // check if group exists?
	// if (getGroupIDFromGroupName(g.name) < 0)
	// {
	// return false; // group doesn't exist
	// }
	//
	// int parent = -5;
	// int zone = getZoneIDFromZoneName(g.zoneName);
	//
	// if (g.parent != null)
	// {
	// parent = getGroupIDFromGroupName(g.parent);
	// if (parent == -5)
	// {
	// return false;
	// }
	// }
	//
	// if (zone < -4)
	// {
	// return false;
	// }
	//
	// // my query
	// statementUpdateGroup.setString(1, g.name);
	// statementUpdateGroup.setString(2, g.prefix);
	// statementUpdateGroup.setString(3, g.suffix);
	// if (parent == -5)
	// {
	// statementUpdateGroup.setNull(4, java.sql.Types.INTEGER);
	// }
	// else
	// {
	// statementUpdateGroup.setInt(4, parent);
	// }
	// statementUpdateGroup.setInt(5, g.priority);
	// statementUpdateGroup.setInt(6, zone);
	// statementUpdateGroup.setString(7, g.name);
	// statementUpdateGroup.setInt(8, zone);
	// statementUpdateGroup.executeUpdate();
	// statementUpdateGroup.clearParameters();
	//
	// return true;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// /**
	// * @param target
	// * (username or groupname)
	// * @param isGroup
	// * @param perm
	// * @return NULL if the
	// */
	// protected synchronized String getPermProp(String target, boolean isGroup, String perm, String zone)
	// {
	// try
	// {
	// int tID;
	// int zID = getZoneIDFromZoneName(zone);
	// PreparedStatement statement = statementGetPermProp;
	// ResultSet set;
	//
	// if (isGroup)
	// {
	// tID = getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// tID = getPlayerIDFromPlayerName(target);
	// }
	//
	// if (zID < -4 || tID < -4)
	// {
	// return null;
	// }
	//
	// statement.setInt(1, tID);
	// statement.setBoolean(2, isGroup);
	// statement.setString(3, perm);
	// statement.setInt(4, zID);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// if (set.next())
	// {
	// return set.getString(COLUMN_PERMPROP_PROP);
	// }
	// else
	// {
	// return null;
	// }
	//
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	//
	// }
	// return null;
	// }
	//
	// /**
	// * @param target
	// * (username or groupname)
	// * @param isGroup
	// * @param perm
	// * @return ALLOW/DENY if the permissions or a parent is allowed/denied. UNKNOWN if nor it or any parents were not found. UNKNOWN also if the target or the
	// * zone do not exist.
	// */
	// protected synchronized PermResult getPermissionResult(String target, boolean isGroup, PermissionChecker perm, String zone, boolean checkForward)
	// {
	// try
	// {
	// int tID;
	// int zID = getZoneIDFromZoneName(zone);
	// int allowed = -1;
	// PreparedStatement statement = statementGetPermission;
	// ResultSet set;
	//
	// if (isGroup)
	// {
	// tID = getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// tID = getPlayerIDFromPlayerName(target);
	// }
	//
	// if (zID < -4 || tID < -4)
	// {
	// return PermResult.UNKNOWN;
	// }
	//
	// // initial check.
	// statement.setInt(1, tID);
	// statement.setBoolean(2, isGroup);
	// statement.setString(3, perm.getQualifiedName());
	// statement.setInt(4, zID);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// PermResult initial = PermResult.UNKNOWN;
	// if (set.next())
	// {
	// return set.getInt(1) > 0 ? PermResult.ALLOW : PermResult.DENY;
	// }
	//
	// // if the stuff is FORWARD!
	// // TODO: fix.
	// /*
	// * if (checkForward) { statement = instance.statementGetPermissionForward; // target, isgroup, perm, zone >> allowed statement.setInt(1, tID);
	// * statement.setInt(2, isG); statement.setString(3, perm.name + ".%"); statement.setInt(4, zID); set = statement.executeQuery();
	// * statement.clearParameters(); boolean allow = false; boolean deny = false; switch (initial) { case ALLOW: allow = true; break; case DENY: deny =
	// * true; break; } while (set.next()) { allowed = set.getInt(1); // allowed.. only 1 column. if (allowed == 0) { deny = true; } else { allow = true;
	// * } if (allow && deny) { instance.statementGetPermission.clearParameters(); return PermResult.PARTIAL; } } if (allowed > -1) {
	// * instance.statementGetPermission.clearParameters(); if (allow && !deny) { return PermResult.DENY; } } statement = instance.statementGetPermission;
	// * }
	// */
	//
	// if (!initial.equals(PermResult.UNKNOWN))
	// {
	// statementGetPermission.clearParameters();
	// return initial;
	// }
	//
	// // normal checking of the parents now
	// while (perm != null)
	// {
	// // params still set from initial
	// statement.setInt(1, tID);
	// statement.setBoolean(2, isGroup);
	// statement.setString(3, perm.getQualifiedName());
	// statement.setInt(4, zID);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// if (set.next())
	// {
	// allowed = set.getInt(1); // allowed.. only 1 column.
	// return allowed > 0 ? PermResult.ALLOW : PermResult.DENY;
	// }
	//
	// if (!perm.hasParent())
	// {
	// perm = null;
	// }
	// else
	// {
	// perm = new PermissionChecker(perm.getAllParent());
	// }
	// }
	//
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	//
	// }
	// return PermResult.UNKNOWN;
	// }
	//
	// /**
	// * Creates the permissions if it doesn't exist.. updates it if it does.
	// *
	// * @param target
	// * @param isGroup
	// * @param perm
	// * @param zone
	// * @return FALSE if the group, or zone do not exist.
	// */
	// protected synchronized boolean setPermission(String target, boolean isGroup, Permission perm, String zone)
	// {
	// try
	// {
	// int tID;
	// int zID = getZoneIDFromZoneName(zone);
	//
	// if (isGroup)
	// {
	// tID = getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// tID = getPlayerIDFromPlayerName(target);
	// }
	//
	// if (zID < -4 || tID < -4)
	// {
	// return false;
	// }
	//
	// PreparedStatement use = statementGetPermission;
	//
	// // check permissions existence...
	// use.setInt(1, tID);
	// use.setBoolean(2, isGroup);
	// use.setString(3, perm.getQualifiedName());
	// use.setInt(4, zID);
	// ResultSet set = use.executeQuery();
	// use.clearParameters();
	//
	// // allowed, target, isgroup, perm, zone
	// if (set.next())
	// {
	// use = statementUpdatePermission; // exists
	// }
	// else
	// {
	// use = statementPutPermission; // does not exist.
	// }
	//
	// use.setBoolean(1, perm.allowed);
	// use.setInt(2, tID);
	// use.setBoolean(3, isGroup);
	// use.setString(4, perm.getQualifiedName());
	// use.setInt(5, zID);
	// use.executeUpdate();
	// use.clearParameters();
	//
	// return true;
	//
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// /**
	// * Creates the permissions if it doesn't exist.. updates it if it does.
	// *
	// * @param target
	// * @param isGroup
	// * @param perm
	// * @param zone
	// * @return FALSE if the group, or zone do not exist.
	// */
	// protected synchronized boolean setPermProp(String target, boolean isGroup, PermissionProp perm, String zone)
	// {
	// try
	// {
	// int tID;
	// int zID = getZoneIDFromZoneName(zone);
	//
	// if (isGroup)
	// {
	// tID = getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// tID = getPlayerIDFromPlayerName(target);
	// }
	//
	// if (zID < -4 || tID < -4)
	// {
	// return false;
	// }
	//
	// // target isgroup perm zone >> permProp
	// PreparedStatement use = statementGetPermProp;
	//
	// // check permissions existence...
	// use.setInt(1, tID);
	// use.setBoolean(2, isGroup);
	// use.setString(3, perm.getQualifiedName());
	// use.setInt(4, zID);
	// ResultSet set = use.executeQuery();
	// use.clearParameters();
	//
	// // allowed, target, isgroup, perm, zone
	// if (set.next())
	// {
	// use = statementUpdatePermProp; // exists
	// }
	// else
	// {
	// use = statementPutPermProp; // does not exist.
	// }
	//
	// use.setString(1, perm.value);
	// use.setInt(2, tID);
	// use.setBoolean(3, isGroup);
	// use.setString(4, perm.getQualifiedName());
	// use.setInt(5, zID);
	// use.executeUpdate();
	// use.clearParameters();
	//
	// return true;
	//
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// protected synchronized boolean delZone(String name)
	// {
	// try
	// {
	// int zid = getZoneIDFromZoneName(name);
	//
	// if (zid == -5)
	// {
	// return false;
	// }
	//
	// statementDelZone.setString(1, name);
	// statementDelZone.executeUpdate();
	// statementDelZone.clearParameters();
	//
	// statementDelGroupFromZone.setInt(1, zid);
	// statementDelGroupFromZone.executeUpdate();
	// statementDelGroupFromZone.clearParameters();
	//
	// statementDelGroupConnectorsFromZone.setInt(1, zid);
	// statementDelGroupConnectorsFromZone.executeUpdate();
	// statementDelGroupConnectorsFromZone.clearParameters();
	//
	// statementDelLadderFromZone.setInt(1, zid);
	// statementDelLadderFromZone.executeUpdate();
	// statementDelLadderFromZone.clearParameters();
	//
	// statementDelPermFromZone.setInt(1, zid);
	// statementDelPermFromZone.executeUpdate();
	// statementDelPermFromZone.clearParameters();
	//
	// return true;
	//
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// protected synchronized boolean createZone(String name)
	// {
	// try
	// {
	// statementPutZone.setString(1, name);
	// statementPutZone.executeUpdate();
	// statementPutZone.clearParameters();
	// return true;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// protected synchronized boolean doesZoneExist(String name)
	// {
	// try
	// {
	// int ID = getZoneIDFromZoneName(name);
	//
	// return ID > 0;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// /**
	// * queries the entire DB and make it into nonDB format..
	// *
	// * @return "players" >> arraylist<String> DONE "groups" >> arrayList<Group> DONE "playerPerms" >> arrayList<permHolder> DONE "groupPerms" >>
	// * arrayList<permHolder> DONE "groupConnectors" >> HashMap<String, HashMap<String, ArrayList<String>>> DONE "ladders" >> arraylist<PromotionLadder>
	// * DONE
	// */
	//
	// protected synchronized HashMap<String, Object> dump()
	// {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	//
	// ResultSet set;
	//
	// ArrayList list;
	//
	// // DUMP PLAYERS! ---------------------------------
	// try
	// {
	// set = statementDumpPlayers.executeQuery();
	//
	// list = new ArrayList<String>();
	//
	// while (set.next())
	// {
	// list.add(set.getString(1));
	// }
	//
	// map.put("players", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.severe("[PermSQL] Player dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP GROUPS! -------------------------------------
	// try
	// {
	// set = statementDumpGroups.executeQuery();
	//
	// list = new ArrayList<Group>();
	//
	// int priority;
	// String parent, prefix, suffix, zone, name;
	// int parentID;
	// Group g;
	// while (set.next())
	// {
	// priority = set.getInt(COLUMN_GROUP_PRIORITY);
	// name = set.getString(COLUMN_GROUP_NAME);
	// parentID = set.getInt(COLUMN_GROUP_PARENT);
	// prefix = set.getString(COLUMN_GROUP_PREFIX);
	// suffix = set.getString(COLUMN_GROUP_SUFFIX);
	// zone = set.getString(COLUMN_AREA_NAME);
	// parent = getGroupNameFromGroupID(parentID);
	// g = new Group(name, prefix, suffix, parent, zone, priority);
	// list.add(g);
	// }
	//
	// map.put("groups", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Group dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP PLAYER PERMISSIONS! ------------------------------
	// try
	// {
	// set = statementDumpPlayerPermissions.executeQuery();
	//
	// list = new ArrayList<PermissionHolder>();
	//
	// boolean allowed;
	// String target, zone, perm;
	// PermissionHolder holder;
	// while (set.next())
	// {
	// target = set.getString(COLUMN_PLAYER_UUID);
	// zone = set.getString(COLUMN_AREA_NAME);
	// perm = set.getString(COLUMN_PERMISSION_PERM);
	// allowed = set.getBoolean(COLUMN_PERMISSION_ALLOWED);
	// holder = new PermissionHolder(target, perm, allowed, zone);
	// list.add(holder);
	// }
	//
	// map.put("playerPerms", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Player Permission dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP GROUP PERMISSIONS! ------------------------------
	// try
	// {
	// set = statementDumpGroupPermissions.executeQuery();
	//
	// list = new ArrayList<PermissionHolder>();
	//
	// boolean allowed;
	// String target, zone, perm;
	// PermissionHolder holder;
	// while (set.next())
	// {
	// target = set.getString(COLUMN_GROUP_NAME);
	// zone = set.getString(COLUMN_AREA_NAME);
	// perm = set.getString(COLUMN_PERMISSION_PERM);
	// allowed = set.getBoolean(COLUMN_PERMISSION_ALLOWED);
	// holder = new PermissionHolder(target, perm, allowed, zone);
	// list.add(holder);
	// }
	//
	// map.put("groupPerms", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Group Permission dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP PLAYER PERMPROPS PROPERTIES! ------------------------------
	// try
	// {
	// set = statementDumpPlayerPermProps.executeQuery();
	//
	// list = new ArrayList<PermissionPropHolder>();
	//
	// String target, zone, perm, prop;
	// PermissionPropHolder holder;
	// while (set.next())
	// {
	// target = set.getString(COLUMN_PLAYER_UUID);
	// zone = set.getString(COLUMN_AREA_NAME);
	// perm = set.getString(COLUMN_PERMPROP_PERM);
	// prop = set.getString(COLUMN_PERMPROP_PROP);
	// holder = new PermissionPropHolder(target, perm, prop, zone);
	// list.add(holder);
	// }
	//
	// map.put("playerPermProps", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Player Permission Property dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP GROUP PERMPROP PROPERTIES! ------------------------------
	// try
	// {
	// set = statementDumpGroupPermProps.executeQuery();
	//
	// list = new ArrayList<PermissionPropHolder>();
	//
	// String target, zone, perm, prop;
	// PermissionPropHolder holder;
	// while (set.next())
	// {
	// target = set.getString(COLUMN_GROUP_NAME);
	// zone = set.getString(COLUMN_AREA_NAME);
	// perm = set.getString(COLUMN_PERMPROP_PERM);
	// prop = set.getString(COLUMN_PERMPROP_PROP);
	// holder = new PermissionPropHolder(target, perm, prop, zone);
	// list.add(holder);
	// }
	//
	// map.put("groupPermProps", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Group Permission Property dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP GROUP CONNECTORS! ------------------------------
	// try
	// {
	// set = statementDumpGroupConnector.executeQuery();
	//
	// HashMap<String, HashMap<String, ArrayList<String>>> uberMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
	//
	// String group, zone, player;
	// HashMap<String, ArrayList<String>> gMap;
	// while (set.next())
	// {
	// group = set.getString(COLUMN_GROUP_NAME);
	// zone = set.getString(COLUMN_AREA_NAME);
	// player = set.getString(COLUMN_PLAYER_UUID);
	//
	// gMap = uberMap.get(zone);
	// if (gMap == null)
	// {
	// gMap = new HashMap<String, ArrayList<String>>();
	// uberMap.put(zone, gMap);
	// }
	//
	// list = gMap.get(group);
	// if (list == null)
	// {
	// list = new ArrayList<String>();
	// gMap.put(group, list);
	// }
	//
	// list.add(player);
	// }
	//
	// map.put("groupConnectors", uberMap);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Group Connection dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// // DUMP LADDERS! ------------------------------
	// try
	// {
	// set = statementDumpLadders.executeQuery();
	//
	// // zone, ladder, groupnames
	// HashMap<String, HashMap<String, ArrayList<String>>> uberMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
	//
	// String ladder, zone, group;
	// HashMap<String, ArrayList<String>> gMap;
	// while (set.next())
	// {
	// ladder = set.getString(COLUMN_LADDER_NAME_NAME);
	// zone = set.getString(COLUMN_AREA_NAME);
	// group = set.getString(COLUMN_GROUP_NAME);
	//
	// gMap = uberMap.get(zone);
	// if (gMap == null)
	// {
	// gMap = new HashMap<String, ArrayList<String>>();
	// uberMap.put(zone, gMap);
	// }
	//
	// list = gMap.get(ladder);
	// if (list == null)
	// {
	// list = new ArrayList<String>();
	// gMap.put(ladder, list);
	// }
	//
	// list.add(group);
	// }
	//
	// list = new ArrayList<PromotionLadder>();
	//
	// PromotionLadder lad;
	// String[] holder = new String[] {};
	// for (Entry<String, HashMap<String, ArrayList<String>>> entry1 : uberMap.entrySet())
	// {
	// for (Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet())
	// {
	// if (entry2.getValue().isEmpty())
	// {
	// continue;
	// }
	//
	// lad = new PromotionLadder(entry2.getKey(), entry1.getKey(), entry2.getValue().toArray(holder));
	// list.add(lad);
	// }
	// }
	//
	// map.put("ladders", list);
	// }
	// catch (SQLException e)
	// {
	// OutputHandler.felog.info("[PermSQL] Ladder dump for export failed!");
	// e.printStackTrace();
	// list = null;
	// }
	//
	// return map;
	// }
	//
	// /**
	// * @param username
	// * @return false if SQL error or the player already exists.
	// */
	// public synchronized boolean generatePlayer(String username)
	// {
	//
	// try
	// {
	// if (doesPlayerExist(username))
	// {
	// return false;
	// }
	// else
	// {
	// PreparedStatement statement = statementPutPlayer;
	//
	// statement.setString(1, username);
	// statement.executeUpdate();
	// statement.clearParameters();
	//
	// int player = getPlayerIDFromPlayerName(username);
	// ResultSet set;
	//
	// // copy permisisons
	// {
	// statement = statementGetAllPermissions;
	// HashMultimap<Integer, Permission> perms = HashMultimap.create();
	//
	// statement.setInt(1, player);
	// statement.setBoolean(2, false);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// while (set.next())
	// {
	// perms.put(set.getInt(3), new Permission(set.getString(1), set.getBoolean(2)));
	// }
	//
	// // $ , allowed, target, isgroup, perm, zone
	// statement = statementPutPermission;
	// statement.setInt(2, player);
	// statement.setBoolean(3, false);
	// for (int zone : perms.keySet())
	// {
	// statement.setInt(5, zone);
	// for (Permission perm : perms.get(zone))
	// {
	// statement.setBoolean(1, perm.allowed);
	// statement.setString(4, perm.getQualifiedName());
	// statement.execute();
	// }
	// }
	// statement.clearParameters();
	// }
	//
	// // copy permProps
	// {
	// statement = statementGetAllPermProps;
	// HashMultimap<Integer, PermissionProp> permProps = HashMultimap.create();
	//
	// statement.setInt(1, player);
	// statement.setBoolean(2, false);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// while (set.next())
	// {
	// permProps.put(set.getInt(3), new PermissionProp(set.getString(1), set.getString(2)));
	// }
	//
	// // $ , allowed, target, isgroup, perm, zone
	// statement = statementPutPermProp;
	// statement.setInt(2, player);
	// statement.setBoolean(3, false);
	// for (int zone : permProps.keySet())
	// {
	// statement.setInt(5, zone);
	// for (PermissionProp prop : permProps.get(zone))
	// {
	// statement.setString(1, prop.value);
	// statement.setString(4, prop.getQualifiedName());
	// statement.execute();
	// }
	// }
	// statement.clearParameters();
	// }
	//
	// // copy groups
	// {
	// statement = statementGetAllGroupsForPlayer;
	// HashMultimap<Integer, Integer> groups = HashMultimap.create();
	//
	// statement.setInt(1, player);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// while (set.next())
	// {
	// groups.put(set.getInt(COLUMN_GROUP_CONNECTOR_AREA), set.getInt(COLUMN_GROUP_CONNECTOR_GROUP));
	// }
	//
	// statement = statementPutPlayerInGroup;
	// statement.setInt(2, player);
	// for (int zone : groups.keySet())
	// {
	// statement.setInt(3, zone);
	// for (int group : groups.get(zone))
	// {
	// statement.setInt(1, group);
	// statement.execute();
	// }
	// }
	// }
	//
	// return true;
	// }
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// public synchronized boolean doesPlayerExist(String username)
	// {
	// try
	// {
	// statementGetPlayerIDFromName.setString(1, username);
	// ResultSet set = statementGetPlayerIDFromName.executeQuery();
	// statementGetPlayerIDFromName.clearParameters();
	//
	// if (!set.next())
	// {
	// return false;
	// }
	//
	// return true;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// /**
	// * @param zone
	// * @return -5 if the Zone does not exist.
	// * @throws SQLException
	// */
	// private synchronized int getZoneIDFromZoneName(String zone) throws SQLException
	// {
	// statementGetZoneIDFromName.setString(1, zone);
	// ResultSet set = statementGetZoneIDFromName.executeQuery();
	// statementGetZoneIDFromName.clearParameters();
	//
	// if (!set.next())
	// {
	// return -5;
	// }
	//
	// return set.getInt(1);
	// }
	//
	// /**
	// * @param zoneID
	// * @return null if the Zone does not exist.
	// * @throws SQLException
	// */
	// private synchronized String getZoneNameFromZoneID(int zoneID) throws SQLException
	// {
	// statementGetZoneNameFromID.setInt(1, zoneID);
	// ResultSet set = statementGetZoneNameFromID.executeQuery();
	// statementGetZoneNameFromID.clearParameters();
	//
	// if (!set.next())
	// {
	// return null;
	// }
	//
	// return set.getString(1);
	// }
	//
	// /**
	// * @param ladder
	// * @return -5 if the Ladder does not exist.
	// * @throws SQLException
	// */
	// private synchronized int getLadderIDFromLadderName(String ladder) throws SQLException
	// {
	// statementGetLadderIDFromName.setString(1, ladder);
	// ResultSet set = statementGetLadderIDFromName.executeQuery();
	// statementGetLadderIDFromName.clearParameters();
	//
	// if (!set.next())
	// {
	// return -5;
	// }
	//
	// return set.getInt(1);
	// }
	//
	// /**
	// * @param ladderID
	// * @return null if the Ladder does not exist.
	// * @throws SQLException
	// */
	// private synchronized String getLadderNameFromLadderID(int ladderID) throws SQLException
	// {
	// statementGetLadderNameFromID.setInt(1, ladderID);
	// ResultSet set = statementGetLadderNameFromID.executeQuery();
	// statementGetLadderNameFromID.clearParameters();
	//
	// if (!set.next())
	// {
	// return null;
	// }
	//
	// return set.getString(1);
	// }
	//
	// /**
	// * @return null if the Ladder does not exist.
	// * @throws SQLException
	// */
	// private synchronized int getLadderIdFromGroup(int groupID, int zoneID) throws SQLException
	// {
	// statementGetLadderIDFromGroup.setInt(1, groupID);
	// statementGetLadderIDFromGroup.setInt(2, zoneID);
	// ResultSet set = statementGetLadderIDFromGroup.executeQuery();
	// statementGetLadderIDFromGroup.clearParameters();
	//
	// if (!set.next())
	// {
	// return -5;
	// }
	//
	// return set.getInt(1);
	// }
	//
	// /**
	// * @param group
	// * @return -5 if the Group does not exist.
	// * @throws SQLException
	// */
	// private synchronized int getGroupIDFromGroupName(String group) throws SQLException
	// {
	//
	// statementGetGroupIDFromName.setString(1, group);
	// ResultSet set = statementGetGroupIDFromName.executeQuery();
	// statementGetGroupIDFromName.clearParameters();
	//
	// if (!set.next())
	// {
	// return -5;
	// }
	//
	// return set.getInt(1);
	// }
	//
	// // ---------------------------------------------------------------------------------------------------
	// // ---------------------------------------------------------------------------------------------------
	// // -------------------------------ID <<>> NAME METHODS -----------------------------------------------
	// // ---------------------------------------------------------------------------------------------------
	// // ---------------------------------------------------------------------------------------------------
	//
	// /**
	// * @param player
	// * @return returns the ID of the EntryPlayer if this player does not exist.
	// * @throws SQLException
	// */
	// private synchronized int getPlayerIDFromPlayerName(String player) throws SQLException
	// {
	// statementGetPlayerIDFromName.setString(1, player);
	// ResultSet set = statementGetPlayerIDFromName.executeQuery();
	// statementGetPlayerIDFromName.clearParameters();
	//
	// if (!set.next())
	// {
	// return getPlayerIDFromPlayerName(APIRegistry.perms.getEntryPlayer().toString());
	// }
	//
	// return set.getInt(1);
	// }
	//
	// /**
	// * @param playerID
	// * @return null if the Player does not exist.
	// * @throws SQLException
	// */
	// private synchronized String getPlayerNameFromPlayerID(int playerID) throws SQLException
	// {
	// statementGetPlayerNameFromID.setInt(1, playerID);
	// ResultSet set = statementGetPlayerNameFromID.executeQuery();
	// statementGetPlayerNameFromID.clearParameters();
	//
	// if (!set.next())
	// {
	// return null;
	// }
	//
	// return set.getString(1);
	// }
	//
	// private synchronized void clearPlayerGroupsInZone(int playerID, int zoneID) throws SQLException
	// {
	// statementRemovePlayerGroups.setInt(1, playerID);
	// statementRemovePlayerGroups.setInt(2, zoneID);
	// statementRemovePlayerGroups.executeUpdate();
	// statementRemovePlayerGroups.clearParameters();
	// }
	//
	// /**
	// * removes all the players groups in a zone, and then sets their group to the specified.
	// *
	// * @param group
	// * @param player
	// * @param zone
	// * @return
	// */
	// public synchronized String setPlayerGroup(String group, String player, String zone)
	// {
	// try
	// {
	// int playerID = SqlHelper.getPlayerIDFromPlayerName(player);
	// int groupID = SqlHelper.getGroupIDFromGroupName(group);
	// int zoneID = SqlHelper.getZoneIDFromZoneName(zone);
	//
	// clearPlayerGroupsInZone(playerID, zoneID);
	// return addPlayerGroup(groupID, playerID, zoneID);
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return "Player group not set.";
	// }
	//
	// public synchronized String addPlayerGroup(String group, String player, String zone)
	// {
	// try
	// {
	// int playerID = getPlayerIDFromPlayerName(player);
	// int groupID = getGroupIDFromGroupName(group);
	// int zoneID = getZoneIDFromZoneName(zone);
	// return addPlayerGroup(groupID, playerID, zoneID);
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return "Player not added to group.";
	// }
	//
	// private synchronized String addPlayerGroup(int groupID, int playerID, int zoneID) throws SQLException
	// {
	// statementPutPlayerInGroup.setInt(1, groupID);
	// statementPutPlayerInGroup.setInt(2, playerID);
	// statementPutPlayerInGroup.setInt(3, zoneID);
	// int result = statementPutPlayerInGroup.executeUpdate();
	// statementPutPlayerInGroup.clearParameters();
	//
	// if (result == 0)
	// {
	// return "Row not inserted.";
	// }
	//
	// return null;
	// }
	//
	// public synchronized String removePlayerGroup(String group, String player, String zone)
	// {
	// try
	// {
	// int playerID = getPlayerIDFromPlayerName(player);
	// int groupID = getGroupIDFromGroupName(group);
	// int zoneID = getZoneIDFromZoneName(zone);
	//
	// statementRemovePlayerGroup.setInt(1, playerID);
	// statementRemovePlayerGroup.setInt(2, zoneID);
	// statementRemovePlayerGroup.setInt(3, groupID);
	// int result = statementRemovePlayerGroup.executeUpdate();
	// statementRemovePlayerGroup.clearParameters();
	//
	// if (result == 0)
	// {
	// return "Player not removed from group.";
	// }
	//
	// return null;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return "Player not added to group";
	// }
	//
	// public synchronized String removePermission(String target, boolean isGroup, String node, String zone)
	// {
	// try
	// {
	// int targetID = -5;
	// if (isGroup)
	// {
	// targetID = SqlHelper.getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// targetID = SqlHelper.getPlayerIDFromPlayerName(target);
	// }
	// int zoneID = SqlHelper.getZoneIDFromZoneName(zone);
	//
	// statementDeletePermission.setInt(1, targetID);
	// statementDeletePermission.setBoolean(2, isGroup);
	// statementDeletePermission.setString(3, node);
	// statementDeletePermission.setInt(4, zoneID);
	// statementDeletePermission.executeUpdate();
	// statementDeletePermission.clearParameters();
	// return null;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return "Permission not removed.";
	// }
	//
	// public synchronized String removePermissionProp(String target, boolean isGroup, String node, String zone)
	// {
	// try
	// {
	// int targetID = -5;
	// if (isGroup)
	// {
	// targetID = SqlHelper.getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// targetID = SqlHelper.getPlayerIDFromPlayerName(target);
	// }
	// int zoneID = SqlHelper.getZoneIDFromZoneName(zone);
	//
	// statementDeletePermProp.setInt(1, targetID);
	// statementDeletePermProp.setBoolean(2, isGroup);
	// statementDeletePermProp.setString(3, node);
	// statementDeletePermProp.setInt(4, zoneID);
	// statementDeletePermProp.executeUpdate();
	// statementDeletePermProp.clearParameters();
	// return null;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return "Permission not removed.";
	// }
	//
	// public synchronized void deleteGroupInZone(String group, String zone)
	// {
	// try
	// {
	// int zoneID = getZoneIDFromZoneName(zone);
	// statementDeleteGroupInZone.setString(1, group);
	// statementDeleteGroupInZone.setInt(2, zoneID);
	// statementDeleteGroupInZone.executeUpdate();
	// statementDeleteGroupInZone.clearParameters();
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// }
	//
	// public synchronized ArrayList<Group> getGroupsInZone(String zoneName)
	// {
	// try
	// {
	// TreeSet<Group> set = new TreeSet<Group>();
	// int zID = getZoneIDFromZoneName(zoneName);
	//
	// statementGetGroupsInZone.setInt(1, zID);
	// ResultSet result = statementGetGroupsInZone.executeQuery();
	// statementGetGroupsInZone.clearParameters();
	//
	// int priority;
	// String name, parent, prefix, suffix;
	// Group g;
	//
	// while (result.next())
	// {
	// priority = result.getInt(COLUMN_GROUP_PRIORITY);
	// name = result.getString(COLUMN_GROUP_NAME);
	// parent = result.getString(COLUMN_GROUP_PARENT);
	// prefix = result.getString(COLUMN_GROUP_PREFIX);
	// suffix = result.getString(COLUMN_GROUP_SUFFIX);
	// g = new Group(name, prefix, suffix, parent, zoneName, priority);
	// set.add(g);
	// }
	//
	// ArrayList<Group> list = new ArrayList<Group>();
	// list.addAll(set);
	// return list;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }
	//
	// public static String getPermission(String target, boolean isGroup, String perm, String zone)
	// {
	// try
	// {
	// int tID;
	// int zID = getZoneIDFromZoneName(zone);
	// int isG = isGroup ? 1 : 0;
	// PreparedStatement statement = statementGetPermission;
	// ResultSet set;
	//
	// if (isGroup)
	// {
	// tID = getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// tID = getPlayerIDFromPlayerName(target);
	// }
	//
	// if (zID < -4 || tID < -4)
	// {
	// return "Zone or target invalid.";
	// }
	//
	// // initial check.
	// statement.setInt(1, tID);
	// statement.setInt(2, isG);
	// statement.setString(3, perm);
	// statement.setInt(4, zID);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// if (set.next())
	// {
	// return set.getBoolean(COLUMN_PERMISSION_ALLOWED) ? "allowed" : "denied";
	// }
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// public static String getPermissionProp(String target, boolean isGroup, String perm, String zone)
	// {
	// try
	// {
	// int tID;
	// int zID = getZoneIDFromZoneName(zone);
	// int isG = isGroup ? 1 : 0;
	// PreparedStatement statement = statementGetPermProp;
	// ResultSet set;
	//
	// if (isGroup)
	// {
	// tID = getGroupIDFromGroupName(target);
	// }
	// else
	// {
	// tID = getPlayerIDFromPlayerName(target);
	// }
	//
	// if (zID < -4 || tID < -4)
	// {
	// return "Zone or target invalid.";
	// }
	//
	// // initial check.
	// statement.setInt(1, tID);
	// statement.setInt(2, isG);
	// statement.setString(3, perm);
	// statement.setInt(4, zID);
	// set = statement.executeQuery();
	// statement.clearParameters();
	//
	// if (set.next())
	// {
	// return set.getString(COLUMN_PERMPROP_PROP);
	// }
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// public static Collection<Permission> getAllPermissions(String target, String zone, boolean isGroup)
	// {
	// ArrayList<Permission> list = new ArrayList<Permission>();
	// PreparedStatement statement = statementGetAllPermissionsInZone;
	// try
	// {
	// int targetID = isGroup ? getGroupIDFromGroupName(target) : getPlayerIDFromPlayerName(target);
	// if (targetID == -5)
	// {
	// return list;
	// }
	//
	// int zoneID = getZoneIDFromZoneName(zone);
	// if (zoneID == -5)
	// {
	// return list;
	// }
	//
	// statement.setInt(1, targetID);
	// statement.setInt(2, zoneID);
	// statement.setBoolean(3, isGroup);
	// ResultSet set = statement.executeQuery();
	// statement.clearParameters();
	//
	// while (set.next())
	// {
	// list.add(new Permission(set.getString(1), set.getBoolean(2)));
	// }
	//
	// return list;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// public static ArrayList<PermissionProp> getAllPermProps(String target, String zone, boolean isGroup)
	// {
	// ArrayList<PermissionProp> list = new ArrayList<PermissionProp>();
	// PreparedStatement statement = statementGetAllPermPropsInZone;
	// try
	// {
	// int targetID = isGroup ? getGroupIDFromGroupName(target) : getPlayerIDFromPlayerName(target);
	// if (targetID == -5)
	// {
	// return list;
	// }
	//
	// int zoneID = getZoneIDFromZoneName(zone);
	// if (zoneID == -5)
	// {
	// return list;
	// }
	//
	// statement.setInt(1, targetID);
	// statement.setInt(2, zoneID);
	// statement.setBoolean(3, isGroup);
	// ResultSet set = statement.executeQuery();
	// statement.clearParameters();
	//
	// while (set.next())
	// {
	// list.add(new PermissionProp(set.getString(1), set.getString(2)));
	// }
	//
	// return list;
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// /**
	// * Create the tables needed.
	// */
	// private void generate()
	// {
	// try
	// {
	// String zoneTable, groupTable, ladderTable, ladderNameTable, playerTable, groupConnectorTable, permissionTable, permPropTable;
	//
	// // ------------------
	// // H2 & MYSQL
	// // ------------------
	//
	// zoneTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_AREA).append("(").append(COLUMN_AREA_ID)
	// .append(" INTEGER AUTO_INCREMENT, ").append(COLUMN_AREA_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ").append("PRIMARY KEY (")
	// .append(COLUMN_AREA_ID).append(") ").append(")").toString();
	//
	// groupTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_GROUP).append("(").append(COLUMN_GROUP_ID)
	// .append(" INTEGER AUTO_INCREMENT, ").append(COLUMN_GROUP_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ").append(COLUMN_GROUP_PARENT)
	// .append(" INTEGER, ").append(COLUMN_GROUP_PRIORITY).append(" SMALLINT NOT NULL, ").append(COLUMN_GROUP_AREA).append(" INTEGER NOT NULL, ")
	// .append(COLUMN_GROUP_PREFIX).append(" VARCHAR(20) DEFAULT '', ").append(COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) DEFAULT '', ")
	// .append("PRIMARY KEY (").append(COLUMN_GROUP_ID).append(") ").append(") ").toString();
	//
	// ladderTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_LADDER).append("(").append(COLUMN_LADDER_LADDERID)
	// .append(" INTEGER NOT NULL, ").append(COLUMN_LADDER_GROUPID).append(" INTEGER NOT NULL, ").append(COLUMN_LADDER_AREA)
	// .append(" INTEGER NOT NULL, ").append(COLUMN_LADDER_RANK).append(" SMALLINT NOT NULL").append(") ").toString();
	//
	// ladderNameTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_LADDER_NAME).append("(").append(COLUMN_LADDER_NAME_ID)
	// .append(" INTEGER AUTO_INCREMENT, ").append(COLUMN_LADDER_NAME_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ").append("PRIMARY KEY (")
	// .append(COLUMN_LADDER_NAME_ID).append(") ").append(")").toString();
	//
	// playerTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_PLAYER).append("(").append(COLUMN_PLAYER_PLAYERID)
	// .append(" INTEGER AUTO_INCREMENT, ").append(COLUMN_PLAYER_UUID).append(" VARCHAR(42) NOT NULL UNIQUE, ").append("PRIMARY KEY (")
	// .append(COLUMN_PLAYER_PLAYERID).append(") ").append(")").toString();
	//
	// groupConnectorTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_GROUP_CONNECTOR).append("(")
	// .append(COLUMN_GROUP_CONNECTOR_GROUP).append(" INTEGER NOT NULL, ").append(COLUMN_GROUP_CONNECTOR_PLAYER).append(" INTEGER NOT NULL, ")
	// .append(COLUMN_GROUP_CONNECTOR_AREA).append(" INTEGER NOT NULL").append(")").toString();
	//
	// permissionTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_PERMISSION).append("(").append(COLUMN_PERMISSION_TARGET)
	// .append(" INTEGER NOT NULL, ").append(COLUMN_PERMISSION_ISGROUP).append(" BOOLEAN NOT NULL, ").append(COLUMN_PERMISSION_PERM)
	// .append(" VARCHAR(255) NOT NULL, ").append(COLUMN_PERMISSION_ALLOWED).append(" BOOLEAN NOT NULL, ").append(COLUMN_PERMISSION_ZONEID)
	// .append(" INTEGER NOT NULL").append(")").toString();
	//
	// permissionTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_PERMISSION).append("(").append(COLUMN_PERMISSION_TARGET)
	// .append(" INTEGER NOT NULL, ").append(COLUMN_PERMISSION_ISGROUP).append(" BOOLEAN NOT NULL, ").append(COLUMN_PERMISSION_PERM)
	// .append(" VARCHAR(255) NOT NULL, ").append(COLUMN_PERMISSION_ALLOWED).append(" BOOLEAN NOT NULL, ").append(COLUMN_PERMISSION_ZONEID)
	// .append(" INTEGER NOT NULL").append(")").toString();
	//
	// permPropTable = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_PERMPROP).append("(").append(COLUMN_PERMPROP_TARGET)
	// .append(" INTEGER NOT NULL, ").append(COLUMN_PERMPROP_ISGROUP).append(" BOOLEAN NOT NULL, ").append(COLUMN_PERMPROP_PERM)
	// .append(" VARCHAR(255) NOT NULL, ").append(COLUMN_PERMPROP_PROP).append(" VARCHAR(255) NOT NULL, ").append(COLUMN_PERMPROP_ZONEID)
	// .append(" INTEGER NOT NULL").append(")").toString();
	//
	// // create the tables.
	// db.createStatement().executeUpdate(zoneTable);
	// db.createStatement().executeUpdate(groupTable);
	// db.createStatement().executeUpdate(ladderTable);
	// db.createStatement().executeUpdate(ladderNameTable);
	// db.createStatement().executeUpdate(playerTable);
	// db.createStatement().executeUpdate(groupConnectorTable);
	// db.createStatement().executeUpdate(permissionTable);
	// db.createStatement().executeUpdate(permPropTable);
	//
	// // DEFAULT group
	// StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (").append(COLUMN_GROUP_ID).append(", ")
	// .append(COLUMN_GROUP_NAME).append(", ").append(COLUMN_GROUP_PRIORITY).append(", ").append(COLUMN_GROUP_AREA).append(") ")
	// .append(" VALUES ").append(" (").append(DEFAULT_ID).append(", ") // groupID
	// .append("'").append(APIRegistry.perms.getDEFAULT().name).append("', ").append("0, ").append(GLOBAL_ID).append(")"); // priority, zone
	// db.createStatement().executeUpdate(query.toString());
	//
	// // GLOBAL zone
	// query = new StringBuilder("INSERT INTO ").append(TABLE_AREA).append(" (").append(COLUMN_AREA_NAME).append(", ").append(COLUMN_AREA_ID).append(") ")
	// .append(" VALUES ").append(" ('").append(APIRegistry.permissionManager.getGlobalZone().getName()).append("', ").append(GLOBAL_ID).append(")");
	// db.createStatement().executeUpdate(query.toString());
	//
	// // SUPER zone
	// query = new StringBuilder("INSERT INTO ").append(TABLE_AREA).append(" (").append(COLUMN_AREA_NAME).append(", ").append(COLUMN_AREA_ID).append(") ")
	// .append(" VALUES ").append(" ('").append(APIRegistry.permissionManager.getSUPER().getName()).append("', ").append(SUPER_ID).append(")");
	// db.createStatement().executeUpdate(query.toString());
	//
	// // Entry player...
	// query = new StringBuilder("INSERT INTO ").append(TABLE_PLAYER).append(" (").append(COLUMN_PLAYER_UUID).append(", ").append(COLUMN_PLAYER_PLAYERID)
	// .append(") ").append(" VALUES ").append(" ('").append(APIRegistry.perms.getEntryPlayer()).append("', ").append(ENTRY_PLAYER_ID).append(")");
	// db.createStatement().executeUpdate(query.toString());
	//
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// throw new RuntimeException(e.getMessage());
	// }
	// }
	//
	// /**
	// * If generation is enabled, puts the provided Registered permissions into the DB.
	// *
	// * @param regPerms
	// */
	// protected synchronized void putRegistrationPerms(HashMultimap<RegGroup, PermissionChecker> regPerms)
	// {
	// if (!generate)
	// {
	// return;
	// }
	// try
	// {
	// OutputHandler.felog.info(" Inserting registration permissions into Permissions DB");
	//
	// // make a statement to be used later.. just easier...
	// PreparedStatement s;
	//
	// // create default groups...
	// StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_PERMISSION).append(" (").append(COLUMN_PERMISSION_TARGET).append(", ")
	// .append(COLUMN_PERMISSION_ALLOWED).append(", ").append(COLUMN_PERMISSION_PERM).append(", ").append(COLUMN_PERMISSION_ISGROUP).append(", ")
	// .append(COLUMN_PERMISSION_ZONEID).append(") ").append(" VALUES ").append(" (?, ?, ?, TRUE, ").append(GLOBAL_ID).append(")");
	// PreparedStatement permStatement = prepareStatement(query.toString());
	//
	// query = new StringBuilder("INSERT INTO ").append(TABLE_PERMPROP).append(" (").append(COLUMN_PERMPROP_TARGET).append(", ")
	// .append(COLUMN_PERMPROP_PROP).append(", ").append(COLUMN_PERMPROP_PERM).append(", ").append(COLUMN_PERMPROP_ISGROUP).append(", ")
	// .append(COLUMN_PERMPROP_ZONEID).append(") ").append(" VALUES ").append(" (?, ?, ?, TRUE, ").append(GLOBAL_ID).append(")");
	// PreparedStatement permPropStatement = prepareStatement(query.toString());
	//
	// // create groups...
	// HashMap<RegGroup, Integer> groups = new HashMap<RegGroup, Integer>();
	// int NUM;
	// for (RegGroup group : RegGroup.values())
	// {
	// if (group.equals(RegGroup.ZONE))
	// {
	// groups.put(group, DEFAULT_ID);
	// continue;
	// }
	//
	// createGroup(group.getGroup());
	// NUM = getGroupIDFromGroupName(group.toString());
	// groups.put(group, NUM);
	// }
	//
	// // register permissions
	// for (RegGroup group : regPerms.keySet())
	// {
	// permStatement.setInt(1, groups.get(group));
	// permPropStatement.setInt(1, groups.get(group));
	// for (PermissionChecker perm : regPerms.get(group))
	// {
	// if (perm instanceof PermissionProp)
	// {
	// permPropStatement.setString(2, ((PermissionProp) perm).value);
	// permPropStatement.setString(3, perm.getQualifiedName());
	// permPropStatement.executeUpdate();
	// }
	// else
	// {
	// permStatement.setBoolean(2, ((Permission) perm).allowed);
	// permStatement.setString(3, perm.getQualifiedName());
	// permStatement.executeUpdate();
	// }
	// }
	// }
	//
	// // put the EntryPlayer to GUESTS for the GLOBAL zone
	// s = statementPutPlayerInGroup;
	// s.setInt(1, groups.get(RegisteredPermValue.TRUE));
	// s.setInt(2, ENTRY_PLAYER_ID);
	// s.setInt(3, GLOBAL_ID); // zoneID
	// s.executeUpdate();
	// s.clearParameters();
	//
	// // make default ladder
	// s = statementPutLadderName;
	// s.setString(1, RegGroup.LADDER);
	// s.executeUpdate();
	// s.clearParameters();
	//
	// // add groups to ladder
	// s = statementPutLadder;
	// s.setInt(2, GLOBAL_ID); // zone
	// s.setInt(4, 1); // the ladderID
	// {
	// // Owner
	// s.setInt(1, groups.get(RegisteredPermValue.OP));
	// s.setInt(3, 1);
	// s.executeUpdate();
	//
	// // ZoneAdmin
	// s.setInt(1, groups.get(RegisteredPermValue.OP));
	// s.setInt(3, 2);
	// s.executeUpdate();
	//
	// // Member
	// s.setInt(1, groups.get(RegisteredPermValue.TRUE));
	// s.setInt(3, 3);
	// s.executeUpdate();
	//
	// // Guest
	// s.setInt(1, groups.get(RegisteredPermValue.TRUE));
	// s.setInt(3, 4);
	// s.executeUpdate();
	// }
	// s.clearParameters();
	//
	// OutputHandler.felog.info(" Registration permissions successfully inserted");
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// throw new RuntimeException(e.getMessage());
	// }
	//
	// ExportThread t = new ExportThread("generated", null);
	// t.run();
	// }
	//
	// /**
	// * "players" >> arraylist<String> DONE "groups" >> TreeSet<Group> DONE "playerPerms" >> arrayList<permHolder> DONE "groupPerms" >> arrayList<permHolder>
	// * DONE "groupConnectors" >> HashMap<String, HashMap<String, String[]>> DONE "ladders" >> arraylist<PromotionLadder> DONE
	// */
	//
	// protected void importPerms(String importDir)
	// {
	// try
	// {
	// File file = new File(ModulePermissions.permsFolder, importDir);
	// OutputHandler.felog.info("[PermSQL] Importing permissions from " + importDir);
	//
	// FlatFileGroups g = new FlatFileGroups(file);
	// HashMap<String, Object> map = g.load();
	//
	// FlatFilePlayers p = new FlatFilePlayers(file);
	// map.put("players", p.load());
	//
	// FlatFilePermissions pm = new FlatFilePermissions(file);
	// map.putAll(pm.load());
	//
	// FlatFilePermProps pmp = new FlatFilePermProps(file);
	// map.putAll(pmp.load());
	//
	// OutputHandler.felog.info("[PermSQL] Loaded Configs into ram");
	//
	// // KILL ALL DE DATA!!!!
	// db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_PERMISSION);
	// db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_GROUP);
	// db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_LADDER_NAME);
	// db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_GROUP_CONNECTOR);
	// db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_PLAYER);
	// OutputHandler.felog.info("[PermSQL] Cleaned tables of existing data");
	//
	// // call generate to remake the stuff that should be there
	// {
	//
	// // recreate EntryPlayer player
	// StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_PLAYER).append(" (").append(COLUMN_PLAYER_UUID).append(", ")
	// .append(COLUMN_PLAYER_PLAYERID).append(") ").append(" VALUES ").append(" ('").append(APIRegistry.perms.getEntryPlayer()).append("', ")
	// .append(ENTRY_PLAYER_ID).append(")");
	// db.createStatement().executeUpdate(query.toString());
	//
	// // recreate DEFAULT group
	// query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (").append(COLUMN_GROUP_ID).append(", ").append(COLUMN_GROUP_NAME)
	// .append(", ").append(COLUMN_GROUP_PRIORITY).append(", ").append(COLUMN_GROUP_AREA).append(") ").append(" VALUES ").append(" (")
	// .append(DEFAULT_ID).append(", ") // groupID
	// .append("'").append(APIRegistry.perms.getDEFAULT().name).append("', ").append("0, ").append(GLOBAL_ID).append(")"); // priority, zone
	// db.createStatement().executeUpdate(query.toString());
	// }
	//
	// // create players...
	// PreparedStatement s = statementPutPlayer;
	// for (String player : (ArrayList<String>) map.get("players"))
	// {
	// s.setString(1, player);
	// s.executeUpdate();
	// }
	// s.clearParameters();
	// OutputHandler.felog.info("[PermSQL] Imported players");
	//
	// // create groups
	// s = statementPutPlayerInGroup;
	// HashMap<String, HashMap<String, String[]>> playerMap = (HashMap<String, HashMap<String, String[]>>) map.get("connector");
	// HashMap<String, String[]> temp;
	// String[] list;
	//
	// for (Group group : (ArrayList<Group>) map.get("groups"))
	// {
	// if (group.name.equals(APIRegistry.perms.getDEFAULT().name))
	// {
	// continue;
	// }
	//
	// createGroup(new Group(group.name, group.prefix, group.suffix, null, group.zoneName, group.priority));
	// s.setInt(1, getGroupIDFromGroupName(group.name));
	// s.setInt(3, getZoneIDFromZoneName(group.zoneName));
	//
	// temp = playerMap.get(group.zoneName);
	// if (temp == null)
	// {
	// continue;
	// }
	//
	// list = temp.get(group.name);
	//
	// if (list == null)
	// {
	// continue;
	// }
	//
	// // add the players to the groups as well.
	// for (String player : list)
	// {
	// s.setInt(2, getPlayerIDFromPlayerName(player));
	// s.executeUpdate();
	// }
	//
	// }
	//
	// // update groups with true parents..
	// for (Group group : (ArrayList<Group>) map.get("groups"))
	// {
	// updateGroup(group);
	// }
	// OutputHandler.felog.info("[PermSQL] Imported groups");
	//
	// // add groups to ladders and stuff
	// s = statementPutLadderName;
	// PreparedStatement s2 = statementPutLadder; // groupID, zoneID, rank,
	// // ladderID
	// for (PromotionLadder ladder : (ArrayList<PromotionLadder>) map.get("ladders"))
	// {
	// s.setString(1, ladder.name);
	// s.executeUpdate();
	// s2.setInt(4, getLadderIDFromLadderName(ladder.name));
	// s2.setInt(2, getZoneIDFromZoneName(ladder.zoneID));
	//
	// list = ladder.getListGroup();
	// for (int i = 0; i < list.length; i++)
	// {
	// s2.setInt(3, i);
	// s2.setInt(1, getGroupIDFromGroupName(list[i]));
	// s2.executeUpdate();
	// }
	// }
	// OutputHandler.felog.info("[PermSQL] Imported ladders");
	//
	// // now the permissions
	// ArrayList<PermissionHolder> perms = (ArrayList<PermissionHolder>) map.get("playerPerms");
	// for (PermissionHolder perm : perms)
	// {
	// setPermission(perm.target, false, perm, perm.zone);
	// }
	//
	// perms = (ArrayList<PermissionHolder>) map.get("groupPerms");
	// for (PermissionHolder perm : perms)
	// {
	// setPermission(perm.target, true, perm, perm.zone);
	// }
	// OutputHandler.felog.info("[PermSQL] Imported permissions");
	//
	// // now the permissions
	// ArrayList<PermissionPropHolder> props = (ArrayList<PermissionPropHolder>) map.get("playerPermProps");
	// for (PermissionPropHolder perm : props)
	// {
	// setPermProp(perm.target, false, perm, perm.zone);
	// }
	//
	// props = (ArrayList<PermissionPropHolder>) map.get("groupPermProps");
	// for (PermissionPropHolder perm : props)
	// {
	// setPermProp(perm.target, true, perm, perm.zone);
	// }
	// OutputHandler.felog.info("[PermSQL] Imported permissions properties");
	//
	// OutputHandler.felog.info("[PermSQL] Import successful!");
	// }
	// catch (SQLException e)
	// {
	// e.printStackTrace();
	// throw new RuntimeException(e.getMessage());
	// }
	// }

	// group connector table.
	private static final String COLUMN_GROUP_CONNECTOR_GROUP = "group_id";
	private static final String COLUMN_GROUP_CONNECTOR_PLAYER = "player_id";
	private static final String COLUMN_GROUP_CONNECTOR_AREA = "area_id";

	// ladder table
	private static final String COLUMN_LADDER_LADDERID = "ladder_id";
	private static final String COLUMN_LADDER_GROUPID = "group_id";
	private static final String COLUMN_LADDER_AREA = "area_id";
	private static final String COLUMN_LADDER_RANK = "ladder_rank";

	// ladderName table
	private static final String COLUMN_LADDER_NAME_ID = "ladder_id";
	private static final String COLUMN_LADDER_NAME_NAME = "ladder_name";

	// permissions table
	private static final String COLUMN_PERMISSION_TARGET = "target";
	private static final String COLUMN_PERMISSION_ISGROUP = "isGroup";
	private static final String COLUMN_PERMISSION_PERM = "perm";
	private static final String COLUMN_PERMISSION_ALLOWED = "allowed";
	private static final String COLUMN_PERMISSION_ZONEID = "zoneID";

	// permprop table
	private static final String COLUMN_PERMPROP_TARGET = "target";
	private static final String COLUMN_PERMPROP_ISGROUP = "isGroup";
	private static final String COLUMN_PERMPROP_PERM = "perm";
	private static final String COLUMN_PERMPROP_PROP = "property";
	private static final String COLUMN_PERMPROP_ZONEID = "zoneID";

	// zones
	private PreparedStatement statementGetZoneIDFromName; // zoneName >> zoneID
	private PreparedStatement statementGetZoneNameFromID; // zoneID >> zoneName
	private PreparedStatement statementPutZone; // $ ZoneName
	private PreparedStatement statementDelZone; // X ZoneName

	// players
	private PreparedStatement statementGetPlayerIDFromName; // playerName >> playerID
	private PreparedStatement statementGetPlayerNameFromID; // playerID >> playerName
	private PreparedStatement statementPutPlayer; // $ usernName
	private PreparedStatement statementRemovePlayerGroups;
	private PreparedStatement statementPutPlayerInGroup;
	private PreparedStatement statementRemovePlayerGroup;

	// groups
	private PreparedStatement statementGetGroupIDFromName; // groupName >> groupID
	private PreparedStatement statementGetGroupsForPlayerInZone; // PlayerID, ZoneID >> Groups
	private PreparedStatement statementGetAllGroupsForPlayer; // groupid, playerid >> players
	private PreparedStatement stmtGetPlayersByGroup; // PlayerID >> all
	private PreparedStatement statementPutGroup; // $ name, prefix, suffix, parent, priority, zone
	private PreparedStatement statementUpdateGroup; // $ name, prefix, suffix, parent, priority, zone
	private PreparedStatement statementDeleteGroupInZone;

	// ladders
	private PreparedStatement statementGetLadderIDFromName; // ladderName >> ladderID
	private PreparedStatement statementGetLadderNameFromID; // LadderID >> ladderName
	private PreparedStatement statementGetLadderIDFromGroup; // groupID, zoneID >> ladderID
	private PreparedStatement statementGetLadderList; // LadderID, ZoneID >> groupName, rank
	private PreparedStatement statementGetGroupsFromLadder; // PlayerID, LadderID >> group
	private PreparedStatement statementGetGroupsFromLadderAndZone; // PlayerID, LadderID, ZoneID >> group
	private PreparedStatement statementGetGroupsFromZone; // PlayerID, ZoneID >> group
	private PreparedStatement statementGetGroupsFromPlayer; // PlayerID >> group
	private PreparedStatement statementPutLadderName; // $ LadderName
	private PreparedStatement statementPutLadder; // $ groupid, zoneID, rank, ladderID

	// dump statements... replace ALL ids with names...
	private PreparedStatement statementDumpGroups;
	private PreparedStatement statementDumpPlayers;
	private PreparedStatement statementDumpGroupPermissions;
	private PreparedStatement statementDumpGroupPermProps;
	private PreparedStatement statementDumpPlayerPermissions;
	private PreparedStatement statementDumpPlayerPermProps;
	private PreparedStatement statementDumpGroupConnector;
	private PreparedStatement statementDumpLadders;

	// zone deletion statements
	private PreparedStatement statementDelPermFromZone;
	private PreparedStatement statementDelLadderFromZone;
	private PreparedStatement statementDelGroupFromZone;
	private PreparedStatement statementDelGroupConnectorsFromZone;
}
