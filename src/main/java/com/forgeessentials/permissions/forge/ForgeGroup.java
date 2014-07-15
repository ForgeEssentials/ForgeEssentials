package com.forgeessentials.permissions.forge;

import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.permissions.PermissionsHelper;
import com.forgeessentials.permissions.SqlHelper;
import net.minecraftforge.permissions.api.IGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Just a simple wrapper around FE's existing groups, for the sake of the ForgePerms API
 */
public class ForgeGroup implements IGroup{

    private Group wrapped;

    public ForgeGroup(Group group){
        wrapped = group;
    }

    @Override public void addPlayer(UUID playerID)
    {
        PermissionsHelper.INSTANCE.addPlayerToGroup(wrapped.name, playerID, wrapped.zoneName);

    }

    @Override public boolean removePlayer(UUID playerID)
    {
        PermissionsHelper.INSTANCE.clearPlayerGroup(wrapped.name, playerID, wrapped.zoneName);
        return true;
    }

    @Override public Collection<UUID> getAllPlayers()
    {
        ArrayList<UUID> returned = new ArrayList<UUID>();
        for (String pname : PermissionsHelper.INSTANCE.getPlayersInGroup(wrapped.name, wrapped.zoneName))
        {
            returned.add(UUID.fromString(pname));
        }

        return returned;
    }

    @Override public boolean isMember(UUID playerID)
    {
        return PermissionsHelper.INSTANCE.getPlayersInGroup(wrapped.name, wrapped.zoneName).contains(playerID.toString());
    }

    @Override public IGroup getParent()
    {
        return make(SqlHelper.getGroupForName(wrapped.parent));
    }

    @Override public IGroup setParent(IGroup parent)
    {
        return null;
    }

    @Override public String getName()
    {
        return wrapped.name;
    }

    @Override public void setName(String name)
    {
        wrapped.name = name;
    }

    public static ForgeGroup make(Group group)
    {
        return new ForgeGroup(group);
    }
}
