package com.forgeessentials.remote.handler.server;

import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FERemoteHandler(id = RemoteMessageID.QUERY_SERVER_LOG)
public class QueryServerLog extends GenericRemoteHandler<Integer>
{

    public static final String PERM_SERVER = PERM_REMOTE + ".server";
    public static final String PERM = PERM_SERVER + ".log";

    public QueryServerLog()
    {
        super(PERM, Integer.class);
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Allows querying server log");
    }

    @Override
    protected RemoteResponse<List<String>> handleData(RemoteSession session, RemoteRequest<Integer> request)
    {
        return new RemoteResponse<>(request,
                LoggingHandler.getLatestLog(request.data == null ? LoggingHandler.MAX_LOG_LENGTH : request.data));
    }

}
