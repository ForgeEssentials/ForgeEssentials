package com.forgeessentials.api.remote;

/**
 * Represents a generic remote request
 */
public class RemoteRequest<T> {

    public final String id;

    public final int rid;

    public final RequestAuth auth;

    public final T data;

    public RemoteRequest(String id, int rid, RequestAuth auth, T data)
    {
        this.id = id;
        this.rid = rid;
        this.auth = auth;
        this.data = data;
    }

}
