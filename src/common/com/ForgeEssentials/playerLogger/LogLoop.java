package com.ForgeEssentials.playerLogger;

import java.util.ArrayList;
import java.util.Iterator;

import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class LogLoop implements Runnable
{
	private boolean run = true;
	public ArrayList<logEntry> buffer = new ArrayList<logEntry> ();
	
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
			ModulePlayerLogger.print("Making logs");
			makeLogs();
			ModulePlayerLogger.print("Done making logs");
		}
	}

	public void makeLogs() 
	{
		MySQLConnector connector = new MySQLConnector();
		Iterator<logEntry> i = buffer.iterator();
		ArrayList<logEntry> delBuffer = new ArrayList<logEntry>();
		while (i.hasNext())
		{
			logEntry log = i.next();
			delBuffer.add(log);
			connector.makeLog(log);
		}
		buffer.removeAll(delBuffer);
		connector.close();
	}

	public void end() 
	{
		run  = false;
	}
}
