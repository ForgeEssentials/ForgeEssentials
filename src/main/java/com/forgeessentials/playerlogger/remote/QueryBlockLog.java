package com.forgeessentials.playerlogger.remote;

import java.util.List;

import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.remote.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.PL_QUERY_LOG_BLOCKS)
public class QueryBlockLog extends GenericRemoteHandler<QueryLogRequest>
{

    public QueryBlockLog()
    {
        super(ModulePlayerLogger.PERM, QueryLogRequest.class);
    }

    @Override
    protected RemoteResponse<QueryLogResponse<Action01Block>> handleData(RemoteSession session, RemoteRequest<QueryLogRequest> request)
    {
        QueryLogRequest data = request.data == null ? new QueryLogRequest() : request.data;
        List<Action01Block> result;
        if (data.hasArea())
            result = ModulePlayerLogger.getLogger().getLoggedBlockChanges(data.getArea(), data.startTime, data.endTime, 0, data.getLimit());
        else
            result = ModulePlayerLogger.getLogger().getLoggedBlockChanges(data.getPoint(), data.startTime, data.endTime, 0, data.getLimit());
        return new RemoteResponse<>(request, new QueryLogResponse<>(request.data, result));
    }

}
