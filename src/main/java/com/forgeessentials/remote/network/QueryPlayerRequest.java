package com.forgeessentials.remote.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.remote.RemoteMessageID;

public class QueryPlayerRequest
{

    public static final String ID = RemoteMessageID.QUERY_PLAYER;

    public String name;

    public Set<String> flags = new HashSet<>();

    public QueryPlayerRequest(String name, String... flags)
    {
        this.name = name;
        for (int i = 0; i < flags.length; i++)
        {
            this.flags.add(flags[i]);
        }
    }

    public QueryPlayerRequest(String name, Collection<String> flags)
    {
        this.name = name;
        this.flags.addAll(flags);
    }

}
