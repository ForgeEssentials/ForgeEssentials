package com.forgeessentials.api.remote;

/**
 * Represents a generic remote response
 */
public class RemoteResponse<T> {

    public int rid;

    public final boolean success;
    
    public final String error;

    public final T data;

    public RemoteResponse(T data)
    {
        this.rid = 0;
        this.success = true;
        this.error = null;
        this.data = data;
    }

    public RemoteResponse(int rid, T data)
    {
        this.rid = rid;
        this.success = true;
        this.error = null;
        this.data = data;
    }

    public RemoteResponse(int rid, String error)
    {
        this.rid = rid;
        this.success = false;
        this.error = error;
        this.data = null;
    }

    public RemoteResponse(String error)
    {
        this.rid = 0;
        this.success = false;
        this.error = error;
        this.data = null;
    }

    public RemoteResponse(RemoteResponse<?> response, T data)
    {
        this.rid = response.rid;
        this.success = response.success;
        this.error = response.error;
        this.data = data;
    }

}
