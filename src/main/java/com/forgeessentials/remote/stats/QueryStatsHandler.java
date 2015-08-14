package com.forgeessentials.remote.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.stats.QueryStatsHandler.Request;

@FERemoteHandler(id = RemoteMessageID.QUERY_STATS)
public class QueryStatsHandler extends GenericRemoteHandler<Request>
{

    public static final String PERM = PERM_REMOTE + ".stats";

    @SuppressWarnings("unused")
    private StatsManager statsManager = new StatsManager();

    public QueryStatsHandler()
    {
        super(PERM, Request.class);
        APIRegistry.perms.registerPermission(PERM, PermissionLevel.OP, "Allows querying server stats");

    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<Request> request)
    {
        if (request.data != null && request.data != null)
            for (Iterator<String> it = request.data.iterator(); it.hasNext();)
            {
                String tracker = it.next();
                if (!APIRegistry.perms.checkUserPermission(session.getUserIdent(), PERM + '.' + tracker))
                    it.remove();
            }

        if (request.data == null)
            return new RemoteResponse<Object>(request, StatsManager.getStats().keySet());

        Map<String, List<?>> stats = new HashMap<>();
        for (String id : request.data)
        {
            StatTracker<?> tracker = StatsManager.getStats().get(id);
            if (tracker != null)
            {
                // TODO: Write better RingBuffer that can directly get reverse list and keeps track of internal size
                ArrayList<?> data = tracker.getBuffer().getOrderedList();
                Collections.reverse(data);
                stats.put(id, data);
            }
        }
        return new RemoteResponse<Object>(request, stats);
    }

    public static class Request extends HashSet<String>
    {
    }

}
