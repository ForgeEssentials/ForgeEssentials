package com.ForgeEssentials.core.preloader.asm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class FEAccessTransformer extends AccessTransformer
{
	private static FEAccessTransformer	instance;
	private static List<String>			mapFiles	= new LinkedList<String>();

	public FEAccessTransformer() throws IOException
	{
		super();
		instance = this;
		// add your access transformers here!
		mapFiles.add("forgeessentials_at.cfg");
		Iterator<String> it = mapFiles.iterator();
		while (it.hasNext())
		{
			String file = (String) it.next();
			readMapFile(file);
		}

	}

	public static void addTransformerMap(String mapFileName)
	{
		if (instance == null)
		{
			mapFiles.add(mapFileName);
		}
		else
		{
			instance.readMapFile(mapFileName);
		}
	}

	private void readMapFile(String name)
	{
		System.out.println("Adding AT map: " + name);
		try
		{
			// get a method from AccessTransformer
			Method e = AccessTransformer.class.getDeclaredMethod("readMapFile", new Class[]
			{ String.class });
			e.setAccessible(true);
			// run it with the file given.
			e.invoke(this, new Object[]
			{ name });

		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
