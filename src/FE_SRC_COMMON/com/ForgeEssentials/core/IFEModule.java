package com.ForgeEssentials.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public interface IFEModule
{
	/**
	 * After this point, the config should have been initialized and saved for the getConfig method.
	 */
	public void preLoad(FMLPreInitializationEvent e);

	public void load(FMLInitializationEvent e);

	public void postLoad(FMLPostInitializationEvent e);

	public void serverStarting(FMLServerStartingEvent e);

	public void serverStarted(FMLServerStartedEvent e);

	public void serverStopping(FMLServerStoppingEvent e);

	public IModuleConfig getConfig();
}
