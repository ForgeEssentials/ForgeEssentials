package com.forgeessentials.jscripting.fewrapper.fe;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;

public class JsZone<T extends Zone> extends JsWrapper<T>
{

    public JsZone(T that)
    {
        super(that);
    }

    public int getId()
    {
        return that.getId();
    }

    public boolean isPlayerInZone(JsEntityPlayer player)
    {
        return that.isPlayerInZone(player.getThat());
    }

    public boolean isInZone(JsWorldPoint<?> point)
    {
        return that.isInZone(point.getThat());
    }

    public boolean isInZone(JsWorldArea<?> point)
    {
        return that.isInZone(point.getThat());
    }

    public boolean isPartOfZone(JsWorldArea<?> point)
    {
        return that.isPartOfZone(point.getThat());
    }

    public String getName()
    {
        return that.getName();
    }

    public JsZone<?> getParent()
    {
        return new JsZone(that.getParent());
    }

    public JsServerZone<?> getServerZone()
    {
        return JsPermissions.getServerZone();
    }
}
