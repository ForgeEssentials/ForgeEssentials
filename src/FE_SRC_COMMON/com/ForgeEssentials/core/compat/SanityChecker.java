package com.ForgeEssentials.core.compat;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;

public class SanityChecker
{
	public void run()
	{
		if (!ForgeEssentials.sanitycheck)
		{
			OutputHandler.felog.severe("Sanity checking has been disabled. Do not bug the FE team with issues running FE on a bukkit server, or with any other server mod.");
			return;
		}

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
				Class.forName("org.bukkit.craftbukkit.Main", false, getClass().getClassLoader());
				OutputHandler.felog.severe("Sanity check failed: Detected a ForgeBukkit server implementation, bad things may happen, proceed at your own risk.");
			}
			catch (ClassNotFoundException e)
			{
				// Safe!
			}
		}
		OutputHandler.felog.fine("Sanity check passed, it's all good to go!");

	}

}
