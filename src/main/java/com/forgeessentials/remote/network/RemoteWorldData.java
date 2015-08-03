package com.forgeessentials.remote.network;

import com.forgeessentials.playerlogger.entity.WorldData;

public class RemoteWorldData
{

    public int id;

    public String name;

    public RemoteWorldData(WorldData worldData)
    {
        this.id = worldData.id;
        this.name = worldData.name;
    }

}
