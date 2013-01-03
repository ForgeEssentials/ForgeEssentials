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
import java.util.TreeSet;

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
	private final PreparedStatement statementPutZone; // $ ZoneName
	
	// players
	private final PreparedStatement statementGetPlayerIDFromName; // playerName >> playerID
	private final PreparedStatement statementGetPlayerNameFromID; // playerID >> playerName
	private final PreparedStatement statementPutPlayer; // $ ZoneName
	
	// groups
	private final PreparedStatement statementGetGroupIDFromName; // groupName >> groupID
	private final PreparedStatement statementGetGroupNameFromID; // groupID >> groupName
	private final PreparedStatement statementGetGroupFromName; // groupName >> Group
	private final PreparedStatement statementGetGroupFromID; // groupID >> Group
	private final PreparedStatement statementGetGroupsForPlayer; // PlayerID, ZoneID >> Groups
	private final PreparedStatement statementPutGroup; // $ name, prefix, suffix, parent, priority, zone
	private final PreparedStatement statementUpdateGroup; // $ name, prefix, suffix, parent, priority, zone
	
	// ladders
	private final PreparedStatement statementGetLadderIDFromName; // ladderName  >> ladderID
	private final PreparedStatement statementGetLadderNameFromID; // LadderID >> ladderName
	private final PreparedStatement statementGetLadderIDFromGroup; // groupID, zoneID  >> ladderID
	private final PreparedStatement statementGetLadderList; // LadderID, ZoneID >> groupName, rank
	private final PreparedStatement statementPutLadder; // $ ZoneName

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
			
			// statementGetGroupsForPlayer
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?")
					.append(" AND ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?")
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID);
			statementGetGroupsForPlayer = instance.db.prepareStatement(query.toString());
			
			// statementUpdateGroup
			query = new StringBuilder("UPDATE ").append(TABLE_GROUP)
					.append(" SET ")
					.append(COLUMN_GROUP_NAME).append("=").append("'?', ")
					.append(COLUMN_GROUP_PREFIX).append("=").append("'?', ")
					.append(COLUMN_GROUP_SUFFIX).append("=").append("'?', ")
					.append(COLUMN_GROUP_PARENT).append("=").append("?, ")
					.append(COLUMN_GROUP_PRIORITY).append("=").append("?, ")
					.append(COLUMN_GROUP_ZONE).append("=").append("?, ")
					.append(" WHERE ")
					.append(COLUMN_GROUP_NAME).append("=").append("'?'");
			statementUpdateGroup = db.prepareStatement(query.toString());

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
					.append(COLUMN_GROUP_NAME).append(COLUMN_GROUP_PREFIX).append(COLUMN_GROUP_SUFFIX)
					.append(COLUMN_GROUP_PARENT).append(COLUMN_GROUP_PRIORITY).append(COLUMN_GROUP_ZONE)
					.append(") ")
					.append(" VALUES ").append(" ('?', '?', ?, '?', '?', '?') ");
			statementPutGroup = db.prepareStatement(query.toString());
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
					.append(this.COLUMN_GROUP_ZONE).append(" INTEGER NOT NULL")
					.append(this.COLUMN_GROUP_PREFIX).append(" VARCHAR(20) DEFAULT \"\", ")
					.append(this.COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) DEFAULT \"\", ")
					.append("PRIMARY KEY (").append(COLUMN_GROUP_GROUPID).append(") ")
					.append(") ").toString();

			String ladderTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER).append("(")
					.append(this.COLUMN_LADDER_LADDERID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_LADDER_GROUPID).append(" INTEGER NOT NULL,")
					.append(this.COLUMN_LADDER_ZONEID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_LADDER_RANK).append(" SMALLINT NOT NULL, ")
					.append(") ").toString();

			String ladderNameTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER_NAME).append("(")
					.append(this.COLUMN_LADDER_NAME_LADDERID).append(" INTEGER AUTO_INCREMENT, ")
					.append(this.COLUMN_LADDER_NAME_NAME).append(" VARCHAR(40) NOT NULL UNIQUE")
					.append("PRIMARY KEY (").append(COLUMN_LADDER_NAME_LADDERID).append(") ")
					.append(")").toString();

			String playerTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
					.append(this.COLUMN_PLAYER_PLAYERID).append(" INTEGER AUTO_INCREMENT, ")
					.append(this.COLUMN_PLAYER_USERNAME).append(" VARCHAR(20) NOT NULL UNIQUE")
					.append("PRIMARY KEY (").append(COLUMN_PLAYER_PLAYERID).append(") ")
					.append(")").toString();

			String groupConnectorTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP_CONNECTOR).append("(")
					.append(this.COLUMN_GROUP_CONNECTOR_GROUPID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_GROUP_CONNECTOR_PLAYERID).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_GROUP_CONNECTOR_ZONEID).append(" INTEGER NOT NULL, ")
					.append(" )").toString();

			String permissionTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PERMISSION).append("(")
					.append(this.COLUMN_PERMISSIONS_TARGET).append(" INTEGER NOT NULL, ")
					.append(this.COLUMN_PERMISSIONS_ISGROUP).append(" TINYINT(1) NOT NULL, ")
					.append(this.COLUMN_PERMISSIONS_PERM).append(" TEXT NOT NULL, ")
					.append(this.COLUMN_PERMISSIONS_ALLOWED).append(" TINYINT(1) NOT NULL, ")
					.append(this.COLUMN_PERMISSIONS_ZONEID).append(" INTEGER NOT NULL, ")
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
	protected static ArrayList<Group> getGroupForPlayer(String username, String zone)
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
	protected static boolean createGroup(Group g)
	{
		try
		{
			//check if group exists?
			if (getGroupIDFromGroupName(g.name) >= -4)
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
			instance.statementPutGroup.execute();
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
	protected static boolean updateGroup(Group g)
	{
		try
		{
			//check if group exists?
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
			instance.statementUpdateGroup.execute();
			instance.statementUpdateGroup.clearParameters();
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	protected static boolean getPermission()
	{
		return false;
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
