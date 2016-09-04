package com.forgeessentials.jscripting.fewrapper.fe;

import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.jscripting.wrapper.mc.JsAreaBase;

public class JsWorldArea<T extends WorldArea> extends JsAreaBase<T>
{

    public JsWorldArea(T that)
    {
        super(that);
    }

}
