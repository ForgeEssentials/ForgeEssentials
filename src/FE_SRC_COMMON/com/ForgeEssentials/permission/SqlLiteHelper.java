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
	private static final String		COLUMN_ZONE_NAME				= "name";
	private static final String		COLUMN_ZONE_PARENT				= "parent";
	private static final String		COLUMN_ZONE_DIM					= "dimension";

	// columns for the group table
	private static final String		COLUMN_GROUP_GROUPID			= "groupID";
	private static final String		COLUMN_GROUP_NAME				= "name";
	private static final String		COLUMN_GROUP_PARENT				= "parent";
	private static final String		COLUMN_GROUP_PREFIX				= "prefix";
	private static final String		COLUMN_GROUP_SUFFIX				= "suffix";
	private static final String		COLUMN_GROUP_PRIORITY			= "priority";
	private static final String		COLUMN_GROUP_ZONE				= "zone";

	// group connector table.
	private static final String		COLUMN_GROUP_CONNECTOR_GROUPID	= "group";
	private static final String		COLUMN_GROUP_CONNECTOR_PLAYERID	= "player";
	private static final String		COLUMN_GROUP_CONNECTOR_ZONEID	= "zone";

	// ladder table
	private static final String		COLUMN_LADDER_LADDERID			= "ladder";
	private static final String		COLUMN_LADDER_GROUPID			= "group";
	private static final String		COLUMN_LADDER_ZONEID			= "zone";
	private static final String		COLUMN_LADDER_RANK				= "rank";

	// ladderName table
	private static final String		COLUMN_LADDER_NAME_LADDERID		= "ladderID";
	private static final String		COLUMN_LADDER_NAME_NAME			= "name";

	// player table
	private static final String		COLUMN_PLAYER_PLAYERID			= "playerID";
	private static final String		COLUMN_PLAYER_USERNAME			= "username";

	// permissions table
	private static final String		COLUMN_PERMISSIONS_TARGET		= "target";
	private static final String		COLUMN_PERMISSIONS_ISGROUP		= "isGroup";
	private static final String		COLUMN_PERMISSIONS_PERM			= "perm";
	private static final String		COLUMN_PERMISSIONS_ALLOWED		= "allowed";
	private static final String		COLUMN_PERMISSIONS_ZONEID		= "zoneID";
	
	private final PreparedStatement statementGetLadder;

	public SqlLiteHelper()
	{
		instance = this;
		connect();

		if (generate)
			generate();
		
		try
		{
			StringBuilder query = new StringBuilder("SELECT ")
			.append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME).append(", ")
			.append(TABLE_LADDER).append(".").append(COLUMN_LADDER_RANK)
			.append(" FROM ").append(TABLE_LADDER)
			.append(" WHERE ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append("=").append("?")
			.append(" INNER JOIN ").append(TABLE_GROUP)
			.append(" ON ").append(TABLE_LADDER).append(".").append(COLUMN_LADDER_GROUPID).append("=").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_GROUPID)
			.append(" ORDER BY ").append(TABLE_GROUP).append(".").append(COLUMN_GROUP_NAME)
			;
			
			statementGetLadder = instance.db.prepareStatement(query.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		OutputHandler.SOP("Statement preperation succesfull");
	}

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
			String zoneTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_ZONE).append("(").append(this.COLUMN_ZONE_ZONEID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ").append(this.COLUMN_ZONE_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ").append(this.COLUMN_ZONE_DIM).append(" SMALLINT NOT NULL, ").append(this.COLUMN_ZONE_PARENT).append(" INT NOT NULL").append(")").toString();
			String groupTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(").append(this.COLUMN_GROUP_GROUPID).append("  INTEGER PRIMARY KEY AUTOINCREMENT, ").append(this.COLUMN_GROUP_NAME).append(" VARCHAR(40) NOT NULL UNIQUE, ").append(this.COLUMN_GROUP_PARENT).append(" INTEGER NOT NULL, ").append(this.COLUMN_GROUP_PRIORITY).append(" SMALLINT NOT NULL, ").append(this.COLUMN_GROUP_ZONE).append(" INTEGER FOREIGN KEY REFERENCES ").append(TABLE_ZONE).append("(").append(COLUMN_ZONE_ZONEID).append("), ").append(this.COLUMN_GROUP_PREFIX).append(" VARCHAR(20) DEFAULT \"\", ").append(this.COLUMN_GROUP_SUFFIX).append(" VARCHAR(20) DEFAULT \"\" ").toString();
			String ladderTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER).append("(").append(this.COLUMN_LADDER_LADDERID).append(" INTEGER FOREIGN KEY (").append(this.COLUMN_LADDER_LADDERID).append(") REFERENCES ").append(TABLE_LADDER_NAME).append("(").append(COLUMN_LADDER_NAME_LADDERID).append("), ").append(this.COLUMN_LADDER_GROUPID).append(" INTEGER FOREIGN KEY REFERENCES ").append(TABLE_GROUP).append("(").append(COLUMN_GROUP_GROUPID).append("), ").append(this.COLUMN_LADDER_ZONEID).append(" INTEGER FOREIGN KEY REFERENCES ").append(TABLE_ZONE).append("(").append(COLUMN_ZONE_ZONEID).append("), ").append(this.COLUMN_LADDER_RANK).append(" SMALLINT ").toString();
			String ladderNameTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_LADDER_NAME).append("(").append(this.COLUMN_LADDER_NAME_LADDERID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ").append(this.COLUMN_LADDER_NAME_NAME).append(" VARCHAR(40) NOT NULL UNIQUE").append(")").toString();
			String playerTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP).append("(").append(this.COLUMN_PLAYER_PLAYERID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ").append(this.COLUMN_PLAYER_USERNAME).append(" VARCHAR(20) NOT NULL UNIQUE").append(")").toString();
			String groupConnectorTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_GROUP_CONNECTOR).append("(").append(this.COLUMN_GROUP_CONNECTOR_GROUPID).append(" INTEGER FOREIGN KEY (").append(this.COLUMN_GROUP_CONNECTOR_GROUPID).append(") REFERENCES ").append(TABLE_GROUP).append("(").append(COLUMN_GROUP_GROUPID).append("), ").append(this.COLUMN_GROUP_CONNECTOR_PLAYERID).append(" INTEGER FOREIGN KEY REFERENCES ").append(TABLE_PLAYER).append("(").append(COLUMN_PLAYER_PLAYERID).append("), ").append(this.COLUMN_GROUP_CONNECTOR_ZONEID).append(" INTEGER FOREIGN KEY REFERENCES ").append(TABLE_ZONE).append("(").append(COLUMN_ZONE_ZONEID).append(") )").toString();
			String permissionTable = (new StringBuilder("CREATE TABLE IF NOT EXISTS ")).append(TABLE_PERMISSION).append("(").append(this.COLUMN_PERMISSIONS_TARGET).append(" VARCHAR(40), ").append(this.COLUMN_PERMISSIONS_ISGROUP).append(" TINYINT, ").append(this.COLUMN_PERMISSIONS_PERM).append(" TEXT, ").append(this.COLUMN_PERMISSIONS_ALLOWED).append(" TINYINT, ").append(this.COLUMN_PERMISSIONS_ZONEID).append(" INTEGER FOREIGN KEY REFERENCES ").append(TABLE_ZONE).append("(").append(COLUMN_ZONE_ZONEID).append(") ").append(")").toString();

			
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

	public static ArrayList<Group> getLadderForGroup(Group g, String zoneID)
	{
		ArrayList<Group> list = new ArrayList<Group>();
		try
		{
			ResultSet set = instance.statementGetLadder.executeQuery();
			
			String group;
			
			while(set.next())
			{
				//set.getString(columnLabel)
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class Compare implements Comparator
	{

		@Override
		public int compare(Object arg0, Object arg1)
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
