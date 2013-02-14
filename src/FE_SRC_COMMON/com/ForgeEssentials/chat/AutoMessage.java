package com.ForgeEssentials.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.MinecraftServer;

public class AutoMessage implements Runnable
{
	private static Thread			thread;
	public static int				waittime;
	public static boolean			random;
	public static ArrayList<String>	msg = new ArrayList<String> ();
	public static boolean			enable;

	MinecraftServer					server;
	public static int				currentMsgID;

	public AutoMessage(MinecraftServer server)
	{
		this.server = server;
		this.currentMsgID = new Random().nextInt(msg.size());

		thread = new Thread(this, "ForgeEssentials - Chat - automessage");
		thread.start();
	}

	@Override
	public void run()
	{
		while (server.isServerRunning())
		{
			try
			{
				thread.sleep(1000 * 60 * waittime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			server.getConfigurationManager().sendChatMsg(msg.get(currentMsgID));
			
			if (enable && server.getAllUsernames().length != 0)
			{
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
		}
	}
}
