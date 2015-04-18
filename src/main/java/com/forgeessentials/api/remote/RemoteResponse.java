package com.forgeessentials.api.remote;

import com.google.gson.JsonElement;

/**
 * Represents a generic remote response
 */
public class RemoteResponse<T> {

    public String id;

    public int rid;

    public boolean success;

    public String message;

    public T data;

    public RemoteResponse(String id, int rid, boolean success, String message, T data)
    {
        this.id = id;
        this.rid = rid;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public RemoteResponse(String id, T data)
    {
        this.id = id;
        this.rid = 0;
        this.success = true;
        this.message = null;
        this.data = data;
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
        return new RemoteResponse<String>(id, rid, false, message, null);
    }

    public static RemoteResponse<String> error(RemoteRequest<?> request, String message)
    {
        return error(request.id, request.rid, message);
    }

    public static RemoteResponse<String> ok(String id, int rid, String message)
    {
        return new RemoteResponse<String>(id, rid, true, message, null);
    }

    public static RemoteResponse<String> ok(RemoteRequest<?> request, String message)
    {
        return ok(request.id, request.rid, message);
    }

    public static RemoteResponse<String> ok(String id, int rid)
    {
        return ok(id, rid, "ok");
    }

    public static RemoteResponse<String> ok(RemoteRequest<?> request)
    {
        return ok(request.id, request.rid);
    }

    public static <T> RemoteResponse<T> transform(RemoteResponse<?> response, T newData)
    {
        return new RemoteResponse<T>(response.id, response.rid, response.success, response.message, newData);
    }

    public static class JsonRemoteResponse extends RemoteResponse<JsonElement> {

        public JsonRemoteResponse(String id, int rid, boolean success, String message, JsonElement data)
        {
            super(id, rid, success, message, data);
        }

        public JsonRemoteResponse(String id, JsonElement data)
        {
            super(id, data);
        }

    }
    
}
