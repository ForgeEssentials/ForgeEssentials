package com.ForgeEssentials.core.moduleLauncher;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.command.ICommandSender;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModuleLauncher
{
	public ModuleLauncher()
	{
		instance = this;
	}

	public static ModuleLauncher					instance;
	private static TreeMap<String, ModuleContainer>	containerMap	= new TreeMap<String, ModuleContainer>();

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		OutputHandler.SOP("If you would like to disable a module, please look in ForgeEssentials/main.cfg.");

		// started ASM handling for the module loaidng.
		Set<ASMData> data = e.getAsmData().getAll(FEModule.class.getName());

		ModuleContainer temp, other;
		for (ASMData asm : data)
		{
			temp = new ModuleContainer(asm);
			if (temp.isValid)
			{
				if (containerMap.containsKey(temp.name))
				{
					other = containerMap.get(temp.name);
					if (temp.doesOverride && other.mod == ForgeEssentials.instance)
						containerMap.put(temp.name, temp);
					else if (temp.mod == ForgeEssentials.instance && other.doesOverride)
						continue;
					else
						throw new RuntimeException("{FE-Module-Launcher} "+temp.name+" is conflicting with "+other.name);
				}
				else
					containerMap.put(temp.name, temp);
				
				temp.createAndPopulate();
				OutputHandler.SOP("Loaded " + temp.name);
			}
		}

		Collection<ModuleContainer> modules = (Collection<ModuleContainer>) containerMap.values();

		// run the preinits.
		for (ModuleContainer module : modules)
		{
			module.runPreInit(e);
		}

		// run the config init methods..
		boolean generate = false;
		for (ModuleContainer module : modules)
		{
			ModuleConfigBase cfg = module.getConfig();

			if (cfg != null)
			{
				File file = cfg.getFile();

				if (!file.getParentFile().exists())
				{
					generate = true;
					file.getParentFile().mkdirs();
				}

				if (!generate && (!file.exists() || !file.isFile()))
				{
					generate = true;
				}

				cfg.setGenerate(generate);
				cfg.init();
			}
		}
	}

	public void load(FMLInitializationEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runInit(e);
		}
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runPostInit(e);
		}
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerInit(e);
		}
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerPostInit(e);
		}
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerStop(e);
		}
	}

	public void reloadConfigs(ICommandSender sender)
	{
		ModuleConfigBase config;
		for (ModuleContainer module : containerMap.values())
		{
			config = module.getConfig();
			if (config != null)
				config.forceLoad(sender);
			module.runReload(sender);
		}
	}
}
