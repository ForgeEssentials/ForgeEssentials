package com.forgeessentials.playerlogger.remote;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.WorldData;
import com.forgeessentials.remote.network.RemotePlayerData;
import com.forgeessentials.remote.network.RemoteWorldData;

public class QueryLogResponse<T extends Action>
{

    public QueryLogRequest request;

    public List<T> result;

    public Set<RemoteWorldData> worlds = new HashSet<>();

    public Set<RemotePlayerData> players = new HashSet<>();

    public QueryLogResponse(QueryLogRequest request, List<T> data)
    {
        this.request = request;
        this.result = data;

        Set<WorldData> worldSet = new HashSet<WorldData>();
        Set<PlayerData> playerSet = new HashSet<PlayerData>();
        for (T action : data)
        {
            worldSet.add(action.world);
            playerSet.add(action.player);
        }
        worldSet.remove(null);
        playerSet.remove(null);
        for (PlayerData playerData : playerSet)
            players.add(new RemotePlayerData(playerData));
        for (WorldData worldData : worldSet)
            worlds.add(new RemoteWorldData(worldData));
    }

}
