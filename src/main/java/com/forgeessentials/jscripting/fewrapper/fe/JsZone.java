package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;

public class JsZone<T extends Zone> extends JsWrapper<T>
{

    /**
     * @tsd.ignore
     */
    public static Map<Zone, JsZone<?>> cache = new WeakHashMap<>();

    /**
     * @tsd.ignore
     */
    public static JsZone<?> get(Zone zone)
    {
        if (zone instanceof ServerZone)
            return JsPermissions.getServerZone();
        JsZone<?> result = cache.get(zone);
        if (result == null) {
            if (zone instanceof AreaZone)
                result = new JsZone<>((AreaZone) zone); // TODO: Add additional zone types
            else if (zone instanceof WorldZone)
                result = new JsZone<>((WorldZone) zone); // TODO: Add additional zone types
            else
                result = new JsZone<>(zone);
        }
        return result;
    }

    protected JsZone(T that)
    {
        super(that);
    }

    public int getId()
    {
        return that.getId();
    }

    public String getName()
    {
        return that.getName();
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

    public JsZone<?> getParent()
    {
        return new JsZone(that.getParent());
    }

    public JsServerZone<?> getServerZone()
    {
        return JsPermissions.getServerZone();
    }

}
