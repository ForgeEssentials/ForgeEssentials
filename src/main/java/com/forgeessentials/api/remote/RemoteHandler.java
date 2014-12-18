package com.forgeessentials.api.remote;

import com.google.gson.JsonObject;

/**
 *
 */
public interface RemoteHandler {

    String getID();

    Object handle(RemoteSession session, JsonObject data);

}
