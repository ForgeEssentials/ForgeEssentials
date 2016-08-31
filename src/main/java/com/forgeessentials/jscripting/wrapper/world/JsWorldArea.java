package com.forgeessentials.jscripting.wrapper.world;

import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.jscripting.wrapper.JsAreaBase;

public class JsWorldArea<T extends WorldArea> extends JsAreaBase<T>
{

    public JsWorldArea(T that)
    {
        super(that);
    }

}
