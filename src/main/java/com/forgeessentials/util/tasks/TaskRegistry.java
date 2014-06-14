package com.forgeessentials.util.tasks;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.TimerTask;

public class TaskRegistry {
    private TimeTaskHandler timed;
    private TickTaskHandler ticks;
    private static TaskRegistry instance;

    public TaskRegistry()
    {
        instance = this;
        ticks = new TickTaskHandler();
        timed = new TimeTaskHandler();
        TickRegistry.registerTickHandler(ticks, Side.SERVER);
    }

    public static void registerTask(ITickTask task)
    {
        instance.ticks.tasks.offer(task);
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

    public void onServerStop()
    {
        instance.timed.kill();
        instance.timed = null;
    }

    public void onServerStart()
    {
        instance.timed = new TimeTaskHandler();
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

}
