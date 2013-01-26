package com.ForgeEssentials.protection;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.util.HashMap;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author Dries007
 */
@FEModule(name = "protection", parentMod = ForgeEssentials.class, isCore = true, configClass=ConfigProtection.class)
public class ModuleProtection
{
	public final static String PERM_EDITS = "ForgeEssentials.Protection.allowEdits";
	public final static String PERM_INTERACT_BLOCK = "ForgeEssentials.Protection.allowBlockInteractions";
	public final static String PERM_INTERACT_ENTITY = "ForgeEssentials.Protection.allowEntityInteractions";
	public final static String PERM_OVERRIDE = "ForgeEssentials.Protection.overrideProtection";

	@Config
	public static ConfigProtection config;
	public static boolean enable = false;

	public static HashMap<String, HashMap<RegGroup, Boolean>> permissions = new HashMap<String, HashMap<RegGroup, Boolean>>();

	public ModuleProtection()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	/*
	 * Module part
	 */

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return;
		}
		if (!enable)
		{
			return;
		}
		OutputHandler.SOP("Protection module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		if (!enable)
		{
			return;
		}
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		// event.registerPermissionDefault(PERM, false);
		event.registerPerm(this, RegGroup.MEMBERS, PERM_EDITS, true);
		event.registerPerm(this, RegGroup.MEMBERS, PERM_INTERACT_BLOCK, true);
		event.registerPerm(this, RegGroup.MEMBERS, PERM_INTERACT_ENTITY, true);
		event.registerPerm(this, RegGroup.OWNERS, PERM_OVERRIDE, true);
	}
}