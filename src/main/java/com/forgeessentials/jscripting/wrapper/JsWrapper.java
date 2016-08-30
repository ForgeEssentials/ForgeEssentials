package com.forgeessentials.jscripting.wrapper;

import com.google.common.base.Preconditions;

/**
 * Basic wrapped java object
 * 
 * @tsd.type JavaObject
 */
public class JsWrapper<T>
{

    /**
     * @tsd.ignore
     */
    protected T that;

    public JsWrapper(T that)
    {
        Preconditions.checkNotNull(that);
        this.that = that;
    }

    /**
     * @tsd.ignore
     */
    public T getThat()
    {
        return that;
    }

    /**
     * @tsd.def equals(obj: JavaObject): boolean;
     */
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
    public String toString()
    {
        return that.toString();
    }

    @Override
    public int hashCode()
    {
        return that.hashCode();
    }

}