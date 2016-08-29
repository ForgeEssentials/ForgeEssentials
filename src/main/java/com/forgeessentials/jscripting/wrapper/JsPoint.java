package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.commons.selections.Point;

public class JsPoint<T extends Point> extends JsWrapper<T>
{

    public JsPoint(T that)
    {
        super(that);
    }

    public int getX()
    {
        return that.getX();
    }

    public int getY()
    {
        return that.getY();
    }

    public int getZ()
    {
        return that.getZ();
    }

    public JsPoint<T> setX(int x)
    {
        that.setX(x);
        return this;
    }

    public JsPoint<T> setY(int y)
    {
        that.setY(y);
        return this;
    }

    public JsPoint<T> setZ(int z)
    {
        that.setZ(z);
        return this;
    }

    public double length()
    {
        return that.length();
    }

    public double distance(JsPoint<T> other)
    {
        return that.distance(other.getThat());
    }

    public void add(JsPoint<T> other)
    {
        that.add(other.getThat());
    }

    public void subtract(JsPoint<T> other)
    {
        that.subtract(other.getThat());
    }

    public double distance(int x, int y, int z)
    {
        return that.distance(new Point(x, y, z));
    }

    public void add(int x, int y, int z)
    {
        that.add(new Point(x, y, z));
    }

    public void subtract(int x, int y, int z)
    {
        that.subtract(new Point(x, y, z));
    }

}
