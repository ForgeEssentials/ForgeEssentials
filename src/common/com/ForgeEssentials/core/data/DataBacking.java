package com.ForgeEssentials.core.data;

import java.util.*;

import com.ForgeEssentials.util.OutputHandler;

public class DataBacking
{
	protected static DataBacking instance;
	
	// Stores bindings between logic classes and their data classes.
	protected HashMap<Class, DataPersister> map;

	public DataBacking()
	{
		this.map = new HashMap<Class, DataPersister>();
	}
	
	public boolean hasMapping(Object o)
	{
		return this.map.containsKey(o.getClass());
	}
	
	public void saveObject(Object o)
	{
		if (hasMapping(o))
		{
			DataPersister dp = this.map.get(o.getClass());
			
			if (dp != null)
			{
				dp.saveData(o);
			}
			else
			{
				OutputHandler.SOP("DataBacking does not have an instance for " + o.getClass());
			}
		}
	}
}
