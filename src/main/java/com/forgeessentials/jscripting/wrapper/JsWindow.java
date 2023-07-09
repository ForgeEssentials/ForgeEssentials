package com.forgeessentials.jscripting.wrapper;

import com.forgeessentials.jscripting.ScriptInstance;

/**
 * @tsd.interface window
 */
public class JsWindow
{

    private ScriptInstance script;

    public JsWindow(ScriptInstance script)
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

}
