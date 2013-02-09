package com.ForgeEssentials.chat;

import java.util.Random;

import net.minecraft.server.MinecraftServer;

public class AutoMessage implements Runnable 
{
	private static Thread thread;
	public static int waittime;
	public static boolean random;
	public static String[] msg;
	public static boolean enable;
	
	MinecraftServer server;
	int currentMsgID;
	
	public AutoMessage(MinecraftServer server)
	{
		this.server = server;
		this.currentMsgID = 0;
		
		thread = new Thread(this, "ForgeEssentials - Chat - automessage");
		thread.start();
	}
	
	@Override
	public void run()
	{
		while(server.isServerRunning())
		{
			try {thread.sleep(1000 * 60 * waittime);} catch (InterruptedException e) {e.printStackTrace();}
			
			if(enable && server.getAllUsernames().length != 0)
			{
				if(random)
				{
					currentMsgID = new Random().nextInt(msg.length);
				}
				else
				{
					currentMsgID ++;
					if(currentMsgID >= msg.length)
					{
						currentMsgID = 0;
					}
				}
				
				server.getConfigurationManager().sendChatMsg(msg[currentMsgID]);
			}
		}
	}	
}
