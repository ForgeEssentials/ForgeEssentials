package com.forgeessentials.remote.network;

import com.forgeessentials.playerlogger.entity.PlayerData;

public class RemotePlayerData
{

    public long id;

    public String uuid;

    public RemotePlayerData(PlayerData playerData)
    {
        this.id = playerData.id;
        this.uuid = playerData.uuid;
    }

}