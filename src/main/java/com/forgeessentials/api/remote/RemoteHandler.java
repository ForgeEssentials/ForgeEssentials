package com.forgeessentials.api.remote;

import com.google.gson.JsonElement;

/**
 *
 */
public interface RemoteHandler {

    String getID();

    RemoteResponse handle(RemoteSession session, RemoteRequest<JsonElement> data);

}
