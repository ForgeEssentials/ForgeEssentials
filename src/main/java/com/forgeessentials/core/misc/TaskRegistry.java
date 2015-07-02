package com.forgeessentials.core.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TaskRegistry extends ServerEventHandler
{

    public static interface TickTask
    {

        public boolean tick();

        public boolean editsBlocks();

    }

    private static TaskRegistry instance;

    public static int MAX_BLOCK_TASKS = 6;

    protected ConcurrentLinkedQueue<TickTask> tickTasks = new ConcurrentLinkedQueue<>();

    private Timer timer = new Timer();

    private Map<Runnable, TimerTask> runnableTasks = new HashMap<>();

    /* ------------------------------------------------------------ */

    public TaskRegistry()
    {
        super();
        instance = this;
    }

    public static TaskRegistry getInstance()
    {
        return instance;
    }

    @SubscribeEvent
    public void onServerStop(FEModuleServerStopEvent event)
    {
        tickTasks.clear();
        runnableTasks.clear();
        timer.cancel();
        timer = new Timer(true);
    }

    /* ------------------------------------------------------------ */

    public void schedule(TickTask task)
    {
        tickTasks.add(task);
    }

    public void remove(TickTask task)
    {
        tickTasks.remove(task);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent e)
    {
        int blockTaskCount = 0;
        for (Iterator<TickTask> iterator = tickTasks.iterator(); iterator.hasNext();)
        {
            TickTask task = iterator.next();
            if (task.editsBlocks())
            {
                if (blockTaskCount >= MAX_BLOCK_TASKS)
                    continue;
                blockTaskCount++;
            }
            if (task.tick())
                iterator.remove();
        }
    }

    /* ------------------------------------------------------------ */
    /* Timers */

    public void schedule(TimerTask task, long time)
    {
        try
        {
            timer.schedule(task, time);
        }
        catch (IllegalStateException e)
        {
            OutputHandler.felog.warn("Could not schedule timer");
            e.printStackTrace();
        }
    }

    public void scheduleRepeated(TimerTask task, long delay, long interval)
    {
        try
        {
            timer.scheduleAtFixedRate(task, delay, interval);
        }
        catch (IllegalStateException e)
        {
            OutputHandler.felog.warn("Could not schedule timer");
            e.printStackTrace();
        }
    }

    public void scheduleRepeated(TimerTask task, long interval)
    {
        scheduleRepeated(task, interval, interval);
    }

    public void remove(TimerTask task)
    {
        task.cancel();
        timer.purge();
    }

    /* ------------------------------------------------------------ */
    /* Runnable compatibility */

    protected TimerTask getTimerTask(final Runnable task, final boolean repeated)
    {
        TimerTask timerTask = runnableTasks.get(task);
        if (timerTask == null)
        {
            timerTask = new TimerTask() {
                @Override
                public void run()
                {
                    task.run();
                    if (!repeated)
                        runnableTasks.remove(task);
                }
            };
            runnableTasks.put(task, timerTask);
        }
        return timerTask;
    }

    public void schedule(Runnable task, long time)
    {
        schedule(getTimerTask(task, false), time);
    }

    public void scheduleRepeated(Runnable task, long interval)
    {
        scheduleRepeated(task, interval, interval);
    }

    public void scheduleRepeated(Runnable task, long delay, long interval)
    {
        scheduleRepeated(getTimerTask(task, true), delay, interval);
    }

    public void remove(Runnable task)
    {
        TimerTask timerTask = runnableTasks.remove(task);
        if (timerTask != null)
            remove(timerTask);
    }

    /* ------------------------------------------------------------ */

    public static long getMilliseconds(int h, int m, int s, int ms)
    {
        return ((h * 60 + m) * 60 + s) * 1000 + ms;
    }

}
