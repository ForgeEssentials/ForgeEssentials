package com.forgeessentials.remote;

public abstract class RemoteMessageID {

	public static final String QUERY_REMOTE_CAPABILITIES = "query_remote_capabilities";

	public static final String QUERY_PERMISSION_REGISTERED = "query_permission_registered";
	public static final String QUERY_PERMISSIONS = "query_permissions";
	public static final String SET_PERMISSION = "set_permission";

	public static final String QUERY_PLAYER = "query_player";

	public static final String QUERY_SERVER_LOG = "query_log_server";
	public static final String QUERY_STATS = "query_stats";

	public static final String CHAT = "chat";
	public static final String PUSH_CHAT = "push_chat";
	public static final String QUERY_CHAT = "query_chat";

	public static final String COMMAND = "command";
	public static final String COMMAND_LIST = "command_list";
	public static final String COMMAND_COMPLETE = "command_complete";

	public static final String PL = "pl";
	public static final String PL_QUERY_LOG = PL + ".log";
	public static final String PL_QUERY_LOG_COMMANDS = PL + ".commands";
	public static final String PL_QUERY_LOG_BLOCKS = PL + ".blocks";

}
