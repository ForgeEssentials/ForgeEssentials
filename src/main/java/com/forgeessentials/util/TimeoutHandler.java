package com.forgeessentials.util;


public abstract class TimeoutHandler {

    private long lastTime;
    
    private int interval;
    
    public TimeoutHandler(int interval)
    {
        this.interval = interval;
    }
    
    public TimeoutHandler(int interval, int initialDelay)
    {
        this.interval = interval;
        this.lastTime = System.currentTimeMillis() - interval + initialDelay;
    }
    
    public void run()
    {
        if (System.currentTimeMillis() - lastTime > interval)
        {
            doRun();
            lastTime = System.currentTimeMillis();
        }
    }
    
    protected abstract void doRun();

    public long getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(long lastTime)
    {
        this.lastTime = lastTime;
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }
    
}
