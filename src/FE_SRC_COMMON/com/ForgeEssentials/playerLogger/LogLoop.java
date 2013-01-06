package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ForgeEssentials.playerLogger.types.logEntry;
import com.ForgeEssentials.util.OutputHandler;

public class LogLoop implements Runnable
{
	private boolean run = true;

	@Override
	public void run()
	{
		OutputHandler.debug("Started running the logger " + run);
		while (run)
		{
			int i = 0;
			while (i < ModulePlayerLogger.interval)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				i++;
			}
			OutputHandler.debug("Making logs");
			sendLogs();
			OutputHandler.debug("Done.");
		}
	}

	public void sendLogs()
	{
		try
		{
			Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
			for (logEntry type : ModulePlayerLogger.logTypes)
			{
				type.makeEntries(connection);
			}
			connection.close();
		}
		catch (SQLException e1)
		{
			OutputHandler.SOP("Could not connect to database!");
			OutputHandler.SOP(e1.getMessage());
			e1.printStackTrace();
		}
	}

	public void end()
	{
		run = false;
	}
}
