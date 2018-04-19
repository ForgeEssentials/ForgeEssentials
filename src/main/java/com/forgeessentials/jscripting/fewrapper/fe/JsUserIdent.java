package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorldServer;

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
        return JsEntityPlayer.get(that.getPlayer());
    }

    public JsEntityPlayer getFakePlayer()
    {
        return JsEntityPlayer.get(that.getFakePlayer());
    }

    public JsEntityPlayer getFakePlayer(JsWorldServer world)
    {
        return JsEntityPlayer.get(that.getFakePlayer(world.getThat()));
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

    public JsPlayerInfo getPlayerInfo()
    {
        return new JsPlayerInfo(that.getUuid());
    }

    public JsWallet getWallet() {
        if (APIRegistry.economy != null)
        {
            return new JsWallet(APIRegistry.economy.getWallet(that));
        } else {
            return null;
        }
    }
}
