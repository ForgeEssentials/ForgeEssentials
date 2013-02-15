package com.ForgeEssentials.core;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;

public class BukkitChecker
{
	public BukkitChecker(){
		// Check for BukkitForge
		if (Loader.isModLoaded("BukkitForge")){
			OutputHandler.severe("Detected BukkitForge, stopping server for your safety.");
			throw new RuntimeException("ForgeEssentials: Please do not use FE with BukkitForge, bad things may happen to your server. You were warned.");
		}
		else{
			// Check for MCPC+ or LavaBukkit
			try{
				Class.forName("org.bukkit.craftbukkit.CraftServer", false, getClass().getClassLoader());
				OutputHandler.severe("Detected a ForgeBukkit server implementation, stopping server for your safety.");
				throw new RuntimeException("ForgeEssentials: Please do not use FE with any ForgeBukkit server implementation, bad things may happen to your server. You were warned.");
			}catch (ClassNotFoundException e){
				OutputHandler.info("BukkitForge or MCPC+ have not been found, server startup is allowed.");
			}
		}
	}

}
