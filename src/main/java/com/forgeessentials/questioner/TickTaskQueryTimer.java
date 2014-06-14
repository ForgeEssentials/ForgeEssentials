package com.forgeessentials.questioner;

import com.forgeessentials.util.tasks.ITickTask;

public class TickTaskQueryTimer implements ITickTask {
    private int seconds;

    public TickTaskQueryTimer(int seconds)
    {
        this.seconds = seconds;
    }

    @Override
    public void tick()
    {

    }

    @Override
    public void onComplete()
    {

    }

    @Override
    public boolean isComplete()
    {
        return false;
    }

    @Override
    public boolean editsBlocks()
    {
        return false;
    }

}
