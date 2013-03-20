package com.ForgeEssentials.protection;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author Dries007
 */
@FEModule(name = "protection", parentMod = ForgeEssentials.class, isCore = true, configClass = ConfigProtection.class)
public class ModuleProtection
{
	public final static String									PERM_EDITS				= "ForgeEssentials.Protection.allowEdits";
	public final static String									PERM_INTERACT_BLOCK		= "ForgeEssentials.Protection.allowBlockInteractions";
	public final static String									PERM_INTERACT_ENTITY	= "ForgeEssentials.Protection.allowEntityInteractions";
	public final static String									PERM_OVERRIDE			= "ForgeEssentials.Protection.overrideProtection";
	public final static String									PERM_PVP				= "ForgeEssentials.Protection.pvp";
	public final static String									PERM_MOB_SPAWN_NATURAL	= "ForgeEssentials.Protection.mobSpawn.natural";
	public final static String									PERM_MOB_SPAWN_FORCED	= "ForgeEssentials.Protection.mobSpawn.forced";

	@FEModule.Config
	public static ConfigProtection								config;
	public static boolean										enable					= false;
	public static boolean										enableMobSpawns			= false;

	public static HashMap<String, HashMap<RegGroup, Boolean>>	permissions				= new HashMap<String, HashMap<RegGroup, Boolean>>();

	/*
	 * Module part
	 */

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;
		if (!enable)
			return;
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		if (!enable)
			return;
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@SuppressWarnings("unchecked")
	@PermRegister
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(PERM_PVP, RegGroup.GUESTS);
		event.registerPermissionLevel(PERM_EDITS, RegGroup.MEMBERS);
		event.registerPermissionLevel(PERM_INTERACT_BLOCK, RegGroup.MEMBERS);
		event.registerPermissionLevel(PERM_INTERACT_ENTITY, RegGroup.MEMBERS);
		event.registerPermissionLevel(PERM_OVERRIDE, RegGroup.OWNERS);

		for (Entry<String, Class<?>> e : (Set<Entry<String, Class<?>>>) EntityList.stringToClassMapping.entrySet())
		{
			if (EntityLiving.class.isAssignableFrom(e.getValue()))
			{
				event.registerPermissionLevel(PERM_MOB_SPAWN_NATURAL + "." + e.getKey(), RegGroup.ZONE);
				event.registerPermissionLevel(PERM_MOB_SPAWN_FORCED + "." + e.getKey(), RegGroup.ZONE);
			}
		}
	}
}
