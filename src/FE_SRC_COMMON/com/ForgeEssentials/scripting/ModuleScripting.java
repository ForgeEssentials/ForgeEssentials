package com.ForgeEssentials.scripting;

import java.io.File;
import java.io.PrintWriter;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;

import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "Scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting
{

	@FEModule.PreInit
	public void preInit(FEModulePreInitEvent e)
	{
		startup();
		GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
	}

	public static void startup()
	{
		try
		{
			if (!ScriptPlayerTracker.scriptfolder.exists())
			{
				ScriptPlayerTracker.scriptfolder.mkdirs();
				//Since it is safe to assume the subfolders don't exist
				ScriptPlayerTracker.loginplayer.mkdirs();
				ScriptPlayerTracker.logingroup.mkdirs();
				ScriptPlayerTracker.respawngroup.mkdirs();
				ScriptPlayerTracker.respawnplayer.mkdirs();
			}
			if (!ScriptPlayerTracker.loginplayer.exists())
			{
				ScriptPlayerTracker.loginplayer.mkdirs();
			}
			if (!ScriptPlayerTracker.logingroup.exists())
			{
				ScriptPlayerTracker.logingroup.mkdirs();
			}
			if (!ScriptPlayerTracker.respawngroup.exists())
			{
				ScriptPlayerTracker.respawngroup.mkdirs();
			}
			if (!ScriptPlayerTracker.respawnplayer.exists())
			{
				ScriptPlayerTracker.respawnplayer.mkdirs();
			}
		}
		catch (Exception e)
		{
			OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
		}
	}

}
