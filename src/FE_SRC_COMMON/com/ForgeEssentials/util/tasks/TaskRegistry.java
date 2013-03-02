package com.ForgeEssentials.util.tasks;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class TaskRegistry
{
	private ThreadTaskHandler	threads;
	private TickTaskHandler		ticks;
	private static TaskRegistry	instance;

	public TaskRegistry()
	{
		threads = new ThreadTaskHandler();
		ticks = new TickTaskHandler();
		TickRegistry.registerTickHandler(ticks, Side.SERVER);
	}

	public static void registerTask(ITickTask task)
	{
		instance.ticks.tasks.offer(task);
	}

	/**
	 * Thread tasks may be registered at any time, however all of them will terminate onServerStop
	 * @param task
	 */
	public static void registerTask(IThreadTask task)
	{
		instance.threads.taskNames.offer(task.getName());
		instance.threads.tasks.put(task.getName(), task);
	}

	public static void cancelThreadTask(String name)
	{
		instance.threads.cancelled.offer(name);
	}
	
	public void serverStart()
	{
		threads = new ThreadTaskHandler();
		threads.start();
	}
	
	public void serverStop()
	{
		threads.interrupt();
		threads = null;
	}
}
