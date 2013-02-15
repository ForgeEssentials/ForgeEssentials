package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ForgeEssentials.playerLogger.types.logEntry;
import com.ForgeEssentials.util.OutputHandler;

public class LogLoop implements Runnable
{
	private boolean			run		= true;
	public List<logEntry>	buffer	= Collections.synchronizedList(new ArrayList<logEntry>());

	@Override
	public void run()
	{
		OutputHandler.finer("Started running the logger " + run);
		while (run)
		{
			int i = 0;
			while (i < ModulePlayerLogger.interval)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					break;
				}
				i++;
			}
			sendLogs();
		}
		
		System.gc();
	}

	public void sendLogs()
	{
		try
		{
			OutputHandler.finer("Trying to make " + buffer.size() + " logs.");
			Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
			List<logEntry> temp = new ArrayList<logEntry>(buffer);
			for (logEntry type : ModulePlayerLogger.logTypes)
			{
				type.makeEntries(connection, temp);
			}
			buffer.removeAll(temp);
			connection.close();
			OutputHandler.finer("Made " + temp.size() + " logs.");
		}
		catch (SQLException e1)
		{
			OutputHandler.info("Could not connect to database!");
			OutputHandler.info(e1.getMessage());
			e1.printStackTrace();
		}
	}

	public void end()
	{
		run = false;
	}
}
