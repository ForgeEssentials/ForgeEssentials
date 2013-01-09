package com.ForgeEssentials.core.moduleLauncher;

import java.lang.reflect.Method;

public class ModuleContainer
{
	private Object module, mod;
	private Method preinit, init, postinit, serverstart, serverstarted, serverstop;
	String name;
	boolean isCore;
	
	public ModuleContainer(Class c)
	{
		assert c.isAnnotationPresent(FEModule.class) : new RuntimeException();
		
		// todo, finish
	}
}
