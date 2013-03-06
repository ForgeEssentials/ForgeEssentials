package com.ForgeEssentials.chat;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.util.tasks.TaskRegistry;

public class AutoMessage implements Runnable
{
	private static Thread			thread;
	public static int				waittime;
	public static boolean			random;
	public static ArrayList<String>	msg	= new ArrayList<String>();
	public static boolean			enable;

	MinecraftServer					server;
	public static int				currentMsgID;

	public AutoMessage(MinecraftServer server)
	{
		this.server = server;

		if (msg.isEmpty())
		{
			currentMsgID = 0;
		}
		else
		{
			currentMsgID = new Random().nextInt(msg.size());
		}

		TaskRegistry.registerRecurringTask(this, 0, waittime, 0, 0, 0, waittime, 0, 0);
	}

	@Override
	public void run()
	{
		if (server.getAllUsernames().length != 0 && enable && !msg.isEmpty())
		{
			server.getConfigurationManager().sendChatMsg(msg.get(currentMsgID));

			if (random)
			{
				currentMsgID = new Random().nextInt(msg.size());
			}
			else
			{
				currentMsgID++;
				if (currentMsgID >= msg.size())
				{
					currentMsgID = 0;
				}
			}
		}
		System.gc();
	}

	public void interrupt()
	{
		thread.interrupt();
	}
}
