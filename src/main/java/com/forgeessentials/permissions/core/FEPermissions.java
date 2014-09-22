package com.forgeessentials.permissions.core;


public final class FEPermissions {

	public static final String MSG_NO_COMMAND_PERM = "You don't have permissions to use this command!";
	public static final String MSG_NO_PERM = "You don't have permissions for that!";

	// ------------------------------------------------
	// -- Internal properties
	// ------------------------------------------------

	public static final String FE_INTERNAL = "fe.internal";

	public static final String ZONE_ENTRY_MESSAGE = FE_INTERNAL + ".zone.entry";
	public static final String ZONE_EXIT_MESSAGE = FE_INTERNAL + ".zone.exit";

	public static final String PREFIX = FE_INTERNAL + ".prefix";
	public static final String SUFFIX = FE_INTERNAL + ".suffix";

	public static final String GROUP = FE_INTERNAL + ".group";
	public static final String GROUP_ID = FE_INTERNAL + ".group.id";

	public static final String PLAYER_UUID = FE_INTERNAL + ".player.uuid";
	public static final String PLAYER_NAME = FE_INTERNAL + ".player.name";
	public static final String PLAYER_GROUPS = FE_INTERNAL + ".player.groups";

	// ------------------------------------------------
	// -- Permissions
	// ------------------------------------------------

	public static final String FE = "fe";
	
	public static final String PERM = FE + ".perm";
	public static final String PERM_ALL = PERM + ".*";
	public static final String PERM_LIST = PERM + ".list";
	public static final String PERM_USER = PERM + ".user";
	public static final String PERM_GROUP = PERM + ".group";
	
	public static final String ZONE = PERM + ".zone";
	public static final String ZONE_ALL = ZONE + ".*";
	public static final String ZONE_LIST = ZONE + ".list";
	public static final String ZONE_INFO = ZONE + ".info";
	public static final String ZONE_DEFINE = ZONE + ".define";
	public static final String ZONE_REDEFINE = ZONE + ".redefine";
	public static final String ZONE_DELETE = ZONE + ".delete";
	public static final String ZONE_SETTINGS = ZONE + ".settings";

}