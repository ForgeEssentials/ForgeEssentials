package com.forgeessentials.jscripting.wrapper.mc;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsZone<T extends Zone> extends JsWrapper<T>
{

    public JsZone(T that)
    {
        super(that);
    }

}
