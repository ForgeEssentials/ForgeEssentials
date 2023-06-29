package com.forgeessentials.remote.handler.permission;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.google.gson.JsonElement;

@FERemoteHandler(id = RemoteMessageID.QUERY_PERMISSIONS)
public class QueryPermissionsHandler extends GenericRemoteHandler<JsonElement> {

	public static final String PERM = PERM_REMOTE + ".permission";

	public QueryPermissionsHandler() {
		super(null, JsonElement.class);
	}

	@Override
	protected RemoteResponse<ServerZone> handleData(RemoteSession session, RemoteRequest<JsonElement> request) {
		return new RemoteResponse<ServerZone>(request, APIRegistry.perms.getServerZone());
	}

}
