package com.forgeessentials.jscripting.fewrapper.fe;

import com.forgeessentials.commons.selections.WorldPoint;

public class JsWorldPoint<T extends WorldPoint> extends JsPoint<T>
{

    public JsWorldPoint(T that)
    {
        super(that);
    }

    @SuppressWarnings("unchecked")
    public JsWorldPoint(String dim, int x, int y, int z)
    {
        this((T) new WorldPoint(dim, x, y, z));
    }

    public String getDimension()
    {
        return that.getDimension();
    }

    public void setDimension(String dim)
    {
        that.setDimension(dim);
    }

    @Override
    public JsWorldPoint<T> setX(int x)
    {
        that.setX(x);
        return this;
    }

    @Override
    public JsWorldPoint<T> setY(int y)
    {
        that.setY(y);
        return this;
    }

    @Override
    public JsWorldPoint<T> setZ(int z)
    {
        that.setZ(z);
        return this;
    }

}
