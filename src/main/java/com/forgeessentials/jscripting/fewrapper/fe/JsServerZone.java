package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;

public class JsServerZone<T extends ServerZone> extends JsZone<T>
{

    protected JsServerZone(T that)
    {
        super(that);
    }

    public JsZone<?> getRootZone()
    {
        return JsZone.get(that.getRootZone());
    }

    public boolean groupExists(String name)
    {
        return that.groupExists(name);
    }

    public boolean createGroup(String name)
    {
        return that.createGroup(name);
    }

    public List<JsZone<?>> getZonesAt(JsWorldPoint<?> worldPoint)
    {
        List<Zone> zones = that.getZonesAt(worldPoint.getThat());
        List<JsZone<?>> result = new ArrayList<>(zones.size());
        for (Zone zone : zones)
            result.add(JsZone.get(zone));
        return result;
    }

    public JsZone getZoneAt(JsWorldPoint<?> worldPoint)
    {
        return JsZone.get(that.getZoneAt(worldPoint.getThat()));
    }

    public List<String> getPlayerGroups(JsEntityPlayer player)
    {
        SortedSet<GroupEntry> groups = that.getPlayerGroups(UserIdent.get(player.getThat()));
        List<String> result = new ArrayList<>(groups.size());
        for (GroupEntry group : groups)
            result.add(group.getGroup());
        return result;
    }
}
