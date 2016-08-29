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

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof JsWrapper<?>)
        {
            Object that2 = ((JsWrapper<?>) obj).getThat();
            if (that == that2)
                return true;
            return that.equals(that2);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return that.hashCode();
    }

}