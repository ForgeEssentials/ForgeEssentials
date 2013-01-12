package com.ForgeEssentials.core.moduleLauncher;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule.*;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePostInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerStopEvent;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModuleContainer
{
	protected static HashSet<Class>			modClasses	= new HashSet<Class>();

	private Object							module, mod;
	private IModuleConfig					configObj;
	private Class<? extends IModuleConfig>	configClass;

	// methods..
	private String							preinit, init, postinit, serverinit, serverpostinit, serverstop, reload;

	// fields
	private String							instance, container, config, parentMod;

	// other vars..
	public final String						className;
	public final String						name;
	public final boolean					isCore;
	private boolean							isLoadable = true;
	protected boolean						isValid		= true;

	public ModuleContainer(ASMData data)
	{
		// get the class....
		Class c = null;
		className = data.getClassName();

		try
		{
			c = Class.forName(className);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error trying to laod " + data.getClassName() + " as an FEModule!");
			e.printStackTrace();
			isValid = false;
			isCore = false;
			name = "INVALID-MODULE";
			return;
		}

		// checks original FEModule annotation.
		if(!c.isAnnotationPresent(FEModule.class))
			throw new IllegalArgumentException(c.getName() + " doesn't have the @FEModule annotation!");
		FEModule annot = (FEModule) c.getAnnotation(FEModule.class);
		if (annot == null)
			throw new IllegalArgumentException(c.getName() + " doesn't have the @FEModule annotation!");
		name = annot.name();
		isCore = annot.isCore();
		configClass = annot.configClass();

		// try getting the parent mod.. and register it.
		{
			Class modClass = annot.parentMod();
			Mod atMod = (Mod) annot.parentMod().getAnnotation(Mod.class);
			if(atMod == null)
				throw new RuntimeException(modClass + " isn't an @mod class!");
			// register
			if (!modClasses.contains(modClass))
			{
				OutputHandler.SOP("Modules from " + atMod.name() + " are bieng loaded");
				modClasses.add(modClass);
			}
			mod = Loader.instance().getIndexedModList().get(atMod.modid());
		}

		// check method annotations. they are all optional...
		Class[] params;
		for (Method m : c.getDeclaredMethods())
		{
			if (m.isAnnotationPresent(PreInit.class))
			{
				if(preinit != null)
					throw new RuntimeException("Only one class may be marked as PreInit");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(FEModulePreInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModulePreInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				preinit = m.getName();
			}
			else if (m.isAnnotationPresent(Init.class))
			{
				if(init != null)
					throw new RuntimeException("Only one class may be marked as Init");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(FEModuleInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				init = m.getName();
			}
			else if (m.isAnnotationPresent(PostInit.class))
			{
				if(postinit != null)
					throw new RuntimeException("Only one class may be marked as PostInit");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(FEModulePostInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModulePostInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				postinit = m.getName();
			}
			else if (m.isAnnotationPresent(ServerInit.class))
			{
				if(serverinit != null)
					throw new RuntimeException("Only one class may be marked as ServerInit");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(FEModuleServerInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleServerInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				serverinit = m.getName();
			}
			else if (m.isAnnotationPresent(ServerPostInit.class))
			{
				if(serverpostinit != null)
					throw new RuntimeException("Only one class may be marked as ServerPostInit");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(FEModuleServerPostInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleServerPostInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				serverpostinit = m.getName();
			}
			else if (m.isAnnotationPresent(ServerStop.class))
			{
				if(serverstop != null)
					throw new RuntimeException("Only one class may be marked as ServerStop");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(FEModuleServerStopEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleServerStopEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				serverstop = m.getName();
			}
			else if (m.isAnnotationPresent(Reload.class))
			{
				if(reload != null)
					throw new RuntimeException("Only one class may be marked as Reload");
				params = m.getParameterTypes();
				if(params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if(!params[0].equals(ICommandSender.class))
					throw new RuntimeException(m + " must take " + ICommandSender.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				reload = m.getName();
			}
		}

		// collect field annotations... these are also optional.
		for (Field f : c.getDeclaredFields())
		{
			if (f.isAnnotationPresent(instance.class))
			{
				if(instance != null)
					throw new RuntimeException("Only one field may be marked as Instance");
				f.setAccessible(true);
				instance = f.getName();
			}
			else if (f.isAnnotationPresent(Container.class))
			{
				if(container != null)
					throw new RuntimeException("Only one field may be marked as Container");
				if(f.getType().equals(ModuleContainer.class))
					throw new RuntimeException("This field must have the type ModuleContainer!");
				f.setAccessible(true);
				container = f.getName();
			}
			else if (f.isAnnotationPresent(Config.class))
			{
				if(config != null)
					throw new RuntimeException("Only one field may be marked as Config");
				if(!IModuleConfig.class.isAssignableFrom(f.getType()))
					throw new RuntimeException("This field must be the type IModuleConfig!");
				f.setAccessible(true);
				config = f.getName();
			}
			else if (f.isAnnotationPresent(ParentMod.class))
			{
				if(parentMod != null)
					throw new RuntimeException("Only one field may be marked as ParentMod");
				f.setAccessible(true);
				parentMod = f.getName();
			}
		}
	}

	protected void createAndPopulate()
	{
		Field f;
		Class c;
		// instantiate.
		try
		{
			c = Class.forName(className);
			module = c.newInstance();
		}
		catch (Exception e)
		{
			OutputHandler.SOP(name + " could not be instantiated. FE will not load this module.");
			e.printStackTrace();
			isLoadable = false;
			return;
		}

		// now for the fields...
		try
		{
			if (instance != null)
			{
				f = c.getDeclaredField(instance);
				f.setAccessible(true);
				f.set(module, module);
			}

			if (container != null)
			{
				f = c.getDeclaredField(container);
				f.setAccessible(true);
				f.set(module, this);
			}

			if (parentMod != null)
			{
				f = c.getDeclaredField(parentMod);
				f.setAccessible(true);
				f.set(module, mod);
			}
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error populating fields of " + name);
			Throwables.propagate(e);
		}

		// now for the config..
		if (configClass.equals(DummyConfig.class))
		{
			OutputHandler.SOP("No config specified for " + name);
			configObj = null;
			return;
		}

		try
		{
			configObj = configClass.newInstance();

			f = c.getDeclaredField(config);
			f.setAccessible(true);
			f.set(module, configObj);

		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error Instantiating or populating config for " + name);
			Throwables.propagate(e);
		}
	}

	// make the methods to run the events now...

	public void runPreInit(FMLPreInitializationEvent fmlEvent)
	{
		if (!isLoadable || preinit == null)
			return;

		FEModulePreInitEvent event = new FEModulePreInitEvent(this, fmlEvent);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(preinit, new Class[] { FEModulePreInitEvent.class });
			m.invoke(module, event);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking preInit event for " + name);
			Throwables.propagate(e);
		}
	}

	public void runInit(FMLInitializationEvent fmlEvent)
	{
		if (!isLoadable || init == null)
			return;

		FEModuleInitEvent event = new FEModuleInitEvent(this, fmlEvent);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(init, new Class[] { FEModuleInitEvent.class });
			m.invoke(module, event);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking Init event for " + name);
			Throwables.propagate(e);
		}
	}

	public void runPostInit(FMLPostInitializationEvent fmlEvent)
	{
		if (!isLoadable || postinit == null)
			return;

		FEModulePostInitEvent event = new FEModulePostInitEvent(this, fmlEvent);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(postinit, new Class[] { FEModulePostInitEvent.class });
			m.invoke(module, event);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking PostInit event for " + name);
			Throwables.propagate(e);
		}
	}

	public void runServerInit(FMLServerStartingEvent fmlEvent)
	{
		if (!isLoadable || serverinit == null)
			return;

		FEModuleServerInitEvent event = new FEModuleServerInitEvent(this, fmlEvent);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(serverinit, new Class[] { FEModuleServerInitEvent.class });
			m.invoke(module, event);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking ServerInit event for " + name);
			Throwables.propagate(e);
		}
	}

	public void runServerPostInit(FMLServerStartedEvent fmlEvent)
	{
		if (!isLoadable || serverpostinit == null)
			return;

		FEModuleServerPostInitEvent event = new FEModuleServerPostInitEvent(this, fmlEvent);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(serverpostinit, new Class[] { FEModuleServerPostInitEvent.class });
			m.invoke(module, event);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking ServerPostInit event for " + name);
			Throwables.propagate(e);
		}
	}

	public void runServerStop(FMLServerStoppingEvent fmlEvent)
	{
		if (!isLoadable || serverstop == null)
			return;

		FEModuleServerStopEvent event = new FEModuleServerStopEvent(this, fmlEvent);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(serverstop, new Class[] { FEModuleServerStopEvent.class });
			m.invoke(module, event);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking ServerStop event for " + name);
			Throwables.propagate(e);
		}
	}

	public void runReload(ICommandSender user)
	{
		if (!isLoadable || reload == null)
			return;

		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(reload, new Class[] { ICommandSender.class });
			m.invoke(module, user);
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Error with invoking Reload method for " + name);
			Throwables.propagate(e);
		}
	}

	public File getModuleDir()
	{
		return new File(ForgeEssentials.FEDIR, name);
	}

	/**
	 * May be null if the module has no config
	 * @return
	 */
	public IModuleConfig getConfig()
	{
		return configObj;
	}
}
