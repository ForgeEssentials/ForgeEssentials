package com.forgeessentials.api.remote;

import com.google.gson.JsonElement;

/**
 * Represents a generic remote response
 */
public class RemoteResponse<T>
{

    public String id;

    public int rid;

    public boolean success;

    public String message;

    public long timestamp = System.currentTimeMillis();

    public T data;

    private RemoteResponse(String id, int rid, boolean success, String message, T data)
    {
        this.id = id;
        this.rid = rid;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    private RemoteResponse(String id, int rid, boolean success, String message)
    {
        this.id = id;
        this.rid = rid;
        this.success = success;
        this.message = message;
    }

    public RemoteResponse(String id, int rid, T data)
    {
        this.id = id;
        this.rid = rid;
        this.success = true;
        this.data = data;
    }

    public RemoteResponse(String id, T data)
    {
        this(id, 0, data);
    }

    public RemoteResponse(RemoteRequest<?> request, T data)
    {
        this.id = request.id;
        this.rid = request.rid;
        this.success = true;
        this.message = null;
        this.data = data;
    }

    public static RemoteResponse<String> error(String id, int rid, String message)
    {
        return new RemoteResponse<String>(id, rid, false, message);
    }

    public static RemoteResponse<String> error(RemoteRequest<?> request, String message)
    {
        return error(request.id, request.rid, message);
    }

    public static RemoteResponse<?> success(String id, int rid, String message)
    {
        return new RemoteResponse<Object>(id, rid, true, message);
    }

    public static RemoteResponse<?> success(RemoteRequest<?> request, String message)
    {
        return success(request.id, request.rid, message);
    }

    public static RemoteResponse<?> success(String id, int rid)
    {
        return success(id, rid, null);
    }

    public static RemoteResponse<?> success(RemoteRequest<?> request)
    {
        return success(request.id, request.rid);
    }

    public static <T> RemoteResponse<T> transform(RemoteResponse<?> response, T newData)
    {
        return new RemoteResponse<T>(response.id, response.rid, response.success, response.message, newData);
    }

    public static class JsonRemoteResponse extends RemoteResponse<JsonElement>
    {

        public JsonRemoteResponse(String id, int rid, boolean success, String message, JsonElement data)
        {
            super(id, rid, success, message, data);
        }

        public JsonRemoteResponse(String id, JsonElement data)
        {
            super(id, data);
        }

    }

    public static class Ignore extends RemoteResponse<Object>
    {

        public Ignore()
        {
            super((String) null, null);
        }

    }

}
