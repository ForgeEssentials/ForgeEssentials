package com.ForgeEssentials.playerLogger;

import java.util.HashSet;
import java.util.Iterator;

import com.ForgeEssentials.playerLogger.ModulePlayerLogger.logEntry;
import com.ForgeEssentials.util.OutputHandler;

public class Logger implements Runnable
{
	private boolean run = true;

	@Override
	public void run() 
	{
		OutputHandler.debug("Started running the logger");
		while (run) 
		{
			try 
			{
				Thread.sleep(1000 * ModulePlayerLogger.interval);
			}
			catch (final InterruptedException e){}
			OutputHandler.SOP("Making logs");
			makeLogs();
		}
	}

	public void makeLogs() 
	{
		MySQLConnector connector = new MySQLConnector();
		Iterator<logEntry> i = ModulePlayerLogger.buffer.iterator();
		HashSet<logEntry> delBuffer = new HashSet<logEntry>();
		while (i.hasNext())
		{
			logEntry log = i.next();
			delBuffer.add(log);
			connector.makeLog(log);
		}
		ModulePlayerLogger.buffer.removeAll(delBuffer);
		connector.close();
	}

	public void end() 
	{
		run  = false;
	}
}
