package com.ForgeEssentials.permission;

import com.ForgeEssentials.util.OutputHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cpw.mods.fml.common.Mod;

import net.minecraftforge.event.Event;

public class PermissionRegistrationEvent extends Event
{
	protected HashSet<String> mods = new HashSet<String>();
	
	/**
	 * This is to define the level the permission should be used for by defualt..
	 * see @see com.ForgeEssentials.permissions.PermissionsAPI for the default groups
	 * If you want.. you can also set specific group permissions with this.. though they may or may not exist...
	 * @param level to apply permission to.
	 * @param permission Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow or deny.  If unset, all permissions default to deny. See the wiki for more info.
	 */
	public void registerPerm(Object mod, RegGroup group, String permission, boolean allow)
	{
		handleMod(mod);

//		Permission perm = new Permission(permission, allow);
//		Set<Permission> perms = ZoneManager.GLOBAL.groupOverrides.get(group);
//
//		if (perms == null)
//		{
//			perms = Collections.newSetFromMap(new ConcurrentHashMap<Permission, Boolean>());
//			perms.add(perm);
//			ZoneManager.GLOBAL.groupOverrides.put(group, perms);
//		}
//		else
//		{
//			PermissionChecker checker = new PermissionChecker(permission);
//			if (perms.contains(checker))
//				perms.remove(checker);
//			perms.add(perm);
//		}
		
		// store defaults... for later...
	}
	
	private void handleMod(Object mod)
	{
		Class c = mod.getClass();
		assert c.isAnnotationPresent(Mod.class) : new IllegalArgumentException("Don't trick me! THIS! > "+mod+" < ISNT A MOD!");
		
		Mod info = (Mod) c.getAnnotation(Mod.class);
		String modid = info.modid();
		
		if (mods.add(modid))
			OutputHandler.SOP("[PermReg] "+modid+" has registerred permissions.");
	}
}
