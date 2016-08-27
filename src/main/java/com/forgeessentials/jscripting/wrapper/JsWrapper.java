package com.forgeessentials.jscripting.wrapper;

public class JsWrapper<T>
{

    protected T that;

    public JsWrapper(T that)
    {
        this.that = that;
    }

    public T getThat()
    {
        return that;
    }

}