package com.forgeessentials.api.permissions;

import java.util.Collection;
import java.util.List;

import com.forgeessentials.api.UserIdent;

import cpw.mods.fml.common.eventhandler.Event;

public class PermissionCheckEvent extends Event
{

    public final UserIdent ident;

    public final Collection<Zone> zones;

    public final List<String> groups;

    public final List<String> nodes;

    public String result = null;

    public PermissionCheckEvent(UserIdent ident, Collection<Zone> zones, List<String> groups, List<String> nodes)
    {
        this.ident = ident;
        this.zones = zones;
        this.groups = groups;
        this.nodes = nodes;
    }

}
