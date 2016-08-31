package com.forgeessentials.playerlogger.remote;

import java.util.List;

import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.remote.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.PL_QUERY_LOG)
public class QueryLog extends GenericRemoteHandler<QueryLogRequest>
{

    public QueryLog()
    {
        super(ModulePlayerLogger.PERM, QueryLogRequest.class);
    }

    @Override
    protected RemoteResponse<QueryLogResponse<Action>> handleData(RemoteSession session, RemoteRequest<QueryLogRequest> request)
    {
        QueryLogRequest data = request.data == null ? new QueryLogRequest() : request.data;
        List<Action> result;
        if (data.hasArea())
            result = ModulePlayerLogger.getLogger().getLoggedActions(data.getArea(), data.startTime, data.endTime, 0, data.getLimit());
        else
            result = ModulePlayerLogger.getLogger().getLoggedActions(data.getPoint(), data.startTime, data.endTime, 0, data.getLimit());
        return new RemoteResponse<>(request, new QueryLogResponse<>(request.data, result));
    }

}
