package com.ForgeEssentials.util.tasks;

import java.util.TimerTask;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class TaskRegistry
{
	private TimeTaskHandler		timed;
	private TickTaskHandler		ticks;
	private static TaskRegistry	instance;

	public TaskRegistry()
	{
		instance = this;
		timed = new TimeTaskHandler();
		ticks = new TickTaskHandler();
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
	
	public static void registerRecurringTask(TimerTask task, int delayHrs, int delayMin, int delaySec, int delayMilli, int intervalHrs, int intervalMin, int intervalSec, int intervalMilli)
	{
		long delay = getMillis(delayHrs, delayMin, delaySec, delayMilli);
		long interval = getMillis(intervalHrs, intervalMin, intervalSec, intervalMilli);
		
		instance.timed.addRepetingTask(task, delay, interval);
	}
	
	public static void registerRecurringTask(Runnable task, int delayHrs, int delayMin, int delaySec, int delayMilli, int intervalHrs, int intervalMin, int intervalSec, int intervalMilli)
	{
		TimedTaskWrapper wrapper = new TimedTaskWrapper(task);
		registerRecurringTask(wrapper, delayHrs, delayMin, delaySec, delayMilli, intervalHrs, intervalMin, intervalSec, intervalMilli);
	}
	
	private static class TimedTaskWrapper extends TimerTask
	{
		private final Runnable runner;
		
		public TimedTaskWrapper(Runnable runner)
		{
			this.runner = runner;
		}
		
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
		time = (time*60) + min;
		
		// all seconds
		time = (time*60) + sec;
		
		// all milliseconds
		time = (time*1000) + milli;
		
		return time;
	}
	
}
