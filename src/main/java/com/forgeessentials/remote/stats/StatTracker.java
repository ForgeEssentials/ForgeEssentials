package com.forgeessentials.remote.stats;

import java.util.TimerTask;

import com.forgeessentials.util.RingBuffer;

public abstract class StatTracker<T> extends TimerTask
{

    private int interval;

    private RingBuffer<T> buffer;

    private long timestamp;

    public StatTracker(int intervalSeconds, int bufferSeconds)
    {
        this.interval = intervalSeconds;
        this.buffer = new RingBuffer<T>(bufferSeconds / intervalSeconds);
    }

    public RingBuffer<T> getBuffer()
    {
        return buffer;
    }

    @Override
    public void run()
    {
        buffer.add(getValue());
        timestamp = System.currentTimeMillis();
    }

    public abstract T getValue();

    public int getInterval()
    {
        return interval * 1000;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

}