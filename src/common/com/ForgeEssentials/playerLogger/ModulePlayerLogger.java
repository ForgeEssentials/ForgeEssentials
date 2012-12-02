package com.ForgeEssentials.playerLogger;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModulePlayerLogger implements IFEModule
{
	public ModulePlayerLogger()
	{
		if (!ModuleLauncher.loggerEnabled)
			return;
	}

	@Override
	public void preLoad(FMLPreInitializationEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(FMLInitializationEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

}
