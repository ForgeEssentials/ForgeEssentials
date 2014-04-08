package com.forgeessentials.core.compat;

import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;

public class EnvironmentChecker
{
	public static boolean worldEditInstalled = checkWorldEdit();
	
	public static void checkBukkit()
	{

		// Check for BukkitForge
		if (Loader.isModLoaded("BukkitForge"))
		{
			OutputHandler.felog.severe("Sanity check failed: Detected BukkitForge, bad things may happen, proceed at your own risk.");
		}

		// Check for Fihgu's mods
		else if (Loader.isModLoaded("fihgu's Core Mod"))
		{
			OutputHandler.felog.severe("Sanity check failed: Detected Fihgu's mods, bad things may happen, proceed at your own risk.");
			
		}

		else
		{
			// Check for MCPC+ or LavaBukkit
			try
			{
				Class.forName("org.bukkit.craftbukkit.Main");
				OutputHandler.felog.severe("Sanity check failed: Detected a ForgeBukkit server implementation, bad things may happen, proceed at your own risk.");
			}
			catch (ClassNotFoundException e)
			{
				// Safe!
			}
		}
		OutputHandler.felog.fine("Sanity check passed, it's all good to go!");

	}
	
	public static boolean checkWorldEdit(){
		if (!Loader.isModLoaded("WorldEdit")){
			OutputHandler.felog.info("WorldEdit Forge not found, continuing as per normal.");
			return false;
		}
		else try{
			Class.forName("com.forgeessentials.worldedit.compat.WorldEditIntegration");
			OutputHandler.felog.info("Found WorldEdit Forge and FE integration tools, using FE integration tools.");
			OutputHandler.felog.info("FEClient graphical selections have been disabled, please use WorldEditCUI.");
			return true;
		}catch (ClassNotFoundException cnfe){
			
		}OutputHandler.felog.warning("WorldEdit Forge found but FE integration tools not found.");
		OutputHandler.felog.warning("You are strongly recommended to install the FE integration tools for a better experience.");
		return true;
	}

}
