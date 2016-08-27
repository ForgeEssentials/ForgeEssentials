package com.forgeessentials.jscripting.wrapper;

import com.google.common.base.Preconditions;

public class JsWrapper<T>
{

    protected T that;

    public JsWrapper(T that)
    {
        Preconditions.checkNotNull(that);
        this.that = that;
    }

    public T getThat()
    {
        return that;
    }

}