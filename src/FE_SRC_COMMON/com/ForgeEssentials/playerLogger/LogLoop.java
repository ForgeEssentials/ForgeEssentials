package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.ForgeEssentials.playerLogger.types.logEntry;
import com.ForgeEssentials.util.OutputHandler;

public class LogLoop implements Runnable
{
	private boolean run = true;
	public ArrayList<logEntry> buffer = new ArrayList<logEntry>();
	
	@Override
	public void run() 
	{
		OutputHandler.debug("Started running the logger " + run);
		while (run)
		{
			int i = 0;
			while(i < ModulePlayerLogger.interval)
			{
				try 
				{
					Thread.sleep(1000);
				}
				catch (final InterruptedException e){e.printStackTrace();}
				i++;
			}
			
			if(buffer.isEmpty())
			{
				OutputHandler.debug("No logs to make");
			}
			else
			{
				OutputHandler.debug("Making logs");
				makeLogs();
				OutputHandler.debug("Done making logs");
			}
		}
	}

	public void makeLogs() 
	{
		try 
		{
			Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
			Statement s = connection.createStatement();
			
			ArrayList<logEntry> delBuffer = new ArrayList<logEntry>();
			Iterator<logEntry> i = buffer.iterator();
			while(i.hasNext())
			{
				logEntry log = i.next();
				s.execute(log.getSQL());
				delBuffer.add(log);
			}
			buffer.removeAll(delBuffer);
			
			OutputHandler.SOP("Made " + delBuffer.size() + " logs.");
			
			s.close();
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
		run  = false;
	}
}
