package com.forgeessentials.api.remote;

import com.google.gson.JsonElement;

/**
 * Represents a generic remote request
 */
public class RemoteRequest<T>
{

    public String id;

    public int rid;

    public RequestAuth auth;

    public T data;

    public RemoteRequest(String id, int rid, RequestAuth auth, T data)
    {
        this.id = id;
        this.rid = rid;
        this.auth = auth;
        this.data = data;
    }

    public RemoteRequest(String id, RequestAuth auth, T data)
    {
        this(id, 0, auth, data);
    }

    public RemoteRequest(String id, T data)
    {
        this(id, null, data);
    }

    public static <T> RemoteRequest<T> transform(RemoteRequest<?> request, T newData)
    {
        return new RemoteRequest<T>(request.id, request.rid, request.auth, newData);
    }

    public static class JsonRemoteRequest extends RemoteRequest<JsonElement>
    {

        public JsonRemoteRequest(String id, int rid, RequestAuth auth, JsonElement data)
        {
            super(id, rid, auth, data);
        }

        public JsonRemoteRequest(String id, RequestAuth auth, JsonElement data)
        {
            super(id, auth, data);
        }

        public JsonRemoteRequest(String id, JsonElement data)
        {
            super(id, data);
        }

    }

}
