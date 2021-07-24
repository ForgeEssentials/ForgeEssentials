package com.forgeessentials.jscripting.wrapper.mc;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsAreaBase<T extends AreaBase> extends JsWrapper<T>
{

    public JsAreaBase(T that)
    {
        super(that);
    }

    @SuppressWarnings("unchecked")
    public JsAreaBase(JsPoint<?> p1, JsPoint<?> p2) {
        this((T) new AreaBase(p1.getThat(), p2.getThat()));
    }

    public JsPoint<?> getHighPoint() {
        return new JsPoint<>(that.getHighPoint());
    }

    public JsPoint<?> getLowPoint() {
        return new JsPoint<>(that.getLowPoint());
    }

}
