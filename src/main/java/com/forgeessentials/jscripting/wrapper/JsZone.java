package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.api.permissions.Zone;

public class JsZone<T extends Zone> extends JsWrapper<T>
{

    public JsZone(T that)
    {
        super(that);
    }

}
