package com.ForgeEssentials.protection;

import java.util.HashMap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.*;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * @author Dries007
 */
@FEModule(name = "protection", parentMod = ForgeEssentials.class, isCore = true)
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
		HashMap<RegGroup, Boolean> map = new HashMap<RegGroup, Boolean>();
		map.put(RegGroup.GUESTS, false);
		map.put(RegGroup.MEMBERS, true);
		map.put(RegGroup.ZONE_ADMINS, true);
		map.put(RegGroup.OWNERS, true);
		permissions.put(PERM_EDITS, map);
		permissions.put(PERM_INTERACT_BLOCK, map);
		permissions.put(PERM_INTERACT_ENTITY, map);

		map.put(RegGroup.MEMBERS, false);
		permissions.put(PERM_OVERRIDE, map);
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
		for (String perm : permissions.keySet())
		{
			event.registerPerm(this, RegGroup.GUESTS, perm, permissions.get(perm).get(RegGroup.GUESTS));
			event.registerPerm(this, RegGroup.MEMBERS, perm, permissions.get(perm).get(RegGroup.MEMBERS));
			event.registerPerm(this, RegGroup.ZONE_ADMINS, perm, permissions.get(perm).get(RegGroup.ZONE_ADMINS));
			event.registerPerm(this, RegGroup.OWNERS, perm, permissions.get(perm).get(RegGroup.OWNERS));
		}
	}
}