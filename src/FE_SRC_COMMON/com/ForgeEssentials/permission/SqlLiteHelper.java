package com.ForgeEssentials.permission;

import com.ForgeEssentials.util.OutputHandler;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteHelper
{
	// TODO: make configureable.
	private static File			file							= new File(ModulePermissions.permsFolder, "permissions.db");

	private String				DriverClass						= "org.sqlite.JDBC";
	private Connection			db;
	private boolean				generate;

	// tables
	private static final String	TABLE_ZONE						= "zones";
	private static final String	TABLE_GROUP						= "groups";
	private static final String	TABLE_GROUP_CONNECTOR			= "groupConnectors";
	private static final String	TABLE_LADDER					= "ladders";
	private static final String	TABLE_LADDER_NAME				= "ladderNames";
	private static final String	TABLE_PLAYER					= "players";
	private static final String	TABLE_PERMISSION				= "permissions";

	// columns for the zone table
	private static final String	COLUMN_ZONE_ZONEID				= "zoneID";
	private static final String	COLUMN_ZONE_NAME				= "name";
	private static final String	COLUMN_ZONE_PARENT				= "parent";
	private static final String	COLUMN_ZONE_DIM					= "dimension";

	// columns for the group table
	private static final String	COLUMN_GROUP_GROUPID			= "groupID";
	private static final String	COLUMN_GROUP_NAME				= "name";
	private static final String	COLUMN_GROUP_PARENT				= "parent";
	private static final String	COLUMN_GROUP_PREFIX				= "prefix";
	private static final String	COLUMN_GROUP_SUFFIX				= "suffix";
	private static final String	COLUMN_GROUP_PRIORITY			= "priority";
	private static final String	COLUMN_GROUP_ZONE				= "zone";

	// group connector table.
	private static final String	COLUMN_GROUP_CONNECTOR_GROUPID	= "group";
	private static final String	COLUMN_GROUP_CONNECTOR_PLAYERID	= "player";
	private static final String	COLUMN_GROUP_CONNECTOR_ZONEID	= "zone";

	// ladder table
	private static final String	COLUMN_LADDER_LADDERID			= "ladder";
	private static final String	COLUMN_LADDER_GROUPID			= "group";
	private static final String	COLUMN_LADDER_ZONEID			= "zone";
	private static final String	COLUMN_LADDER_RANK				= "rank";

	// ladderName table
	private static final String	COLUMN_LADDER_NAME_LADDERID		= "ladderID";
	private static final String	COLUMN_LADDER_NAME_NAME			= "name";
	
	// player table
	private static final String	COLUMN_PLAYER_PLAYERID			= "playerID";
	private static final String	COLUMN_PLAYER_USERNAME			= "username";
	
	// permissions table
	private static final String	COLUMN_PERMISSIONS_TARGET			= "target";
	private static final String	COLUMN_PERMISSIONS_ISGROUP			= "isGroup";
	private static final String	COLUMN_PERMISSIONS_PERM				= "perm";
	private static final String	COLUMN_PERMISSIONS_ALLOWED			= "allowed";
	private static final String	COLUMN_PERMISSIONS_ZONEID			= "zoneID";

	public SqlLiteHelper()
	{

	}

	protected void connect() throws SQLException, ClassNotFoundException
	{
		if (db != null)
		{
			db.close();
			db = null;
		}

		try
		{
			Class driverClass = Class.forName(DriverClass);

			this.db = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			throw e;
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the SQLite JDBC Driver! Does it exist in the lib directory?");
			throw e;
		}
	}

	// create tables.
	protected void generate()
	{
		String zoneTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_ZONE).append("(")
				.append(this.COLUMN_ZONE_ZONEID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(this.COLUMN_ZONE_NAME).append(" VARCHAR(40), ") 
				.append(this.COLUMN_ZONE_DIM).append(" SMALLINT, ") 
				.append(this.COLUMN_ZONE_PARENT).append(" INT ") 
				.append(")").toString();
		
		String groupTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
				.append(this.COLUMN_GROUP_GROUPID).append("  INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(this.COLUMN_GROUP_NAME).append(" VARCHAR(40), ")
				.append(this.COLUMN_GROUP_PARENT).append(" INTEGER, ")
				.append(this.COLUMN_GROUP_PRIORITY).append(" SMALLINT, ")
				.append(this.COLUMN_GROUP_ZONE).append(" INTEGER, ")
				.append(this.COLUMN_GROUP_PREFIX).append(" VARCHAR(20), ")
				.append(this.COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) ")
				.toString();
		
		String groupConnectorTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP_CONNECTOR).append("(")
				.append(this.COLUMN_GROUP_CONNECTOR_GROUPID).append(" INTEGER, ")
				.append(this.COLUMN_GROUP_CONNECTOR_PLAYERID).append(" INTEGER, ")
				.append(this.COLUMN_GROUP_CONNECTOR_ZONEID).append(" INTEGER ")
				.append(")").toString();
		
		
		String ladderTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER).append("(")
				.append(this.COLUMN_LADDER_LADDERID).append(" INTEGER, ")
				.append(this.COLUMN_LADDER_GROUPID).append(" INTEGER, ")
				.append(this.COLUMN_LADDER_ZONEID).append(" INTEGER, ")
				.append(this.COLUMN_LADDER_RANK).append(" SMALLINT ")
				.toString();
		
		String ladderNameTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER_NAME).append("(")
				.append(this.COLUMN_LADDER_NAME_LADDERID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(this.COLUMN_LADDER_NAME_NAME).append(" TEXT ")
				.append(")").toString();
		
		String playerTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(")
				.append(this.COLUMN_PLAYER_PLAYERID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
				.append(this.COLUMN_PLAYER_USERNAME).append(" VARCHAR(20) ")
				.append(")").toString();
		
		String permissionTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PERMISSION).append("(")
				.append(this.COLUMN_PERMISSIONS_TARGET).append(" VARCHAR(40), ")
				.append(this.COLUMN_PERMISSIONS_ISGROUP).append(" TINYINT, ")
				.append(this.COLUMN_PERMISSIONS_PERM).append(" TEXT, ")
				.append(this.COLUMN_PERMISSIONS_ALLOWED).append(" TINYINT, ")
				.append(this.COLUMN_PERMISSIONS_ZONEID).append(" INTEGER ")
				.append(")").toString();
	}
}
