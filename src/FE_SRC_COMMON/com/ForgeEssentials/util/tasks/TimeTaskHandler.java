package com.ForgeEssentials.util.tasks;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ForgeEssentials.util.OutputHandler;

public class TimeTaskHandler
{
	private Timer timer;
	
	public TimeTaskHandler()
	{
		timer = new Timer();
	}
	
	protected void addTask(TimerTask task, long time)
	{
		timer.schedule(task, time);
	}
	
	protected void addRepetingTask(TimerTask task, long delay, long interval)
	{
		timer.scheduleAtFixedRate(task, delay, interval);
	}
}
