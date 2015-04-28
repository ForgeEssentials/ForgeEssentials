package com.forgeessentials.remote.handler.permission;

import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.permissions.commands.PermissionCommandParser;

@FERemoteHandler(id = "set_permission")
public class SetPermissionHandler extends GenericRemoteHandler<SetPermissionHandler.Request> {

    public static final String PERM = QueryPermissionsHandler.PERM + ".set";

    public SetPermissionHandler()
    {
        super(PERM, SetPermissionHandler.Request.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.OP, "Allows to change permissions through remote");
    }

    @Override
    protected RemoteResponse<Object> handleData(RemoteSession session, RemoteRequest<SetPermissionHandler.Request> request)
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

    public static class Request {

        public int zoneId;

        public UserIdent user;

        public String group;

        public String permission;

        public String value;

        public Request(int zoneId, UserIdent user, String permission, String value)
        {
            this.zoneId = zoneId;
            this.user = user;
            this.group = null;
            this.permission = permission;
            this.value = value;
        }

        public Request(int zoneId, String group, String permission, String value)
        {
            this.zoneId = zoneId;
            this.user = null;
            this.group = group;
            this.permission = permission;
            this.value = value;
        }
    }

}
