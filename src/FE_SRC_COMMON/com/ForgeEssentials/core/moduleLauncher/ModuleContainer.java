package com.ForgeEssentials.core.moduleLauncher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import net.minecraft.command.ICommandSender;

import com.ForgeEssentials.api.modules.CallableMap;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Container;
import com.ForgeEssentials.api.modules.FEModule.DummyConfig;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.Instance;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.ParentMod;
import com.ForgeEssentials.api.modules.FEModule.PostInit;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.Reload;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerPostInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class ModuleContainer implements Comparable
{
	protected static HashSet<Class>				modClasses	= new HashSet<Class>();

	public Object								module, mod;
	private ModuleConfigBase					configObj;
	private Class<? extends ModuleConfigBase>	configClass;

	// methods..
	private String								preinit, init, postinit, serverinit, serverpostinit, serverstop, reload;

	// fields
	private String								instance, container, config, parentMod, moduleDir;

	// other vars..
	public final String							className;
	public final String							name;
	public final boolean						isCore;
	public boolean								isLoadable	= true;
	protected boolean							doesOverride;

	public ModuleContainer(ASMData data)
	{
		// get the class....
		Class c = null;
		className = data.getClassName();

		try
		{
			c = Class.forName(className);
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error trying to load " + data.getClassName() + " as a FEModule!");
			e.printStackTrace();

			isCore = false;
			name = "INVALID-MODULE";
			return;
		}

		// checks original FEModule annotation.
		if (!c.isAnnotationPresent(FEModule.class))
			throw new IllegalArgumentException(c.getName() + " doesn't have the @FEModule annotation!");
		FEModule annot = (FEModule) c.getAnnotation(FEModule.class);
		if (annot == null)
			throw new IllegalArgumentException(c.getName() + " doesn't have the @FEModule annotation!");
		name = annot.name();
		isCore = annot.isCore();
		doesOverride = annot.doesOverride();
		configClass = annot.configClass();

		// try getting the parent mod.. and register it.
		{
			mod = handleMod(annot.parentMod());
		}

		// check method annotations. they are all optional...
		Class[] params;
		for (Method m : c.getDeclaredMethods())
		{
			if (m.isAnnotationPresent(PreInit.class))
			{
				if (preinit != null)
					throw new RuntimeException("Only one class may be marked as PreInit");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(FEModulePreInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModulePreInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				preinit = m.getName();
			}
			else if (m.isAnnotationPresent(Init.class))
			{
				if (init != null)
					throw new RuntimeException("Only one class may be marked as Init");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(FEModuleInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				init = m.getName();
			}
			else if (m.isAnnotationPresent(PostInit.class))
			{
				if (postinit != null)
					throw new RuntimeException("Only one class may be marked as PostInit");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(FEModulePostInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModulePostInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				postinit = m.getName();
			}
			else if (m.isAnnotationPresent(ServerInit.class))
			{
				if (serverinit != null)
					throw new RuntimeException("Only one class may be marked as ServerInit");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(FEModuleServerInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleServerInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				serverinit = m.getName();
			}
			else if (m.isAnnotationPresent(ServerPostInit.class))
			{
				if (serverpostinit != null)
					throw new RuntimeException("Only one class may be marked as ServerPostInit");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(FEModuleServerPostInitEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleServerPostInitEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				serverpostinit = m.getName();
			}
			else if (m.isAnnotationPresent(ServerStop.class))
			{
				if (serverstop != null)
					throw new RuntimeException("Only one class may be marked as ServerStop");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(FEModuleServerStopEvent.class))
					throw new RuntimeException(m + " must take " + FEModuleServerStopEvent.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				serverstop = m.getName();
			}
			else if (m.isAnnotationPresent(Reload.class))
			{
				if (reload != null)
					throw new RuntimeException("Only one class may be marked as Reload");
				params = m.getParameterTypes();
				if (params.length != 1)
					throw new RuntimeException(m + " may only have 1 argument!");
				if (!params[0].equals(ICommandSender.class))
					throw new RuntimeException(m + " must take " + ICommandSender.class.getSimpleName() + " as a param!");
				m.setAccessible(true);
				reload = m.getName();
			}
		}

		// collect field annotations... these are also optional.
		for (Field f : c.getDeclaredFields())
		{
			if (f.isAnnotationPresent(Instance.class))
			{
				if (instance != null)
					throw new RuntimeException("Only one field may be marked as Instance");
				f.setAccessible(true);
				instance = f.getName();
			}
			else if (f.isAnnotationPresent(Container.class))
			{
				if (container != null)
					throw new RuntimeException("Only one field may be marked as Container");
				if (f.getType().equals(ModuleContainer.class))
					throw new RuntimeException("This field must have the type ModuleContainer!");
				f.setAccessible(true);
				container = f.getName();
			}
			else if (f.isAnnotationPresent(Config.class))
			{
				if (config != null)
					throw new RuntimeException("Only one field may be marked as Config");
				if (!ModuleConfigBase.class.isAssignableFrom(f.getType()))
					throw new RuntimeException("This field must be the type ModuleConfigBase!");
				f.setAccessible(true);
				config = f.getName();
			}
			else if (f.isAnnotationPresent(ParentMod.class))
			{
				if (parentMod != null)
					throw new RuntimeException("Only one field may be marked as ParentMod");
				f.setAccessible(true);
				parentMod = f.getName();
			}
			else if (f.isAnnotationPresent(ModuleDir.class))
			{
				if (moduleDir != null)
					throw new RuntimeException("Only one field may be marked as ModuleDir");
				if (!File.class.isAssignableFrom(f.getType()))
					throw new RuntimeException("This field must be the type File!");
				f.setAccessible(true);
				moduleDir = f.getName();
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
		catch (Throwable e)
		{
			OutputHandler.warning(name + " could not be instantiated. FE will not load this module.");
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

			if (moduleDir != null)
			{
				File file = new File(ForgeEssentials.FEDIR, name);
				file.mkdirs();

				f = c.getDeclaredField(moduleDir);
				f.setAccessible(true);
				f.set(module, file);
			}
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error populating fields of " + name);
			Throwables.propagate(e);
		}

		// now for the config..
		if (configClass.equals(DummyConfig.class))
		{
			OutputHandler.info("No config specified for " + name);
			configObj = null;
			return;
		}

		try
		{
			configObj = configClass.getConstructor(File.class).newInstance(new File(ForgeEssentials.FEDIR, name + "/config.cfg"));

			if (config != null)
			{
				f = c.getDeclaredField(config);
				f.setAccessible(true);
				f.set(module, configObj);
			}

		}
		catch (Throwable e)
		{
			OutputHandler.info("Error Instantiating or populating config for " + name);
			Throwables.propagate(e);
		}
	}

	// make the methods to run the events now...

	public void runPreInit(FMLPreInitializationEvent fmlEvent, CallableMap map)
	{
		if (!isLoadable || preinit == null)
			return;

		FEModulePreInitEvent event = new FEModulePreInitEvent(this, fmlEvent, map);
		try
		{
			Class c = Class.forName(className);
			Method m = c.getDeclaredMethod(preinit, new Class[] { FEModulePreInitEvent.class });
			m.invoke(module, event);
		}
		catch (Throwable e)
		{
			OutputHandler.severe("Error while invoking preInit event for " + name);
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
		catch (Throwable e)
		{
			OutputHandler.info("Error while invoking Init event for " + name);
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
			Method m = c.getDeclaredMethod(postinit, new Class[]
			{ FEModulePostInitEvent.class });
			m.invoke(module, event);
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error while invoking PostInit event for " + name);
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
			Method m = c.getDeclaredMethod(serverinit, new Class[]
			{ FEModuleServerInitEvent.class });
			m.invoke(module, event);
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error while invoking ServerInit event for " + name);
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
			Method m = c.getDeclaredMethod(serverpostinit, new Class[]
			{ FEModuleServerPostInitEvent.class });
			m.invoke(module, event);
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error while invoking ServerPostInit event for " + name);
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
			Method m = c.getDeclaredMethod(serverstop, new Class[]
			{ FEModuleServerStopEvent.class });
			m.invoke(module, event);
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error while invoking ServerStop event for " + name);
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
			Method m = c.getDeclaredMethod(reload, new Class[]
			{ ICommandSender.class });
			m.invoke(module, user);
		}
		catch (Throwable e)
		{
			OutputHandler.info("Error while invoking Reload method for " + name);
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
	public ModuleConfigBase getConfig()
	{
		return configObj;
	}

	@Override
	public int compareTo(Object o)
	{
		ModuleContainer container = (ModuleContainer) o;

		if (equals(container))
			return 0;

		if (isCore && !container.isCore)
			return 1;
		else if (!isCore && container.isCore)
			return -1;

		return name.compareTo(container.name);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ModuleContainer))
			return false;

		ModuleContainer c = (ModuleContainer) o;

		return isCore == c.isCore && name.equals(c.name) && className.equals(c.className);
	}

	private static Object handleMod(Class c)
	{
		String modid;
		Object obj = null;

		ModContainer contain = null;
		for (ModContainer container : Loader.instance().getModList())
			if (container.getMod() != null && container.getMod().getClass().equals(c))
			{
				contain = container;
				obj = container.getMod();
				break;
			}

		if (obj == null || contain == null)
			throw new RuntimeException(c + " isn't an loaded mod class!");

		modid = contain.getModId() + "--" + contain.getVersion();

		if (modClasses.add(c))
		{
			OutputHandler.info("Modules from " + modid + " are being loaded");
		}
		return obj;
	}
}
