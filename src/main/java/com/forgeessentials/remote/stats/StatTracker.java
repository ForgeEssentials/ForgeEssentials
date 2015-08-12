package com.forgeessentials.remote.stats;

import com.forgeessentials.util.RingBuffer;

public abstract class StatTracker<T>
{

    private int interval;

    private int tick;

    private RingBuffer<T> buffer;

    public StatTracker(int interval, int seconds)
    {
        this.interval = interval;
        this.buffer = new RingBuffer<T>(seconds / interval);
    }

    public RingBuffer<T> getBuffer()
    {
        return buffer;
    }

    protected void tick()
    {
        if (++tick >= interval * 20)
        {
            tick = 0;
            buffer.add(getValue());
        }
    }

    public abstract T getValue();

}