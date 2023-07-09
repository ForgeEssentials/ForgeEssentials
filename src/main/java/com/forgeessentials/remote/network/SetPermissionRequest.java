package com.forgeessentials.remote.network;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.remote.RemoteMessageID;

public class SetPermissionRequest
{

    public static final String ID = RemoteMessageID.SET_PERMISSION;

    public int zoneId;

    public UserIdent user;

    public String group;

    public String permission;

    public String value;

    public SetPermissionRequest(int zoneId, UserIdent user, String permission, String value)
    {
        this.zoneId = zoneId;
        this.user = user;
        this.group = null;
        this.permission = permission;
        this.value = value;
    }

    public SetPermissionRequest(int zoneId, String group, String permission, String value)
    {
        this.zoneId = zoneId;
        this.user = null;
        this.group = group;
        this.permission = permission;
        this.value = value;
    }

}