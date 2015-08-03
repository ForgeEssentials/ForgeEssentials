package com.forgeessentials.playerlogger.remote;

import java.util.List;

import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.remote.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.PL_QUERY_LOG_BLOCKS)
public class QueryBlockLog extends GenericRemoteHandler<QueryLogRequest>
{

    public QueryBlockLog()
    {
        super(ModulePlayerLogger.PERM, QueryLogRequest.class);
    }

    @Override
    protected RemoteResponse<QueryLogResponse<ActionBlock>> handleData(RemoteSession session, RemoteRequest<QueryLogRequest> request)
    {
        QueryLogRequest data = request.data == null ? new QueryLogRequest() : request.data;
        List<ActionBlock> result;
        if (data.hasArea())
            result = ModulePlayerLogger.getLogger().getLoggedBlockChanges(data.getArea(), data.startTime, data.endTime, data.getLimit());
        else
            result = ModulePlayerLogger.getLogger().getLoggedBlockChanges(data.getPoint(), data.startTime, data.endTime, data.getLimit());
        return new RemoteResponse<QueryLogResponse<ActionBlock>>(request, new QueryLogResponse<ActionBlock>(request.data, result));
    }

}
