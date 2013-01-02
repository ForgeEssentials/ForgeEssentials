package com.ForgeEssentials.permission;

import com.ForgeEssentials.util.OutputHandler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;

public class SqlLiteHelper
{
	// TODO: make configureable.
	private static File				file							= new File(ModulePermissions.permsFolder, "permissions.db");

	private String					DriverClass						= "org.sqlite.JDBC";
	private Connection				db;
	private boolean					generate						= false;
	private static SqlLiteHelper	instance;

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
	private static final String		COLUMN_PERMISSIONS_TARGET		= "target";
	private static final String		COLUMN_PERMISSIONS_ISGROUP		= "isGroup";
	private static final String		COLUMN_PERMISSIONS_PERM			= "perm";
	private static final String		COLUMN_PERMISSIONS_ALLOWED		= "allowed";
	private static final String		COLUMN_PERMISSIONS_ZONEID		= "zoneID";
	
	// zones
	private final PreparedStatement statementGetZoneIDFromName; // zoneName >> zoneID
	private final PreparedStatement statementGetZoneNameFromID; // zoneID >> zoneName
	private final PreparedStatement statementPutZone; // ZoneName
	
	// players
	private final PreparedStatement statementGetPlayerIDFromName; // playerName >> playerID
	private final PreparedStatement statementGetPlayerNameFromID; // playerID >> playerName
	private final PreparedStatement statementPutPlayer; // ZoneName
	
	// groups
	private final PreparedStatement statementGetGroupIDFromName; // groupName >> groupID
	private final PreparedStatement statementGetGroupNameFromID; // groupID >> groupName
	private final PreparedStatement statementGetGroupFromName; // groupName >> Group
	private final PreparedStatement statementGetGroupFromID; // groupID >> Group
	
	// ladders
	private final PreparedStatement statementGetLadderIDFromName; // ladderName  >> ladderID
	private final PreparedStatement statementGetLadderNameFromID; // LadderID >> ladderName
	private final PreparedStatement statementGetLadderIDFromGroup; // groupID, zoneID  >> ladderID
	private final PreparedStatement statementGetLadderList; // LadderID, ZoneID >> groupName, rank
	private final PreparedStatement statementPutLadder; // ZoneName

	public SqlLiteHelper()
	{
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
					.append(" WHERE ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=").append("?")
					.append(" AND ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_ZONEID).append("=").append("?")
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
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
					.append(" WHERE ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append("=").append("'?'")
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME);
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
					.append(" WHERE ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID).append("=").append("?")
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME);
			statementGetGroupFromID = instance.db.prepareStatement(query.toString());
			
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

	protected void connect()
	{
		try
		{
			if (!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
				OutputHandler.SOP("Permissions db file not found, creating.");
				generate = true;
			}

			if (db != null)
			{
				db.close();
				db = null;
			}

			Class driverClass = Class.forName(DriverClass);

			this.db = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			throw new RuntimeException(e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the SQLite JDBC Driver! Does it exist in the lib directory?");
			throw new RuntimeException(e.getMessage());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}
	}

	// create tables.
	protected void generate()
	{
		try
		{
			// table creation statements.
			String zoneTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_ZONE).append("(")
					.append(this.COLUMN_ZONE_ZONEID).append(" INTEGER AUTOINCREMENT, ")
					.append(this.COLUMN_ZONE_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_ZONE_ZONEID).append(") ")
					.append(")").toString();

			String groupTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
					.append(this.COLUMN_GROUP_GROUPID).append("  INTEGER AUTOINCREMENT, ")
					.append(this.COLUMN_GROUP_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append(this.COLUMN_GROUP_PARENT).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_GROUP_PRIORITY).append(" SMALLINT NOT NULL, ")
					.append(this.COLUMN_GROUP_ZONE).append(" INTEGER NOT NULL")
					.append(this.COLUMN_GROUP_PREFIX).append(" VARCHAR(20) DEFAULT \"\", ")
					.append(this.COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) DEFAULT \"\", ")
					.append("PRIMARY KEY (").append(COLUMN_GROUP_GROUPID).append(") ")
					.append(") ").toString();

			String ladderTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER).append("(")
					.append(this.COLUMN_LADDER_LADDERID).append(" INTEGER, ")
					.append(this.COLUMN_LADDER_GROUPID).append(" INTEGER ,")
					.append(this.COLUMN_LADDER_ZONEID).append(" INTEGER, ")
					.append(this.COLUMN_LADDER_RANK).append(" SMALLINT, ")
					.append(") ").toString();

			String ladderNameTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER_NAME).append("(")
					.append(this.COLUMN_LADDER_NAME_LADDERID).append(" INTEGER AUTOINCREMENT, ")
					.append(this.COLUMN_LADDER_NAME_NAME).append(" VARCHAR(40) NOT NULL UNIQUE")
					.append("PRIMARY KEY (").append(COLUMN_LADDER_NAME_LADDERID).append(") ")
					.append(")").toString();

			String playerTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
					.append(this.COLUMN_PLAYER_PLAYERID).append(" INTEGER AUTOINCREMENT, ")
					.append(this.COLUMN_PLAYER_USERNAME).append(" VARCHAR(20) NOT NULL UNIQUE")
					.append("PRIMARY KEY (").append(COLUMN_PLAYER_PLAYERID).append(") ")
					.append(")").toString();

			String groupConnectorTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP_CONNECTOR).append("(")
					.append(this.COLUMN_GROUP_CONNECTOR_GROUPID).append(" INTEGER, ")
					.append(this.COLUMN_GROUP_CONNECTOR_PLAYERID).append(" INTEGER, ")
					.append(this.COLUMN_GROUP_CONNECTOR_ZONEID).append(" INTEGER, ")
					.append(" )").toString();

			String permissionTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PERMISSION).append("(")
					.append(this.COLUMN_PERMISSIONS_TARGET).append(" INTEGER, ")
					.append(this.COLUMN_PERMISSIONS_ISGROUP).append(" TINYINT(1), ")
					.append(this.COLUMN_PERMISSIONS_PERM).append(" TEXT, ")
					.append(this.COLUMN_PERMISSIONS_ALLOWED).append(" TINYINT(1), ")
					.append(this.COLUMN_PERMISSIONS_ZONEID).append(" INTEGER, ")
					.append(")").toString();

			// create the tables.
			db.createStatement().execute(zoneTable);
			db.createStatement().execute(groupTable);
			db.createStatement().execute(ladderTable);
			db.createStatement().execute(ladderNameTable);
			db.createStatement().execute(playerTable);
			db.createStatement().execute(groupConnectorTable);
			db.createStatement().execute(permissionTable);
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
	public static synchronized PromotionLadder getLadderForGroup(String group, String zone)
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * @param groupName
	 * @return NULL if no group in existence. or an SQL error hapenned.
	 */
	public static synchronized Group getGroupForName(String group)
	{
		try
		{
			// setup query for List
			instance.statementGetGroupFromName.setString(1, group);
			ResultSet set = instance.statementGetGroupFromName.executeQuery();
			instance.statementGetGroupFromName.clearParameters();
			
			if (!set.next())
				return null;
			
			Group g = createGroupFromRow(set, group);
			return g;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * @param groupID
	 * @return NULL if no group in existence, or an SQL erorr happenend.
	 */
	public static synchronized Group getGroupForID(int group)
	{
		try
		{
			// setup query for List
			instance.statementGetGroupFromID.setInt(1, group);
			ResultSet set = instance.statementGetGroupFromID.executeQuery();
			instance.statementGetGroupFromID.clearParameters();
			
			if (!set.next())
				return null;
			
			Group g = createGroupFromRow(set);
			return g;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// --------------------------PRIVATE ---- CREATION ---- METHODS --------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	/**
	 * The set must be joined with the Zone table for converting the ZoneID to name.
	 * This method is used when you want to get the Group from the 
	 * @return a created group
	 * @throws SQLException
	 */
	private static synchronized Group createGroupFromRow(ResultSet set, String groupName) throws SQLException
	{
		int priority = set.getInt(COLUMN_GROUP_PRIORITY);
		String parent = set.getString(COLUMN_GROUP_PARENT);
		String prefix = set.getString(COLUMN_GROUP_PREFIX);
		String suffix = set.getString(COLUMN_GROUP_SUFFIX);
		String zone = set.getString(COLUMN_ZONE_NAME);
		return new Group(groupName, prefix, suffix, parent, zone, priority);
	}
	
	/**
	 * The set must be joined with the Zone table for converting the ZoneID to name.
	 * This method does not move the cursor, and only looks at the row the cursos is on.
	 * @return a created group
	 * @throws SQLException
	 */
	private static synchronized Group createGroupFromRow(ResultSet set) throws SQLException
	{
		int priority = set.getInt(COLUMN_GROUP_PRIORITY);
		String name = set.getString(COLUMN_GROUP_NAME);
		String parent = set.getString(COLUMN_GROUP_PARENT);
		String prefix = set.getString(COLUMN_GROUP_PREFIX);
		String suffix = set.getString(COLUMN_GROUP_SUFFIX);
		String zone = set.getString(COLUMN_ZONE_NAME);
		return new Group(name, prefix, suffix, parent, zone, priority);
	}
	
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// -------------------------------ID <<>> NAME  METHODS ----------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	/**
	 * @param zone
	 * @return -5 if the Zone does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getZoneIDFromZoneName(String zone) throws SQLException
	{
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
	 * @return -5 if the Player does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getPlayerIDFromPlayerName(String player) throws SQLException
	{
		instance.statementGetPlayerIDFromName.setString(1, player);
		ResultSet set = instance.statementGetPlayerIDFromName.executeQuery();
		instance.statementGetPlayerIDFromName.clearParameters();

		if (!set.next())
			return -5;

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
