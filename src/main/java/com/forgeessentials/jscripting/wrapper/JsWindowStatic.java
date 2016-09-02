package com.forgeessentials.jscripting.wrapper;

import net.minecraft.util.AxisAlignedBB;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.wrapper.world.JsPoint;
import com.forgeessentials.jscripting.wrapper.world.JsWorldPoint;

public class JsWindowStatic
{

    private ScriptInstance script;

    public JsWindowStatic(ScriptInstance script)
    {
        this.script = script;
    }

    /**
     * Set a timeout to call 'handler' after 'timeout' milliseconds.
     * 
     * @tsd.def setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
     */
    public int setTimeout(Object fn, long timeout, Object... args)
    {
        return script.setTimeout(fn, timeout, args);
    }

    /**
     * Set a interval to call 'handler' fn repeatedly each 'interval' milliseconds.
     * 
     * @tsd.def setInterval(handler: (...args: any[]) => void, interval?: any, ...args: any[]): number;
     */
    public int setInterval(Object fn, long timeout, Object... args)
    {
        return script.setInterval(fn, timeout, args);
    }

    /**
     * Clear a timeout.
     */
    public void clearTimeout(int handle)
    {
        script.clearTimeout(handle);
    }

    /**
     * Clear an interval.
     */
    public void clearInterval(int handle)
    {
        script.clearTimeout(handle);
    }

    public JsPoint<?> createPoint(int x, int y, int z)
    {
        return new JsPoint<>(new Point(x, y, z));
    }

    public JsWorldPoint<?> createWorldPoint(int dimension, int x, int y, int z)
    {
        return new JsWorldPoint<>(new WorldPoint(dimension, x, y, z));
    }

    public JsAxisAlignedBB createAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new JsAxisAlignedBB(AxisAlignedBB.fromBounds(minX, minY, minZ, maxX, maxY, maxZ));
    }

}
