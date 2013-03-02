package com.ForgeEssentials.util.tasks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ForgeEssentials.util.OutputHandler;

public class ThreadTaskHandler extends Thread
{
	protected ConcurrentHashMap<String, IThreadTask>	tasks		= new ConcurrentHashMap<String, IThreadTask>();
	protected ConcurrentLinkedQueue<String>				taskNames	= new ConcurrentLinkedQueue<String>();
	protected ConcurrentLinkedQueue<String>				cancelled	= new ConcurrentLinkedQueue<String>();

	public ThreadTaskHandler()
	{
		this.setDaemon(true);
	}

	@Override
	public void run()
	{
		IThreadTask task;

		// go through them and do it...
		while (isAlive() && !isInterrupted())
		{
			for (String name : taskNames)
			{
				task = tasks.get(name);

				if (cancelled.contains(name))
				{
					task.die();
					cancelled.remove(name);
					taskNames.remove(name);
					tasks.remove(name);
				}
				else
				{
					task.run();
				}
			}
		}

		OutputHandler.fine("Killing ThreadTasks");

		// no longer alive....
		for (String name : taskNames)
		{
			task = tasks.get(name);
			task.die();
		}

		cancelled.clear();
		taskNames.clear();
		cancelled.clear();
	}

	public void kill()
	{
		this.interrupt();
	}
}
