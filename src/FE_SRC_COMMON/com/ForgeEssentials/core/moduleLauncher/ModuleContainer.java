package com.ForgeEssentials.core.moduleLauncher;

import com.ForgeEssentials.core.moduleLauncher.FEModule.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModuleContainer
{
	private Object module, mod;
	private Method preinit, init, postinit, serverstart, serverstarted, serverstop;
	String name;
	boolean isCore;
	
	public ModuleContainer(Class c)
	{
		// if this isn't there... il be mad.
		assert c.isAnnotationPresent(FEModule.class) : new RuntimeException();
		FEModule annot = (FEModule) c.getAnnotation(FEModule.class);
		name = annot.name();
		isCore = annot.isCore();
		
		// todo, finish
		for (Method m : c.getDeclaredMethods())
		{
			if (m.isAnnotationPresent(PreInit.class))
			{
				
			}
		}
		
		for (Field f : c.getDeclaredFields())
		{
			
		}
	}
}
