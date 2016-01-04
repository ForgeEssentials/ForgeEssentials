package com.forgeessentials.playerlogger.remote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.playerlogger.entity.BlockData;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.WorldData;
import com.forgeessentials.remote.network.RemoteBlockData;
import com.forgeessentials.remote.network.RemotePlayerData;
import com.forgeessentials.remote.network.RemoteWorldData;

public class QueryLogResponse<T extends Action>
{

    public QueryLogRequest request;

    public List<T> result;

    public Map<Integer, RemoteWorldData> worlds = new HashMap<>();

    public Map<Long, RemotePlayerData> players = new HashMap<>();

    public Map<Integer, RemoteBlockData> blocks = null;

    public QueryLogResponse(QueryLogRequest request, List<T> data)
    {
        this.request = request;
        this.result = data;

        Set<WorldData> worldSet = new HashSet<WorldData>();
        Set<PlayerData> playerSet = new HashSet<PlayerData>();
        Set<BlockData> blockSet = null;
        for (T action : data)
        {
            worldSet.add(action.world);
            playerSet.add(action.player);
            if (action instanceof Action01Block)
            {
                if (blockSet == null)
                    blockSet = new HashSet<BlockData>();
                blockSet.add(((Action01Block) action).block);
            }
        }
        
        playerSet.remove(null);
        for (PlayerData playerData : playerSet)
            players.put(playerData.id, new RemotePlayerData(playerData));
        
        worldSet.remove(null);
        for (WorldData worldData : worldSet)
            worlds.put(worldData.id, new RemoteWorldData(worldData));
        
        if (blockSet != null)
        {
            blockSet.remove(null);
            blocks = new HashMap<>();
            for (BlockData blockData : blockSet)
                blocks.put(blockData.id, new RemoteBlockData(blockData));
        }
    }

}
