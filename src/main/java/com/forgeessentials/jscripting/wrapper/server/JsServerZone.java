package com.forgeessentials.jscripting.wrapper.server;

import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsServerZone<T extends ServerZone> extends JsWrapper<T>
{

    public JsServerZone(T that)
    {
        super(that);
    }

}
