package com.forgeessentials.util.vector;

import net.minecraft.util.MathHelper;

public class Vector2 implements Cloneable {
    public double x;
    public double y;

    public Vector2()
    {
        this(0, 0);
    }

    public Vector2(int x, int y)
    {
        this.getX() = x;
        this.getY() = y;
    }

    public Vector2(double x, double y)
    {
        this.getX() = x;
        this.getY() = y;
    }

    // Returns the values as an int
    public int intX()
    {
        return (int) Math.floor(x);
    }

    public int intY()
    {
        return (int) Math.floor(y);
    }

    /**
     * Makes a new copy of this Vector. Prevents variable referencing problems.
     */
    @Override
    public Vector2 clone()
    {
        return new Vector2(x, y);
    }

    public static double distance(Vector2 par1, Vector2 par2)
    {
        double var2 = par1.getX() - par2.getX();
        double var4 = par1.getY() - par2.getY();
        return MathHelper.sqrt_double(var2 * var2 + var4 * var4);
    }

    public static double slope(Vector2 par1, Vector2 par2)
    {
        double var2 = par1.getX() - par2.getX();
        double var4 = par1.getY() - par2.getY();
        return var4 / var2;
    }

    public void add(Vector2 par1)
    {
        x += par1.getX();
        y += par1.getY();
    }

    public void add(double par1)
    {
        x += par1;
        y += par1;
    }

    public void substract(Vector2 par1)
    {
        x -= par1.getX();
        y -= par1.getY();
    }

    public void substract(double par1)
    {
        x -= par1;
        y -= par1;
    }

    public void multiply(Vector2 par1)
    {
        x *= par1.getX();
        y *= par1.getY();
    }

    public void multiply(double par1)
    {
        x *= par1;
        y *= par1;
    }

    public void normalize()
    {
        double dist = Math.sqrt(x * x + y * y);
        x = x / dist;
        y = y / dist;
    }

    public Vector2 round()
    {
        return new Vector2(Math.round(x), Math.round(y));
    }

    public Vector2 floor()
    {
        return new Vector2(Math.floor(x), Math.floor(y));
    }

    public boolean isEquals(Vector2 vector)
    {
        return x == vector.getX() && y == vector.getY();
    }

    @Override
    public String toString()
    {
        return "Vector2 [" + x + "," + y + "]";
    }
}
