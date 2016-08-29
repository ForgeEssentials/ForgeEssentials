package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.commons.selections.WorldPoint;

public class JsWorldPoint<T extends WorldPoint> extends JsPoint<T>
{

    public JsWorldPoint(T that)
    {
        super(that);
    }

    public int getDimension()
    {
        return that.getDimension();
    }

    public void setDimension(int dim)
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
