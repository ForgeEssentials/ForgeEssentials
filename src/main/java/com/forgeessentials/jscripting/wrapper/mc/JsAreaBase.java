package com.forgeessentials.jscripting.wrapper.mc;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsAreaBase<T extends AreaBase> extends JsWrapper<T>
{

    public JsAreaBase(T that)
    {
        super(that);
    }

}
