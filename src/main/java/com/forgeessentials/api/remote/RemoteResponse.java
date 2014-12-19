package com.forgeessentials.api.remote;

/**
 * Represents a generic remote response
 */
public class RemoteResponse<T> {

    public int rid;

    public final boolean success;

    public final T data;

    public RemoteResponse(T data)
    {
        this.rid = 0;
        this.success = true;
        this.data = data;
    }

    public RemoteResponse(int rid, T data)
    {
        this.rid = rid;
        this.success = true;
        this.data = data;
    }

    protected RemoteResponse(int rid)
    {
        this.rid = rid;
        this.success = false;
        this.data = null;
    }

    public static class Error extends RemoteResponse<Object> {

        public final String error;

        public Error(int rid, String error)
        {
            super(rid);
            this.error = error;
        }

        public Error(String error)
        {
            this(0, error);
        }

    }

}
