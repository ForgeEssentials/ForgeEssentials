package com.forgeessentials.remote.data;

import com.google.gson.JsonObject;

/**
 * Represents a generic remote request
 */
public class RemoteRequest {
    
    public String id;
    
    public RequestAuth auth;
    
    public JsonObject data;

}
