package com.ForgeEssentials.util.events.modules;

import java.util.logging.Logger;

import com.ForgeEssentials.core.moduleLauncher.CallableMap;
import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModulePreInitEvent extends FEModuleEvent
{
	private FMLPreInitializationEvent	event;
	private CallableMap					callables;

	public FEModulePreInitEvent(ModuleContainer container, FMLPreInitializationEvent event, CallableMap map)
	{
		super(container);
		this.event = event;
		callables = map;
	}

	/**
	 * Get a logger instance configured to write to the FE Log as a parent,
	 * identified by ModuleName. Handy for module logging!
	 * @return A logger
	 */
	public Logger getModLog()
	{
		Logger log = Logger.getLogger(container.name);
		log.setParent(OutputHandler.felog);
		return log;
	}

	@Override
	public FMLStateEvent getFMLEvent()
	{
		return event;
	}

	public CallableMap getCallableMap()
	{
		return callables;
	}
}
