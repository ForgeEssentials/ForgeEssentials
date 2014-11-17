package com.forgeessentials.util.tasks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskRegistry {
    public static final int MAX_BLOCK_UPDATES = 10;
    private static TaskRegistry instance;
    protected ConcurrentLinkedQueue<ITickTask> tasks = new ConcurrentLinkedQueue<ITickTask>();
    private TimeTaskHandler timed;

    public TaskRegistry()
    {
        instance = this;
        timed = new TimeTaskHandler();
        FMLCommonHandler.instance().bus().register(instance);

    }

    public static void registerTask(ITickTask task)
    {
        instance.tasks.offer(task);
    }

    public static void registerSingleTask(TimerTask task, int hours, int minutes, int seconds, int milliseconds)
    {
        long time = getMillis(hours, minutes, seconds, milliseconds);
        instance.timed.addTask(task, time);
    }

    public static void registerSingleTask(Runnable task, int hours, int minutes, int seconds, int milliseconds)
    {
        TimedTaskWrapper wrapper = new TimedTaskWrapper(task);
        registerSingleTask(wrapper, hours, minutes, seconds, milliseconds);
    }

    public static void registerRecurringTask(TimerTask task, int delayHrs, int delayMin, int delaySec, int delayMilli, int intervalHrs, int intervalMin,
            int intervalSec, int intervalMilli)
    {
        long delay = getMillis(delayHrs, delayMin, delaySec, delayMilli);
        long interval = getMillis(intervalHrs, intervalMin, intervalSec, intervalMilli);

        instance.timed.addRepetingTask(task, delay, interval);
    }

    public static void registerRecurringTask(Runnable task, int delayHrs, int delayMin, int delaySec, int delayMilli, int intervalHrs, int intervalMin,
            int intervalSec, int intervalMilli)
    {
        TimedTaskWrapper wrapper = new TimedTaskWrapper(task);
        registerRecurringTask(wrapper, delayHrs, delayMin, delaySec, delayMilli, intervalHrs, intervalMin, intervalSec, intervalMilli);
    }

    public static void removeTask(TimerTask task)
    {
        try
        {
            instance.timed.removeTask(task);
        }
        catch (Throwable e)
        {
        }
    }

    public static void removeTask(Runnable task)
    {
        TimedTaskWrapper wrapper = new TimedTaskWrapper(task);
        instance.timed.removeTask(wrapper);
    }

    private static long getMillis(int hrs, int min, int sec, int milli)
    {
        long time = 0;

        // all hours.
        time = hrs;

        // all minutes
        time = time * 60 + min;

        // all seconds
        time = time * 60 + sec;

        // all milliseconds
        time = time * 1000 + milli;

        return time;
    }

    public void onServerStop()
    {
        instance.timed.kill();
        instance.timed = null;
    }

    public void onServerStart()
    {
        instance.timed = new TimeTaskHandler();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent e)
    {
        int blockCounter = 0;

        for (ITickTask task : tasks)
        {
            // remove the compelte ones
            if (task.isComplete())
            {
                task.onComplete();
                tasks.remove(task);
            }

            // add the blockCounter if it edits blocks
            else if (task.editsBlocks() && blockCounter <= MAX_BLOCK_UPDATES)
            {
                task.tick();
                blockCounter++;
            }

            // otherwise just tick
            else
            {
                task.tick();
            }
        }
    }

    private static class TimedTaskWrapper extends TimerTask {
        private final Runnable runner;

        public TimedTaskWrapper(Runnable runner)
        {
            this.runner = runner;
        }

        @Override
        public void run()
        {
            runner.run();
        }
    }

}
