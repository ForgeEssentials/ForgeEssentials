package com.forgeessentials.remote.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

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
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Allows querying server stats");

    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<Request> request)
    {
        if (request.data == null)
            return new RemoteResponse<Object>(request, StatsManager.getStats().keySet());

        if (request.data.graphs == null)
            request.data.graphs = new HashSet<>(StatsManager.getStats().keySet());

        for (Iterator<String> it = request.data.graphs.iterator(); it.hasNext();)
        {
            String tracker = it.next();
            if (!APIRegistry.perms.checkUserPermission(session.getUserIdent(), PERM + '.' + tracker))
                it.remove();
        }

        Map<String, GraphData> stats = new HashMap<>();
        for (String id : request.data.graphs)
        {
            StatTracker<?> tracker = StatsManager.getStats().get(id);
            if (tracker != null)
            {
                // TODO: Write better RingBuffer that can directly get reverse list and keeps track of internal size
                int elementCount = Math.max(0, (int) ((tracker.getTimestamp() - request.data.timestamp + tracker.getInterval()) / tracker.getInterval()));
                ArrayList<?> data = (elementCount <= 0) ? new ArrayList<Integer>() : tracker.getBuffer().getOrderedList(elementCount);
                Collections.reverse(data);
                stats.put(id, new GraphData(data, tracker.getInterval()));
            }
        }
        return new RemoteResponse<Object>(request, stats);
    }

    public static class GraphData
    {

        public int interval;

        public List<?> data;

        public GraphData(List<?> data, int interval)
        {
            this.data = data;
            this.interval = interval;
        }

    }

    public static class Request
    {

        public long timestamp;

        public Set<String> graphs;

    }

}
