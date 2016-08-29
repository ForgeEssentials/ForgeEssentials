package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import net.minecraft.world.WorldServer;

import com.forgeessentials.api.UserIdent;

public class JsUserIdent extends JsWrapper<UserIdent>
{

    public JsUserIdent(UserIdent that)
    {
        super(that);
    }

    public boolean hasUsername()
    {
        return that.hasUsername();
    }

    public boolean hasUuid()
    {
        return that.hasUuid();
    }

    public boolean hasPlayer()
    {
        return that.hasPlayer();
    }

    public boolean isFakePlayer()
    {
        return that.isFakePlayer();
    }

    public boolean isPlayer()
    {
        return that.isPlayer();
    }

    public boolean isNpc()
    {
        return that.isNpc();
    }

    public UUID getUuid()
    {
        return that.getUuid();
    }

    public String getUsername()
    {
        return that.getUsername();
    }

    public String getUsernameOrUuid()
    {
        return that.getUsernameOrUuid();
    }

    public JsEntityPlayer getPlayer()
    {
        return new JsEntityPlayer(that.getPlayer());
    }

    public JsEntityPlayer getFakePlayer()
    {
        return new JsEntityPlayer(that.getFakePlayer());
    }

    public JsEntityPlayer getFakePlayer(WorldServer world)
    {
        return new JsEntityPlayer(that.getFakePlayer(world));
    }

    public String toSerializeString()
    {
        return that.toSerializeString();
    }

    @Override
    public String toString()
    {
        return that.toString();
    }

    @Override
    public int hashCode()
    {
        return that.hashCode();
    }

    public boolean checkPermission(String permissionNode)
    {
        return that.checkPermission(permissionNode);
    }

    public String getPermissionProperty(String permissionNode)
    {
        return that.getPermissionProperty(permissionNode);
    }

}
