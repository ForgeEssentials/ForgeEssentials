package com.forgeessentials.remote.handler.permission;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.network.SetPermissionRequest;

@FERemoteHandler(id = RemoteMessageID.SET_PERMISSION)
public class SetPermissionHandler extends GenericRemoteHandler<SetPermissionRequest>
{

    public static final String PERM = QueryPermissionsHandler.PERM + ".set";

    public SetPermissionHandler()
    {
        super(PERM, SetPermissionRequest.class);
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Allows to change permissions");
    }

    @Override
    protected RemoteResponse<Object> handleData(RemoteSession session, RemoteRequest<SetPermissionRequest> request)
    {
        if (request.data.permission == null)
            error("Missing permission");
        if (request.data.user != null && request.data.group != null)
            error("Only set player OR group!");
        if (request.data.user == null && request.data.group == null)
            error("Missing player or group name");

        Zone zone = APIRegistry.perms.getZoneById(request.data.zoneId);
        if (zone == null)
            error("Zone with ID %s not found", request.data.zoneId);

        if (request.data.user != null)
        {
            checkPermission(session, PermissionCommandParser.PERM_USER_PERMS);
            zone.setPlayerPermissionProperty(request.data.user, request.data.permission, request.data.value);
        }
        else
        {
            checkPermission(session, PermissionCommandParser.PERM_GROUP_PERMS);
            zone.setGroupPermissionProperty(request.data.group, request.data.permission, request.data.value);
        }

        return new RemoteResponse<Object>(request, null);
    }

}
