package com.ForgeEssentials.permission;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.EnumDBType;
import com.ForgeEssentials.util.OutputHandler;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.google.common.base.Throwables;

public class SqlHelper
{
	private Connection				db;
	private boolean					generate						= false;
	private static SqlHelper		instance;
	private EnumDBType				dbType;

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
	private final PreparedStatement	statementRemovePlayerGroups;
	private final PreparedStatement	statementPutPlayerInGroup;
	private final PreparedStatement	statementRemovePlayerGroup;

	// groups
	private final PreparedStatement	statementGetGroupIDFromName;							// groupName >> groupID
	private final PreparedStatement	statementGetGroupNameFromID;							// groupID >> groupName
	private final PreparedStatement	statementGetGroupFromName;								// groupName >> Group
	private final PreparedStatement	statementGetGroupFromID;								// groupID >> Group
	private final PreparedStatement	statementGetGroupsForPlayer;							// PlayerID, ZoneID >> Groups
	private final PreparedStatement	statementGetGroupsInZone;								// ZoneID
	private final PreparedStatement	statementGetGroupIDsForEntryPlayer;					// ZoneID >> GroupIDs
	private final PreparedStatement	statementPutGroup;										// $ name, prefix, suffix, parent, priority, zone
	private final PreparedStatement	statementUpdateGroup;									// $ name, prefix, suffix, parent, priority, zone
	private final PreparedStatement	statementDeleteGroupInZone;

	// ladders
	private final PreparedStatement	statementGetLadderIDFromName;							// ladderName >> ladderID
	private final PreparedStatement	statementGetLadderNameFromID;							// LadderID >> ladderName
	private final PreparedStatement	statementGetLadderIDFromGroup;							// groupID, zoneID >> ladderID
	private final PreparedStatement	statementGetLadderList;								// LadderID, ZoneID >> groupName, rank
	private final PreparedStatement	statementGetGroupsFromLadder;							// PlayerID, LadderID >> group
	private final PreparedStatement	statementGetGroupsFromLadderAndZone;					// PlayerID, LadderID, ZoneID >> group
	private final PreparedStatement	statementGetGroupsFromZone;							// PlayerID, ZoneID >> group
	private final PreparedStatement	statementGetGroupsFromPlayer;							// PlayerID >> group
	private final PreparedStatement	statementPutLadderName;								// $ LadderName
	private final PreparedStatement	statementPutLadder;									// $ groupid, zoneID, rank, ladderID

	// permissions
	private final PreparedStatement	statementGetPermission;								// target, isgroup, perm, zone >> allowed
	private final PreparedStatement statementGetAllPermissions;
	private final PreparedStatement	statementGetAll;										// target, isgroup, zone >> allowed
	private final PreparedStatement	statementGetPermissionForward;							// target, isgroup, perm, zone >> allowed
	private final PreparedStatement	statementPutPermission;								// $ , allowed, target, isgroup, perm, zone
	private final PreparedStatement	statementUpdatePermission;								// $ allowed, target, isgroup, perm, zone
	private final PreparedStatement	statementDeletePermission;

	// dump statements... replace ALL ids with names...
	private final PreparedStatement	statementDumpGroups;
	private final PreparedStatement	statementDumpPlayers;
	private final PreparedStatement	statementDumpGroupPermissions;
	private final PreparedStatement	statementDumpPlayerPermissions;
	private final PreparedStatement	statementDumpGroupConnector;
	private final PreparedStatement	statementDumpLadders;

	// zone deletion statements
	private final PreparedStatement	statementDelPermFromZone;
	private final PreparedStatement	statementDelLadderFromZone;
	private final PreparedStatement	statementDelGroupFromZone;
	private final PreparedStatement	statementDelGroupConnectorsFromZone;

	public SqlHelper(ConfigPermissions config)
	{
		instance = this;
		connect(config.connector);

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

			// statementGetGroupsFromLadder
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_LADDER)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID)
					.append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?")
					.append(" AND ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=").append("?");
			statementGetGroupsFromLadder = instance.db.prepareStatement(query.toString());

			// statementGetGroupsFromLadderAndZone
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_LADDER)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID)
					.append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?")
					.append(" AND ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_LADDERID).append("=").append("?")
					.append(" AND ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementGetGroupsFromLadderAndZone = instance.db.prepareStatement(query.toString());

			// statementGetGroupsFromZone
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID)
					.append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?")
					.append(" AND ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementGetGroupsFromZone = instance.db.prepareStatement(query.toString());

			// statementGetGroupsFromZone
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID)
					.append(" WHERE ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?");
			statementGetGroupsFromPlayer = instance.db.prepareStatement(query.toString());

			// statementGetGroupFromName
			query = new StringBuilder("SELECT ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_SUFFIX).append(", ")
					.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID)
					.append(" WHERE ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append("=").append("?");
			statementGetGroupFromName = instance.db.prepareStatement(query.toString());

			// statementGetGroupFromID
			query = new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ").append(TABLE_GROUP).append(".")
					.append(COLUMN_GROUP_PRIORITY).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP)
					.append(".").append(COLUMN_GROUP_SUFFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(" FROM ").append(TABLE_GROUP).append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".")
					.append(COLUMN_ZONE_ZONEID).append(" WHERE ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID).append("=").append("?");
			statementGetGroupFromID = instance.db.prepareStatement(query.toString());

			// statementGetGroupsForPlayer
			query = new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ").append(TABLE_GROUP).append(".")
					.append(COLUMN_GROUP_PRIORITY).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP)
					.append(".").append(COLUMN_GROUP_SUFFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(" FROM ")
					.append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
					.append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID).append(" WHERE ")
					.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?").append(" AND ")
					.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementGetGroupsForPlayer = instance.db.prepareStatement(query.toString());

			// statementGetGroupsInZone
			query = new StringBuilder("SELECT * FROM ").append(TABLE_GROUP).append(" WHERE ").append(COLUMN_GROUP_ZONE).append("=").append("?");
			statementGetGroupsInZone = instance.db.prepareStatement(query.toString());

			// statementGetGroupIDsForEntryPlayer
			query = new StringBuilder("SELECT ").append(COLUMN_GROUP_CONNECTOR_GROUPID)
					.append(" FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" WHERE ").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append(0)
					.append(" AND ").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementGetGroupIDsForEntryPlayer = instance.db.prepareStatement(query.toString());

			// statementUpdateGroup
			query = new StringBuilder("UPDATE ").append(TABLE_GROUP).append(" SET ").append(COLUMN_GROUP_NAME).append("=").append("?, ")
					.append(COLUMN_GROUP_PREFIX).append("=").append("?, ").append(COLUMN_GROUP_SUFFIX).append("=").append("?, ").append(COLUMN_GROUP_PARENT)
					.append("=").append("?, ").append(COLUMN_GROUP_PRIORITY).append("=").append("?, ").append(COLUMN_GROUP_ZONE).append("=").append("?")
					.append(" WHERE ").append(COLUMN_GROUP_NAME).append("=").append("?")
					.append(" AND ").append(COLUMN_GROUP_ZONE).append("=").append("?");
			statementUpdateGroup = db.prepareStatement(query.toString());

			// statementGetPermission
			query = new StringBuilder("SELECT ").append(COLUMN_PERMISSION_ALLOWED).append(" FROM ").append(TABLE_PERMISSION).append(" WHERE ")
					.append(COLUMN_PERMISSION_TARGET).append("=").append("?").append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_PERM).append("=").append("?").append(" AND ").append(COLUMN_PERMISSION_ZONEID).append("=")
					.append("?");
			statementGetPermission = db.prepareStatement(query.toString());

			// statementGetAll
			query = new StringBuilder("SELECT ").append(COLUMN_PERMISSION_ALLOWED)
					.append(" FROM ").append(TABLE_PERMISSION)
					.append(" WHERE ").append(COLUMN_PERMISSION_TARGET).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_PERM).append("=").append("'" + Permission.ALL + "'")
					.append(" AND ").append(COLUMN_PERMISSION_ZONEID).append("=").append("?");
			statementGetAll = db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ").append(COLUMN_PERMISSION_ALLOWED).append(" FROM ").append(TABLE_PERMISSION).append(" WHERE ")
					.append(COLUMN_PERMISSION_TARGET).append("=").append("?").append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_PERM).append(" LIKE ").append("?").append(" AND ").append(COLUMN_PERMISSION_ZONEID)
					.append("=").append("?");
			statementGetPermissionForward = db.prepareStatement(query.toString());

			// statementUpdatePermission
			query = new StringBuilder("UPDATE ").append(TABLE_PERMISSION).append(" SET ").append(COLUMN_PERMISSION_ALLOWED).append("=").append("?")
					.append(" WHERE ").append(COLUMN_PERMISSION_TARGET).append("=").append("?").append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=")
					.append("?").append(" AND ").append(COLUMN_PERMISSION_PERM).append("=").append("?").append(" AND ").append(COLUMN_PERMISSION_ZONEID)
					.append("=").append("?");
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
					.append(" WHERE ").append(COLUMN_LADDER_NAME_NAME).append("=").append("?");
			statementGetLadderIDFromName = db.prepareStatement(query.toString());

			// statementGetLadderFromGroup
			query = new StringBuilder("SELECT ").append(COLUMN_LADDER_LADDERID)
					.append(" FROM ").append(TABLE_LADDER)
					.append(" WHERE ").append(COLUMN_LADDER_GROUPID).append("=").append("?")
					.append(" AND ").append(COLUMN_LADDER_ZONEID).append("=").append("?");
			statementGetLadderIDFromGroup = db.prepareStatement(query.toString());

			// statementGetZoneFromID
			query = new StringBuilder("SELECT ").append(COLUMN_ZONE_NAME)
					.append(" FROM ").append(TABLE_ZONE)
					.append(" WHERE ").append(COLUMN_ZONE_ZONEID).append("=").append("?");
			statementGetZoneNameFromID = db.prepareStatement(query.toString());

			// statementGetZoneFromName
			query = new StringBuilder("SELECT ").append(COLUMN_ZONE_ZONEID)
					.append(" FROM ").append(TABLE_ZONE)
					.append(" WHERE ").append(COLUMN_ZONE_NAME).append("=").append("?");
			statementGetZoneIDFromName = db.prepareStatement(query.toString());

			// statementGetGroupFromID
			query = new StringBuilder("SELECT ").append(COLUMN_GROUP_NAME)
					.append(" FROM ").append(TABLE_GROUP)
					.append(" WHERE ").append(COLUMN_GROUP_GROUPID).append("=").append("?");
			statementGetGroupNameFromID = db.prepareStatement(query.toString());

			// statementGetGroupFromName
			query = new StringBuilder("SELECT *") // .append(COLUMN_GROUP_GROUPID)
			.append(" FROM ").append(TABLE_GROUP)
					.append(" WHERE ").append(COLUMN_GROUP_NAME).append("=").append("?");
			statementGetGroupIDFromName = db.prepareStatement(query.toString());

			// statementGetPlayerFromID
			query = new StringBuilder("SELECT ").append(COLUMN_PLAYER_USERNAME)
					.append(" FROM ").append(TABLE_PLAYER)
					.append(" WHERE ").append(COLUMN_PLAYER_PLAYERID).append("=").append("?");
			statementGetPlayerNameFromID = db.prepareStatement(query.toString());

			// statementGetPlayerFromName
			query = new StringBuilder("SELECT ").append(COLUMN_PLAYER_PLAYERID)
					.append(" FROM ").append(TABLE_PLAYER)
					.append(" WHERE ").append(COLUMN_PLAYER_USERNAME).append("=").append("?");
			statementGetPlayerIDFromName = db.prepareStatement(query.toString());
			
			// statementGetAllPermissions
			query = new StringBuilder("SELECT * FROM ").append(TABLE_PERMISSION)
					.append(" WHERE ").append(COLUMN_PERMISSION_TARGET).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_ZONEID).append("=").append("?")
					.append(" AND ").append(COLUMN_PERMISSION_ISGROUP).append("=").append("?");
			statementGetAllPermissions = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Helper Put Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementPutZone
			query = new StringBuilder("INSERT INTO ").append(TABLE_ZONE).append(" (")
					.append(COLUMN_ZONE_NAME).append(") ")
					.append(" VALUES ").append(" (?) ");
			statementPutZone = db.prepareStatement(query.toString());

			// statementPutPlayer
			query = new StringBuilder("INSERT INTO ").append(TABLE_PLAYER).append(" (")
					.append(COLUMN_PLAYER_USERNAME).append(") ")
					.append(" VALUES ").append(" (?) ");
			statementPutPlayer = db.prepareStatement(query.toString());

			// statementPutLadderName
			query = new StringBuilder("INSERT INTO ").append(TABLE_LADDER_NAME).append(" (")
					.append(COLUMN_LADDER_NAME_NAME).append(") ")
					.append(" VALUES ").append(" (?) ");
			statementPutLadderName = db.prepareStatement(query.toString());

			// statementPutLadder
			query = new StringBuilder("INSERT INTO ").append(TABLE_LADDER).append(" (")
					.append(COLUMN_LADDER_GROUPID).append(", ")
					.append(COLUMN_LADDER_ZONEID).append(", ")
					.append(COLUMN_LADDER_RANK).append(", ")
					.append(COLUMN_LADDER_LADDERID).append(") ")
					.append(" VALUES ").append(" (?, ?, ?, ?) ");
			statementPutLadder = db.prepareStatement(query.toString());

			// statementPutGroup
			query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (").append(COLUMN_GROUP_NAME).append(", ").append(COLUMN_GROUP_PREFIX)
					.append(", ").append(COLUMN_GROUP_SUFFIX).append(", ").append(COLUMN_GROUP_PARENT).append(", ").append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(COLUMN_GROUP_ZONE).append(") ").append(" VALUES ").append(" (?, ?, ?, ?, ?, ?) ");
			statementPutGroup = db.prepareStatement(query.toString());

			// statementPutPermission
			query = new StringBuilder("INSERT INTO ").append(TABLE_PERMISSION).append(" (").append(COLUMN_PERMISSION_ALLOWED).append(", ")
					.append(COLUMN_PERMISSION_TARGET).append(", ").append(COLUMN_PERMISSION_ISGROUP).append(", ").append(COLUMN_PERMISSION_PERM).append(", ")
					.append(COLUMN_PERMISSION_ZONEID).append(") ").append(" VALUES ").append(" (?, ?, ?, ?, ?) ");
			statementPutPermission = db.prepareStatement(query.toString());

			// statementPutPlayerInGroup
			query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP_CONNECTOR).append(" (")
					.append(COLUMN_GROUP_CONNECTOR_GROUPID).append(", ")
					.append(COLUMN_GROUP_CONNECTOR_PLAYERID).append(", ")
					.append(COLUMN_GROUP_CONNECTOR_ZONEID).append(") ")
					.append(" VALUES ").append(" (?, ?, ?)");
			statementPutPlayerInGroup = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Helper Delete Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementDeletePermission
			query = new StringBuilder("DELETE FROM ").append(TABLE_PERMISSION).append(" WHERE ")
					.append(COLUMN_PERMISSION_TARGET).append("=").append("?").append(" AND ")
					.append(COLUMN_PERMISSION_ISGROUP).append("=").append("?").append(" AND ")
					.append(COLUMN_PERMISSION_PERM).append("=").append("?").append(" AND ")
					.append(COLUMN_PERMISSION_ZONEID).append("=").append("?");
			statementDeletePermission = db.prepareStatement(query.toString());

			// statementDeleteGroupInZone
			query = new StringBuilder("DELETE FROM ").append(TABLE_GROUP)
					.append(" WHERE ").append(COLUMN_GROUP_NAME).append("=").append("?").append(" AND ")
					.append(COLUMN_GROUP_ZONE).append("=").append("?");
			statementDeleteGroupInZone = db.prepareStatement(query.toString());

			// statementDelZone
			query = new StringBuilder("DELETE FROM ").append(TABLE_ZONE).append(" WHERE ")
					.append(COLUMN_ZONE_NAME).append("=").append("?");
			statementDelZone = db.prepareStatement(query.toString());

			// remove player from all groups in specified zone. used in /p user <player> group set
			query = new StringBuilder("DELETE FROM ").append(TABLE_GROUP_CONNECTOR).append(" WHERE ")
					.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID)
					.append("=").append("?").append(" AND ").append(TABLE_GROUP_CONNECTOR).append(".")
					.append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementRemovePlayerGroups = instance.db.prepareStatement(query.toString());

			// remove player from specified group in specified zone. used in /p user <player> group add
			query = new StringBuilder("DELETE FROM ").append(TABLE_GROUP_CONNECTOR)
					.append(" WHERE ").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=").append("?")
					.append(" AND ").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?")
					.append(" AND ").append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append("?");
			statementRemovePlayerGroup = instance.db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Helper ZONE Delete Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// delete groups from zone
			query = new StringBuilder("DELETE FROM ").append(TABLE_GROUP)
					.append(" WHERE ").append(COLUMN_GROUP_ZONE).append("=").append("?");
			statementDelGroupFromZone = db.prepareStatement(query.toString());

			// delete ladder from zone
			query = new StringBuilder("DELETE FROM ").append(TABLE_LADDER)
					.append(" WHERE ").append(COLUMN_LADDER_ZONEID).append("=").append("?");
			statementDelLadderFromZone = db.prepareStatement(query.toString());

			// delete group connectorsw from zone
			query = new StringBuilder("DELETE FROM ").append(TABLE_LADDER)
					.append(" WHERE ").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append("?");
			statementDelGroupConnectorsFromZone = db.prepareStatement(query.toString());

			// statementDeleteGroupInZone
			query = new StringBuilder("DELETE FROM ").append(TABLE_PERMISSION)
					.append(" WHERE ").append(COLUMN_PERMISSION_ZONEID).append("=").append("?");
			statementDelPermFromZone = db.prepareStatement(query.toString());

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>
			// Dump Statements
			// <<<<<<<<<<<<<<<<<<<<<<<<<<

			// statementGetGroupFromID
			query = new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ").append(TABLE_GROUP).append(".")
					.append(COLUMN_GROUP_PRIORITY).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PREFIX).append(", ").append(TABLE_GROUP)
					.append(".").append(COLUMN_GROUP_SUFFIX).append(", ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_PARENT).append(", ")
					.append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(" FROM ").append(TABLE_GROUP).append(" INNER JOIN ").append(TABLE_ZONE)
					.append(" ON ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_ZONE).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpGroups = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ").append(TABLE_PERMISSION).append(".")
					.append(COLUMN_PERMISSION_PERM).append(", ").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ").append(TABLE_PERMISSION)
					.append(".").append(COLUMN_PERMISSION_ALLOWED).append(" FROM ").append(TABLE_PERMISSION).append(" INNER JOIN ").append(TABLE_GROUP)
					.append(" ON ").append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_TARGET).append("=").append(TABLE_GROUP).append(".")
					.append(COLUMN_GROUP_GROUPID).append(" INNER JOIN ").append(TABLE_ZONE).append(" ON ").append(TABLE_PERMISSION).append(".")
					.append(COLUMN_PERMISSION_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpGroupPermissions = instance.db.prepareStatement(query.toString());

			query = (new StringBuilder("SELECT ")).append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_USERNAME).append(", ").append(TABLE_PERMISSION)
					.append(".").append(COLUMN_PERMISSION_PERM).append(", ").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(", ")
					.append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_ALLOWED).append(" FROM ").append(TABLE_PERMISSION).append(" INNER JOIN ")
					.append(TABLE_PLAYER).append(" ON ").append(TABLE_PERMISSION).append(".").append(COLUMN_PERMISSION_TARGET).append("=").append(TABLE_PLAYER)
					.append(".").append(COLUMN_PLAYER_PLAYERID).append(" INNER JOIN ").append(TABLE_ZONE).append(" ON ").append(TABLE_PERMISSION).append(".")
					.append(COLUMN_PERMISSION_ZONEID).append("=").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_ZONEID);
			statementDumpPlayerPermissions = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT ").append(COLUMN_PLAYER_USERNAME).append(" FROM ").append(TABLE_PLAYER);
			statementDumpPlayers = instance.db.prepareStatement(query.toString());

			query = new StringBuilder("SELECT DISTINCT ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ").append(TABLE_PLAYER)
					.append(".").append(COLUMN_PLAYER_USERNAME).append(", ").append(TABLE_ZONE).append(".").append(COLUMN_ZONE_NAME).append(" FROM ")
					.append(TABLE_GROUP_CONNECTOR).append(" INNER JOIN ").append(TABLE_GROUP).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".")
					.append(COLUMN_GROUP_CONNECTOR_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID).append(" INNER JOIN ")
					.append(TABLE_PLAYER).append(" ON ").append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_PLAYERID).append("=")
					.append(TABLE_PLAYER).append(".").append(COLUMN_PLAYER_PLAYERID).append(" INNER JOIN ").append(TABLE_ZONE).append(" ON ")
					.append(TABLE_GROUP_CONNECTOR).append(".").append(COLUMN_GROUP_CONNECTOR_ZONEID).append("=").append(TABLE_ZONE).append(".")
					.append(COLUMN_ZONE_ZONEID);
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

			// remove specified group
			query = new StringBuilder("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Throwables.propagate(e);
			// it may not get to this.. hopefully...
			throw new RuntimeException(e.getMessage());
		}

		OutputHandler.SOP("Statement preparation successful");
	}

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------INIT ---- METHODS ------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	private void connect(DBConnector connector)
	{
		try
		{
			dbType = connector.getChosenType();
			db = connector.getChosenConnection();

			DatabaseMetaData meta = db.getMetaData();
			ResultSet set = meta.getTables(null, null, null, new String[] { "TABLE" });
			generate = true;
			while (set.next())
			{
				if (set.getString("TABLE_NAME").equalsIgnoreCase(TABLE_PERMISSION))
				{
					generate = false;
					continue;
				}
			}
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			Throwables.propagate(e);
		}
	}

	// create tables.
	private void generate()
	{
		try
		{
			String zoneTable, groupTable, ladderTable, ladderNameTable, playerTable, groupConnectorTable, permissionTable;

			// ------------------
			// H2 & MYSQL
			// ------------------

			zoneTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_ZONE).append("(")
					.append(COLUMN_ZONE_ZONEID).append(" INTEGER AUTO_INCREMENT, ")
					.append(COLUMN_ZONE_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_ZONE_ZONEID).append(") ")
					.append(")").toString();

			groupTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
					.append(COLUMN_GROUP_GROUPID).append(" INTEGER AUTO_INCREMENT, ")
					.append(COLUMN_GROUP_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append(COLUMN_GROUP_PARENT).append(" INTEGER, ")
					.append(COLUMN_GROUP_PRIORITY).append(" SMALLINT NOT NULL, ")
					.append(COLUMN_GROUP_ZONE).append(" INTEGER NOT NULL, ")
					.append(COLUMN_GROUP_PREFIX).append(" VARCHAR(20) DEFAULT '', ")
					.append(COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) DEFAULT '', ")
					.append("PRIMARY KEY (").append(COLUMN_GROUP_GROUPID).append(") ")
					.append(") ").toString();

			ladderTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER).append("(")
					.append(COLUMN_LADDER_LADDERID).append(" INTEGER NOT NULL, ")
					.append(COLUMN_LADDER_GROUPID).append(" INTEGER NOT NULL, ")
					.append(COLUMN_LADDER_ZONEID).append(" INTEGER NOT NULL, ")
					.append(COLUMN_LADDER_RANK).append(" SMALLINT NOT NULL")
					.append(") ").toString();

			ladderNameTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER_NAME).append("(")
					.append(COLUMN_LADDER_NAME_LADDERID).append(" INTEGER AUTO_INCREMENT, ")
					.append(COLUMN_LADDER_NAME_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_LADDER_NAME_LADDERID).append(") ")
					.append(")").toString();

			playerTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PLAYER).append("(")
					.append(COLUMN_PLAYER_PLAYERID).append(" INTEGER AUTO_INCREMENT, ")
					.append(COLUMN_PLAYER_USERNAME).append(" VARCHAR(20) NOT NULL UNIQUE, ")
					.append("PRIMARY KEY (").append(COLUMN_PLAYER_PLAYERID).append(") ")
					.append(")").toString();

			groupConnectorTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP_CONNECTOR).append("(")
					.append(COLUMN_GROUP_CONNECTOR_GROUPID).append(" INTEGER NOT NULL, ")
					.append(COLUMN_GROUP_CONNECTOR_PLAYERID).append(" INTEGER NOT NULL, ")
					.append(COLUMN_GROUP_CONNECTOR_ZONEID).append(" INTEGER NOT NULL")
					.append(")").toString();

			permissionTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PERMISSION).append("(")
					.append(COLUMN_PERMISSION_TARGET).append(" INTEGER NOT NULL, ")
					.append(COLUMN_PERMISSION_ISGROUP).append(" TINYINT(1) NOT NULL, ")
					.append(COLUMN_PERMISSION_PERM).append(" TEXT NOT NULL, ")
					.append(COLUMN_PERMISSION_ALLOWED).append(" TINYINT(1) NOT NULL, ")
					.append(COLUMN_PERMISSION_ZONEID).append(" INTEGER NOT NULL")
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
			StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (")
					.append(COLUMN_GROUP_GROUPID).append(", ")
					.append(COLUMN_GROUP_NAME).append(", ")
					.append(COLUMN_GROUP_PRIORITY).append(", ")
					.append(COLUMN_GROUP_ZONE).append(") ")
					.append(" VALUES ").append(" (")
					.append("-1, ") // groupID
					.append("'").append(PermissionsAPI.getDEFAULT().name).append("', ")
					.append("0, 0)"); // priority, zone
			db.createStatement().executeUpdate(query.toString());

			// zones arent touched when importing...
			// GLOBAL zone
			query = new StringBuilder("INSERT INTO ").append(TABLE_ZONE).append(" (")
					.append(COLUMN_ZONE_NAME).append(", ")
					.append(COLUMN_ZONE_ZONEID).append(") ")
					.append(" VALUES ").append(" ('")
					.append(ZoneManager.getGLOBAL().getZoneName()).append("', 0) ");
			db.createStatement().executeUpdate(query.toString());

			// SUPER zone
			query = new StringBuilder("INSERT INTO ").append(TABLE_ZONE).append(" (")
					.append(COLUMN_ZONE_NAME).append(", ")
					.append(COLUMN_ZONE_ZONEID).append(") ")
					.append(" VALUES ").append(" ('")
					.append(ZoneManager.getSUPER().getZoneName()).append("', -1) ");
			db.createStatement().executeUpdate(query.toString());

			// Entry player...
			query = new StringBuilder("INSERT INTO ").append(TABLE_PLAYER).append(" (")
					.append(COLUMN_PLAYER_USERNAME).append(", ")
					.append(COLUMN_PLAYER_PLAYERID).append(") ")
					.append(" VALUES ").append(" ('")
					.append(PermissionsAPI.getEntryPlayer()).append("', 0) ");
			db.createStatement().executeUpdate(query.toString());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * If generation is enabled, puts the provided Registered permissions into the DB.
	 * 
	 * @param map
	 */
	protected synchronized void putRegistrationperms(HashMap<RegGroup, HashSet<Permission>> map)
	{
		if (!generate)
		{
			return;
		}
		try
		{
			OutputHandler.SOP(" Inserting registration permissions into Permissions DB");

			// make a statement to be used later.. just easier...
			PreparedStatement s;

			// create default groups...
			StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_PERMISSION).append(" (").append(COLUMN_PERMISSION_TARGET).append(", ")
					.append(COLUMN_PERMISSION_ALLOWED).append(", ").append(COLUMN_PERMISSION_PERM).append(", ").append(COLUMN_PERMISSION_ISGROUP).append(", ")
					.append(COLUMN_PERMISSION_ZONEID).append(") ").append(" VALUES ").append(" (?, ?, ?, 1, 0) ");
			PreparedStatement statement = db.prepareStatement(query.toString());

			// create groups...
			HashMap<RegGroup, Integer> groups = new HashMap<RegGroup, Integer>();
			int NUM;
			for (RegGroup group : RegGroup.values())
			{
				if (group.equals(RegGroup.ZONE))
				{
					groups.put(group, -1);
					continue;
				}

				createGroup(group.getGroup());
				NUM = getGroupIDFromGroupName(group.toString());
				groups.put(group, NUM);
			}

			// register permissions
			for (Entry<RegGroup, HashSet<Permission>> entry : map.entrySet())
			{
				statement.setInt(1, groups.get(entry.getKey()));
				for (Permission perm : entry.getValue())
				{
					statement.setInt(2, perm.allowed ? 1 : 0);
					statement.setString(3, perm.name);
					statement.executeUpdate();
				}
			}

			// put the EntryPlayer to GUESTS for the GLOBAL zone
			s = this.statementPutPlayerInGroup;
			s.setInt(1, groups.get(RegGroup.GUESTS));
			s.setInt(2, 0);
			s.setInt(3, 0);
			s.executeUpdate();
			s.clearParameters();

			// make default ladder
			s = this.statementPutLadderName;
			s.setString(1, RegGroup.LADDER);
			s.executeUpdate();
			s.clearParameters();

			// add groups to ladder
			s = this.statementPutLadder;
			s.setInt(2, 0); // zone
			s.setInt(4, 1); // the ladderID
			{
				// Owner
				s.setInt(1, groups.get(RegGroup.OWNERS));
				s.setInt(3, 1);
				s.executeUpdate();

				// ZoneAdmin
				s.setInt(1, groups.get(RegGroup.ZONE_ADMINS));
				s.setInt(3, 2);
				s.executeUpdate();

				// Member
				s.setInt(1, groups.get(RegGroup.MEMBERS));
				s.setInt(3, 3);
				s.executeUpdate();

				// Guest
				s.setInt(1, groups.get(RegGroup.GUESTS));
				s.setInt(3, 4);
				s.executeUpdate();
			}
			s.clearParameters();

			OutputHandler.SOP(" Registration permissions successfully inserted");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * "players" >> arraylist<String> DONE
	 * "groups" >> arrayList<Group> DONE
	 * "playerPerms" >> arrayList<permHolder> DONE
	 * "groupPerms" >> arrayList<permHolder> DONE
	 * "groupConnectors" >> HashMap<String, HashMap<String, String[]>> DONE
	 * "ladders" >> arraylist<PromotionLadder> DONE
	 */
	protected void importPerms(String importDir)
	{
		try
		{
			File file = new File(ModulePermissions.permsFolder, importDir);
			OutputHandler.SOP("[PermSQL] Importing permissions from " + importDir);

			FlatFileGroups g = new FlatFileGroups(file);
			HashMap<String, Object> map = g.load();

			FlatFilePlayers p = new FlatFilePlayers(file);
			map.put("players", p.load());

			FlatFilePermissions pm = new FlatFilePermissions(file);
			map.putAll(pm.load());

			OutputHandler.SOP("[PermSQL] Loaded Configs into ram");

			// KILL ALL DE DATA!!!!
			db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_PERMISSION);
			db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_GROUP);
			db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_PLAYER);
			db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_LADDER);
			db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_LADDER_NAME);
			db.createStatement().executeUpdate("TRUNCATE TABLE " + TABLE_GROUP_CONNECTOR);
			OutputHandler.SOP("[PermSQL] Cleaned tables of existing data");

			// call generate to remake the stuff that should be there
			{
				// recreate EntryPlayer player
				this.statementPutPlayer.setString(1, PermissionsAPI.getEntryPlayer());
				this.statementPutPlayer.executeUpdate();
				this.statementPutPlayer.clearParameters();
				
				// recreate DEFAULT group
				StringBuilder query = new StringBuilder("INSERT INTO ").append(TABLE_GROUP).append(" (")
						.append(COLUMN_GROUP_GROUPID).append(", ")
						.append(COLUMN_GROUP_NAME).append(", ")
						.append(COLUMN_GROUP_PRIORITY).append(", ")
						.append(COLUMN_GROUP_ZONE).append(") ")
						.append(" VALUES ").append(" (")
						.append("-1, ") // groupID
						.append("'").append(PermissionsAPI.getDEFAULT().name).append("', ")
						.append("0, 0)"); // priority, zone
				db.createStatement().executeUpdate(query.toString());
			}

			// create players...
			PreparedStatement s = this.statementPutPlayer;
			for (String player : (ArrayList<String>) map.get("players"))
			{
				s.setString(1, player);
				s.executeUpdate();
			}
			s.clearParameters();
			OutputHandler.SOP("[PermSQL] Imported players");

			// create groups
			s = instance.statementPutPlayerInGroup;
			HashMap<String, HashMap<String, String[]>> playerMap = (HashMap<String, HashMap<String, String[]>>) map.get("connector");
			HashMap<String, String[]> temp;
			String[] list;
			for (Group group : (ArrayList<Group>) map.get("groups"))
			{
				if (group.name.equals(PermissionsAPI.getDEFAULT().name))
					continue;
				
				createGroup(group);
				s.setInt(1, getGroupIDFromGroupName(group.name));
				s.setInt(3, getZoneIDFromZoneName(group.zoneName));

				temp = playerMap.get(group.zoneName);
				if (temp == null)
					continue;

				list = temp.get(group.name);

				if (list == null)
					continue;

				// add the players to the groups as well.
				for (String player : list)
				{
					s.setInt(2, getPlayerIDFromPlayerName(player));
					s.executeUpdate();
				}

			}
			OutputHandler.SOP("[PermSQL] Imported groups");

			// add groups to ladders and stuff
			s = this.statementPutLadderName;
			PreparedStatement s2 = this.statementPutLadder; // groupID, zoneID, rank, ladderID
			for (PromotionLadder ladder : (ArrayList<PromotionLadder>) map.get("ladders"))
			{
				s.setString(1, ladder.name);
				s.executeUpdate();
				s2.setInt(4, getLadderIDFromLadderName(ladder.name));
				s2.setInt(2, getZoneIDFromZoneName(ladder.zoneID));

				list = ladder.getListGroup();
				for (int i = 0; i < list.length; i++)
				{
					s2.setInt(3, i);
					s2.setInt(1, getGroupIDFromGroupName(list[i]));
					s2.executeUpdate();
				}
			}
			OutputHandler.SOP("[PermSQL] Imported ladders");

			// now the permissions
			ArrayList<PermissionHolder> perms = (ArrayList<PermissionHolder>) map.get("playerPerms");
			for (PermissionHolder perm : perms)
				setPermission(perm.target, false, perm, perm.zone);

			perms = (ArrayList<PermissionHolder>) map.get("groupPerms");
			for (PermissionHolder perm : perms)
				setPermission(perm.target, true, perm, perm.zone);

			OutputHandler.SOP("[PermSQL] Imported permissions");

			OutputHandler.SOP("[PermSQL] Import successful!");
		}
		catch (SQLException e)
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
	 * @param ladderName may be null
	 * @param zoneName may be null
	 * @param username may NOT be null
	 * @return at worst an EmptySet
	 */
	public static TreeSet<Group> getGroupsForChat(String ladderName, String zoneName, String username)
	{
		TreeSet<Group> end = new TreeSet<Group>();
		try
		{
			int lID, zID, pID;
			pID = getPlayerIDFromPlayerName(username);

			// for variations..
			if (zoneName == null && ladderName == null)
			{
				lID = zID = -3;
			}
			else if (zoneName == null && ladderName != null)
			{
				lID = getLadderIDFromLadderName(ladderName);
				zID = -3;
			}
			else if (zoneName != null && ladderName == null)
			{
				lID = -3;
				zID = getZoneIDFromZoneName(zoneName);
			}
			else
			{
				lID = getLadderIDFromLadderName(ladderName);
				zID = getZoneIDFromZoneName(zoneName);
			}

			if (lID < -4 || zID < -4 || pID < -4)
			{
				OutputHandler.SOP("Ladder, Player, or Zone does not exist!");
				return end;
			}

			ResultSet set;
			PreparedStatement s;

			if (lID == zID && lID == -3)
				s = instance.statementGetGroupsFromPlayer;
			else if (lID == -3)
			{
				s = instance.statementGetGroupsFromZone;
				s.setInt(2, zID);
			}
			else if (zID == -3)
			{
				s = instance.statementGetGroupsFromLadder;
				s.setInt(2, pID);
			}
			else
			{
				s = instance.statementGetGroupsFromLadderAndZone;
				s.setInt(2, lID);
				s.setInt(3, zID);
			}
			s.setInt(1, pID);
			set = s.executeQuery();

			Group g;
			String prefix, suffix, name, zone;
			int priority;
			while (set.next())
			{
				name = set.getString(1);
				prefix = set.getString(2);
				suffix = set.getString(3);
				zone = set.getString(4);
				priority = set.getInt(5);
				g = new Group(name, prefix, suffix, null, zone, priority);
				end.add(g);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return end;
	}

	/**
	 * @param groupName
	 * @return NULL if no group in existence. or an SQL error happened.
	 */
	protected static synchronized Group getGroupForName(String group)
	{
		try
		{
			
			if (group == null)
				return null;
			
			// setup query for List
			instance.statementGetGroupFromName.setString(1, group);
			ResultSet set = instance.statementGetGroupFromName.executeQuery();
			instance.statementGetGroupFromName.clearParameters();

			if (!set.next())
			{
				return null;
			}

			int priority = set.getInt(COLUMN_GROUP_PRIORITY);
			String parent = getGroupNameFromGroupID(set.getInt(COLUMN_GROUP_PARENT));
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
	 * @return NULL if no group in existence, or an SQL erorr happenend. TDOD: remove?? its unused...
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
			{
				return null;
			}

			int priority = set.getInt(COLUMN_GROUP_PRIORITY);
			String name = set.getString(COLUMN_GROUP_NAME);
			String parent = getGroupNameFromGroupID(set.getInt(COLUMN_GROUP_PARENT));
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
	 * 
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
				parent = getGroupNameFromGroupID(result.getInt(COLUMN_GROUP_PARENT));
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
			{
				return false; // group exists
			}

			int parent = -5;
			int zone = getZoneIDFromZoneName(g.zoneName);

			if (g.parent != null)
			{
				parent = getGroupIDFromGroupName(g.parent);
				if (parent == -5)
				{
					return false;
				}
			}

			if (zone < -4)
			{
				return false;
			}

			// my query
			// $ name, prefix, suffix, parent, priority, zone
			instance.statementPutGroup.setString(1, g.name);
			instance.statementPutGroup.setString(2, g.prefix);
			instance.statementPutGroup.setString(3, g.suffix);
			if (parent == -5)
			{
				instance.statementPutGroup.setNull(4, java.sql.Types.INTEGER);
			}
			else
			{
				instance.statementPutGroup.setInt(4, parent);
			}

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
			{
				return false; // group doesn't exist
			}

			int parent = -5;
			int zone = getZoneIDFromZoneName(g.zoneName);

			if (g.parent != null)
			{
				parent = getGroupIDFromGroupName(g.parent);
				if (parent == -5)
				{
					return false;
				}
			}

			if (zone < -4)
			{
				return false;
			}

			// my query
			instance.statementUpdateGroup.setString(1, g.name);
			instance.statementUpdateGroup.setString(2, g.prefix);
			instance.statementUpdateGroup.setString(3, g.suffix);
			if (parent == -5)
			{
				instance.statementUpdateGroup.setNull(4, java.sql.Types.INTEGER);
			}
			else
			{
				instance.statementUpdateGroup.setInt(4, parent);
			}
			instance.statementUpdateGroup.setInt(5, g.priority);
			instance.statementUpdateGroup.setInt(6, zone);
			instance.statementUpdateGroup.setString(7, g.name);
			instance.statementUpdateGroup.setInt(8, zone);
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
	 * @return ALLOW/DENY if the permission is allowed/denied. UNKNOWN if its not found.
	 */
	protected static synchronized PermResult getAllResult(String target, boolean isGroup, String zone, boolean checkForward)
	{
		try
		{
			int tID;
			int zID = getZoneIDFromZoneName(zone);
			int isG = isGroup ? 1 : 0;
			int allowed = -1;
			PreparedStatement statement = instance.statementGetAll;
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
			statement.setInt(3, zID);
			set = statement.executeQuery();
			statement.clearParameters();

			if (set.next())
				return set.getBoolean(1) ? PermResult.ALLOW : PermResult.ALLOW;
			else
				return PermResult.UNKNOWN;

		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}
		return PermResult.UNKNOWN;
	}

	/**
	 * @param target
	 * (username or groupname)
	 * @param isGroup
	 * @param perm
	 * @return ALLOW/DENY if the permission or a parent is allowed/denied. UNKNOWN if nor it or any parents were not found. UNKNOWN also if the target or the
	 * zone do not exist.
	 */
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
			{
				tID = getGroupIDFromGroupName(target);
			}
			else
			{
				tID = getPlayerIDFromPlayerName(target);
			}

			if (zID < -4 || tID < -4)
			{
				return PermResult.UNKNOWN;
			}

			// initial check.
			statement.setInt(1, tID);
			statement.setInt(2, isG);
			statement.setString(3, perm.name);
			statement.setInt(4, zID);
			set = statement.executeQuery();
			statement.clearParameters();

			PermResult initial = PermResult.UNKNOWN;
			if (set.next())
			{
				return set.getInt(1) > 0 ? PermResult.ALLOW : PermResult.DENY;
			}

			// if the stuff is FORWARD!
			// TODO: fix.
			/*
			 * if (checkForward)
			 * {
			 * statement = instance.statementGetPermissionForward;
			 * // target, isgroup, perm, zone >> allowed
			 * statement.setInt(1, tID);
			 * statement.setInt(2, isG);
			 * statement.setString(3, perm.name + ".%");
			 * statement.setInt(4, zID);
			 * set = statement.executeQuery();
			 * statement.clearParameters();
			 * boolean allow = false;
			 * boolean deny = false;
			 * switch (initial)
			 * {
			 * case ALLOW:
			 * allow = true;
			 * break;
			 * case DENY:
			 * deny = true;
			 * break;
			 * }
			 * while (set.next())
			 * {
			 * allowed = set.getInt(1); // allowed.. only 1 column.
			 * if (allowed == 0)
			 * {
			 * deny = true;
			 * }
			 * else
			 * {
			 * allow = true;
			 * }
			 * if (allow && deny)
			 * {
			 * instance.statementGetPermission.clearParameters();
			 * return PermResult.PARTIAL;
			 * }
			 * }
			 * if (allowed > -1)
			 * {
			 * instance.statementGetPermission.clearParameters();
			 * if (allow && !deny)
			 * {
			 * return PermResult.DENY;
			 * }
			 * }
			 * statement = instance.statementGetPermission;
			 * }
			 */

			if (!initial.equals(PermResult.UNKNOWN))
			{
				instance.statementGetPermission.clearParameters();
				return initial;
			}

			// normal checking of the parents now
			while (perm != null)
			{
				// params still set from initial
				statement.setInt(1, tID);
				statement.setInt(2, isG);
				statement.setString(3, perm.name);
				statement.setInt(4, zID);
				set = statement.executeQuery();
				statement.clearParameters();

				if (set.next())
				{
					allowed = set.getInt(1); // allowed.. only 1 column.
					return allowed > 0 ? PermResult.ALLOW : PermResult.DENY;
				}

				if (!perm.hasParent())
				{
					perm = null;
				}
				else
				{
					perm = new PermissionChecker(perm.getAllParent());
				}
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
	 * 
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
			{
				tID = getGroupIDFromGroupName(target);
			}
			else
			{
				tID = getPlayerIDFromPlayerName(target);
			}

			if (zID < -4 || tID < -4)
			{
				return false;
			}

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
			{
				use = instance.statementUpdatePermission; // exists
			}
			else
			{
				use = instance.statementPutPermission; // does not exist.
			}

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
			int zid = getZoneIDFromZoneName(name);

			if (zid == -5)
				return false;

			instance.statementDelZone.setString(1, name);
			instance.statementDelZone.executeUpdate();
			instance.statementDelZone.clearParameters();

			instance.statementDelGroupFromZone.setInt(1, zid);
			instance.statementDelGroupFromZone.executeUpdate();
			instance.statementDelGroupFromZone.clearParameters();

			instance.statementDelGroupConnectorsFromZone.setInt(1, zid);
			instance.statementDelGroupConnectorsFromZone.executeUpdate();
			instance.statementDelGroupConnectorsFromZone.clearParameters();

			instance.statementDelLadderFromZone.setInt(1, zid);
			instance.statementDelLadderFromZone.executeUpdate();
			instance.statementDelLadderFromZone.clearParameters();

			instance.statementDelPermFromZone.setInt(1, zid);
			instance.statementDelPermFromZone.executeUpdate();
			instance.statementDelPermFromZone.clearParameters();

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
	 * 
	 * @return
	 * "players" >> arraylist<String> DONE
	 * "groups" >> arrayList<Group> DONE
	 * "playerPerms" >> arrayList<permHolder> DONE
	 * "groupPerms" >> arrayList<permHolder> DONE
	 * "groupConnectors" >> HashMap<String, HashMap<String, ArrayList<String>>> DONE
	 * "ladders" >> arraylist<PromotionLadder> DONE
	 */
	protected static synchronized HashMap<String, Object> dump()
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
			{
				list.add(set.getString(1));
			}

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
			int parentID;
			Group g;
			while (set.next())
			{
				priority = set.getInt(COLUMN_GROUP_PRIORITY);
				name = set.getString(COLUMN_GROUP_NAME);
				parentID = set.getInt(COLUMN_GROUP_PARENT);
				prefix = set.getString(COLUMN_GROUP_PREFIX);
				suffix = set.getString(COLUMN_GROUP_SUFFIX);
				zone = set.getString(COLUMN_ZONE_NAME);
				parent = getGroupNameFromGroupID(parentID);
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
			for (Entry<String, HashMap<String, ArrayList<String>>> entry1 : uberMap.entrySet())
			{
				for (Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet())
				{
					if (entry2.getValue().isEmpty())
					{
						continue;
					}

					lad = new PromotionLadder(entry2.getKey(), entry1.getKey(), entry2.getValue().toArray(holder));
					list.add(lad);
				}
			}

			map.put("ladders", list);
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("[PermSQL] Ladder dump for export failed!");
			e.printStackTrace();
			list = null;
		}

		return map;
	}

	/**
	 * @param username
	 * @return FALSE if player was not generated or SQL error.
	 */
	public static synchronized boolean generatePlayer(String username)
	{
		try
		{
			boolean generate = false;
			int pid = getPlayerIDFromPlayerName(username);

			if (pid == -5)
			{
				// generate players
				generate = true;
				instance.statementPutPlayer.setString(1, username);
				instance.statementPutPlayer.executeUpdate();
				instance.statementPutPlayer.clearParameters();

				pid = getPlayerIDFromPlayerName(username);
			}

			if (generate)
			{

				// get groups list...
				ArrayList<Integer> groups = new ArrayList<Integer>();
				ResultSet set;

				instance.statementGetGroupIDsForEntryPlayer.setInt(1, 0); // global
				set = instance.statementGetGroupIDsForEntryPlayer.executeQuery();
				instance.statementGetGroupIDsForEntryPlayer.clearParameters();

				while (set.next())
					groups.add(set.getInt(1));

				instance.statementPutPlayerInGroup.setInt(2, pid); // player
				instance.statementPutPlayerInGroup.setInt(3, 0); // zone
				for (int num : groups)
				{
					instance.statementPutPlayerInGroup.setInt(1, num);
					instance.statementPutPlayerInGroup.executeUpdate();
				}

				instance.statementPutPlayerInGroup.clearParameters();

				return true;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return false;
	}

	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// -------------------------------ID <<>> NAME METHODS -----------------------------------------------
	// ---------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------

	/**
	 * @param zone
	 * @return -5 if the Zone does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getZoneIDFromZoneName(String zone) throws SQLException
	{
		if (zone.equalsIgnoreCase(ZoneManager.getGLOBAL().getZoneName()))
			return 0;

		instance.statementGetZoneIDFromName.setString(1, zone);
		ResultSet set = instance.statementGetZoneIDFromName.executeQuery();
		instance.statementGetZoneIDFromName.clearParameters();

		if (!set.next())
		{
			return -5;
		}

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
		{
			return null;
		}

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
		{
			return -5;
		}

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
		{
			return null;
		}

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
		{
			return -5;
		}

		return set.getInt(1);
	}

	/**
	 * @param group
	 * @return -5 if the Group does not exist.
	 * @throws SQLException
	 */
	private static synchronized int getGroupIDFromGroupName(String group) throws SQLException
	{
		if (group.equals(PermissionsAPI.getDEFAULT().name))
			return -1;

		instance.statementGetGroupIDFromName.setString(1, group);
		ResultSet set = instance.statementGetGroupIDFromName.executeQuery();
		instance.statementGetGroupIDFromName.clearParameters();

		if (!set.next())
		{
			return -5;
		}

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
		{
			return null;
		}

		return set.getString(1);
	}

	/**
	 * @param player
	 * @return returns -5 if the player doesn't exist.
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
		{
			return null;
		}

		return set.getString(1);
	}

	private static synchronized void clearPlayerGroupsInZone(int playerID, int zoneID) throws SQLException
	{
		instance.statementRemovePlayerGroups.setInt(1, playerID);
		instance.statementRemovePlayerGroups.setInt(2, zoneID);
		instance.statementRemovePlayerGroups.executeUpdate();
		instance.statementRemovePlayerGroups.clearParameters();
	}

	public static synchronized String setPlayerGroup(String group, String player, String zone)
	{
		try
		{
			int playerID = instance.getPlayerIDFromPlayerName(player);
			int groupID = instance.getGroupIDFromGroupName(group);
			int zoneID = instance.getZoneIDFromZoneName(zone);

			clearPlayerGroupsInZone(playerID, zoneID);
			return addPlayerGroup(groupID, playerID, zoneID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "Player group not set.";
	}

	public static synchronized String addPlayerGroup(String group, String player, String zone)
	{
		try
		{
			int playerID = getPlayerIDFromPlayerName(player);
			int groupID = getGroupIDFromGroupName(group);
			int zoneID = getZoneIDFromZoneName(zone);
			return addPlayerGroup(groupID, playerID, zoneID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "Player not added to group.";
	}

	private static synchronized String addPlayerGroup(int groupID, int playerID, int zoneID) throws SQLException
	{
		instance.statementPutPlayerInGroup.setInt(1, groupID);
		instance.statementPutPlayerInGroup.setInt(2, playerID);
		instance.statementPutPlayerInGroup.setInt(3, zoneID);
		int result = instance.statementPutPlayerInGroup.executeUpdate();
		instance.statementPutPlayerInGroup.clearParameters();

		if (result == 0)
		{
			return "Row not inserted.";
		}

		return null;
	}

	public static synchronized String removePlayerGroup(String group, String player, String zone)
	{
		try
		{
			int playerID = getPlayerIDFromPlayerName(player);
			int groupID = getGroupIDFromGroupName(group);
			int zoneID = getZoneIDFromZoneName(zone);
			return removePlayerGroup(groupID, playerID, zoneID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "Player not added to group";
	}

	private static synchronized String removePlayerGroup(int groupID, int playerID, int zoneID) throws SQLException
	{
		instance.statementRemovePlayerGroup.setInt(1, playerID);
		instance.statementRemovePlayerGroup.setInt(2, zoneID);
		instance.statementRemovePlayerGroup.setInt(3, groupID);
		int result = instance.statementRemovePlayerGroup.executeUpdate();
		instance.statementRemovePlayerGroup.clearParameters();

		if (result == 0)
		{
			return "Player not removed from group.";
		}

		return null;
	}

	public static synchronized String removePermission(String target, boolean isGroup,
			String node, String zone)
	{
		try
		{
			int playerID = instance.getPlayerIDFromPlayerName(target);
			int zoneID = instance.getZoneIDFromZoneName(zone);

			return removePermission(playerID, isGroup, node, zoneID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "Player group not set.";
	}

	public static synchronized String removePermission(int playerID, boolean isGroup,
			String node, int zoneID) throws SQLException
	{
		instance.statementDeletePermission.setInt(1, playerID);
		instance.statementDeletePermission.setBoolean(2, isGroup);
		instance.statementDeletePermission.setString(3, node);
		instance.statementDeletePermission.setInt(4, zoneID);
		instance.statementDeletePermission.executeUpdate();
		instance.statementDeletePermission.clearParameters();
		return null;
	}

	public static synchronized void deleteGroupInZone(String group, String zone)
	{
		try
		{
			int zoneID = getZoneIDFromZoneName(zone);
			instance.statementDeleteGroupInZone.setString(1, group);
			instance.statementDeleteGroupInZone.setInt(2, zoneID);
			instance.statementDeleteGroupInZone.executeUpdate();
			instance.statementDeleteGroupInZone.clearParameters();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static synchronized ArrayList getGroupsInZone(String zoneName)
	{
		try
		{
			TreeSet<Group> set = new TreeSet<Group>();
			int zID = getZoneIDFromZoneName(zoneName);

			instance.statementGetGroupsInZone.setInt(1, zID);
			ResultSet result = instance.statementGetGroupsInZone.executeQuery();
			instance.statementGetGroupsInZone.clearParameters();

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
				g = new Group(name, prefix, suffix, parent, zoneName, priority);
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

	public static String getPermission(String target, boolean isGroup, String perm, String zone)
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
			{
				tID = getGroupIDFromGroupName(target);
			}
			else
			{
				tID = getPlayerIDFromPlayerName(target);
			}

			if (zID < -4 || tID < -4)
			{
				return "Zone or target invalid.";
			}

			// initial check.
			statement.setInt(1, tID);
			statement.setInt(2, isG);
			statement.setString(3, perm);
			statement.setInt(4, zID);
			set = statement.executeQuery();
			statement.clearParameters();

			PermResult initial = PermResult.UNKNOWN;
			if (set.next())
			{
				return set.getInt(COLUMN_PERMISSION_ALLOWED) == 1 ? "allowed" : "denied";
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList getAllPermissions(String target, String zone, int isGroup)
	{
		ArrayList list = new ArrayList();
		PreparedStatement statement = instance.statementGetAllPermissions;
		try
		{
			int targetID = (isGroup == 1 ? getGroupIDFromGroupName(target) : getPlayerIDFromPlayerName(target));
			if(targetID == -5)
			{
				list.add("ERROR");
				list.add((isGroup == 1 ? "Group " : "Player ") + target + " does not exist.");
				return list;
			}
			int zoneID = getZoneIDFromZoneName(zone);
			if(zoneID == -5)
			{
				list.add("ERROR");
				list.add("Zone " + zone + " does not exist.");
				return list;
			}
			ResultSet set;
			
			statement.setInt(1, targetID);
			statement.setInt(2, zoneID);
			statement.setInt(3, isGroup);
			set = statement.executeQuery();
			statement.clearParameters();
			
			while(set.next())
			{
				list.add(set.getString(COLUMN_PERMISSION_PERM) + ":" + (set.getInt(COLUMN_PERMISSION_ALLOWED) == 1 ? "ALLOW" : "DENY"));
			}
			if(list.isEmpty())
			{
				list.add(target + " has no individual permissions.");
			}
			return list;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
