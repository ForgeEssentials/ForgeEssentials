package com.ForgeEssentials.core;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;

public class SanityChecker
{
	public void run(){
		
		
		
		if (ForgeEssentials.bukkitcheck = false){
			OutputHandler.severe("Sanity checking has been disabled. Do not bug the FE team with issues running FE on a bukkit server, or with any other server mod.");
		}
		
		else{
		// Check for BukkitForge
		if (Loader.isModLoaded("BukkitForge")){
			OutputHandler.severe("Sanity check failed: Detected BukkitForge, stopping server for your safety.");
			throw new RuntimeException("ForgeEssentials: Please do not use FE with BukkitForge, bad things may happen to your server. You were warned.");
		}
		
		// Check for Fihgu's mods
		else if (Loader.isModLoaded("fihgu's Core Mod")){
			OutputHandler.severe("Sanity check failed: Detected Fihgu's mods, stopping server for your safety.");
			throw new RuntimeException("ForgeEssentials: Please DO NOT use FE with Fihgu's mods, bad things WILL happen. Trust us. You were warned,");
		}
		
		else{
			// Check for MCPC+ or LavaBukkit
			try{
				Class.forName("org.bukkit.craftbukkit.Main", false, getClass().getClassLoader());
				OutputHandler.severe("Sanity check failed: Detected a ForgeBukkit server implementation, stopping server for your safety.");
				throw new RuntimeException("ForgeEssentials: Please do not use FE with any ForgeBukkit server implementation, bad things may happen to your server. You were warned.");
			}catch (ClassNotFoundException e){
			// Safe!
			}
		}
		}
		OutputHandler.info("Sanity check passed: No Bukkit server implementations found, starting server.");
	}

}
