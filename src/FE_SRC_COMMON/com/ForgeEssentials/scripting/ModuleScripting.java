package com.ForgeEssentials.scripting;

import java.io.File;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ModuleDir;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting
{

	@ModuleDir
	public static File	moduleDir;

	@FEModule.PreInit
	public void preInit(FEModulePreInitEvent e)
	{
		startup();
		GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandScript());
		e.registerServerCommand(new CommandGetScriptBook());
		e.registerServerCommand(new CommandScriptBook());
	}

	public static void startup()
	{
		try
		{
			if (!ScriptPlayerTracker.scriptfolder.exists())
			{
				ScriptPlayerTracker.scriptfolder.mkdirs();
				// Since it is safe to assume the sub-folders don't exist.
				ScriptPlayerTracker.loginplayer.mkdirs();
				ScriptPlayerTracker.logingroup.mkdirs();
				ScriptPlayerTracker.respawngroup.mkdirs();
				ScriptPlayerTracker.respawnplayer.mkdirs();
				CommandScript.commandscripts.mkdirs();
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
			if (!CommandScript.commandscripts.exists())
			{
				CommandScript.commandscripts.mkdirs();
			}
		}
		catch (Exception e)
		{
			OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
		}
	}

}
