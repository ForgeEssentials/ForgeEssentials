package com.forgeessentials.remote.network;

import java.util.Arrays;
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
        this.flags.addAll(Arrays.asList(flags));
    }

    public QueryPlayerRequest(String name, Collection<String> flags)
    {
        this.name = name;
        this.flags.addAll(flags);
    }

}
