package com.ForgeEssentials.skcompat;

import com.sk89q.worldedit.WorldEdit;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class Modulesk89qCompat implements IFEModule
{
	public WorldEdit	we;

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Starting WorldEdit/WorldGuard integration module...");
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		LibraryDetector.detect();
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

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		// TODO Auto-generated method stub
		
	}

}
