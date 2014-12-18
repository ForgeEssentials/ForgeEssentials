package com.forgeessentials.api.remote;

import com.forgeessentials.util.UserIdent;
import com.google.gson.JsonObject;

/**
 *
 */
public interface RemoteHandler {

    String getID();
    
    Object handle(UserIdent ident, JsonObject data);
    
}
