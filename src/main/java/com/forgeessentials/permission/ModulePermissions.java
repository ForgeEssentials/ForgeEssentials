package com.forgeessentials.permission;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permission.autoPromote.AutoPromote;
import com.forgeessentials.permission.autoPromote.AutoPromoteManager;
import com.forgeessentials.permission.autoPromote.CommandAutoPromote;
import com.forgeessentials.permission.mcoverride.OverrideManager;
import com.forgeessentials.permission.network.PacketPermNodeList;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePostInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions
{
	public static SqlHelper				sql;

	@FEModule.Config
	public static ConfigPermissions		config;

	@FEModule.ModuleDir
	public static File					permsFolder;

	protected static AbstractDataDriver	data;

	// permission registrations here...
	protected PermRegLoader				permLoader;
	private AutoPromoteManager			autoPromoteManager;

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		APIRegistry.zones = new ZoneHelper();
		APIRegistry.perms = new PermissionsHelper();// new one for new API

		MinecraftForge.EVENT_BUS.register(APIRegistry.zones);
		permLoader = new PermRegLoader(e.getCallableMap().getCallable(PermRegister.class));

		DataStorageManager.registerSaveableType(new ClassContainer(Zone.class));
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		// setup SQL
		sql = new SqlHelper(config);

		DataStorageManager.registerSaveableType(Zone.class);
		DataStorageManager.registerSaveableType(AutoPromote.class);

		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@FEModule.PostInit
	public void postload(FEModulePostInitEvent e)
	{
		permLoader.loadAllPerms();
		permLoader.clearMethods();
		sql.putRegistrationPerms(permLoader.registerredPerms);

		PermissionsList list = new PermissionsList();
		if (list.shouldMake())
		{
			list.output(permLoader.perms);
		}
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		// load zones...
		data = DataStorageManager.getReccomendedDriver();
		((ZoneHelper) APIRegistry.zones).loadZones();

		if (config.importBool)
		{
			sql.importPerms(config.importDir);
		}

		// init perms and vMC command overrides
		e.registerServerCommand(new CommandZone());
		e.registerServerCommand(new CommandFEPerm());
		e.registerServerCommand(new CommandAutoPromote());
		OverrideManager.regOverrides((FMLServerStartingEvent) e.getFMLEvent());

		autoPromoteManager = new AutoPromoteManager();
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("fe.perm", RegGroup.OWNERS);
		event.registerPermissionLevel("fe.perm._ALL_", RegGroup.OWNERS, true);
		event.registerPermissionLevel("fe.perm.zone.define", RegGroup.OWNERS);
		event.registerPermissionLevel("fe.perm.zone.redefine._ALL_", RegGroup.OWNERS);
		event.registerPermissionLevel("fe.perm.zone.remove._ALL_", RegGroup.OWNERS);
		event.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);
		event.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);

		event.registerPermissionLevel("fe.perm.zone", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("fe.perm.zone.setparent", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("fe.perm.autoPromote", RegGroup.ZONE_ADMINS);

		event.registerPermissionLevel("fe.perm.zone.info._ALL_", RegGroup.MEMBERS);
		event.registerPermissionLevel("fe.perm.zone.list", RegGroup.MEMBERS);

		event.registerPermissionLevel("ForgeEssentials.BasicCommands.list", RegGroup.GUESTS);
		
		// somehow the perms stuff doesn't read this where they were, try here
		event.registerPermissionLevel("mc.ban", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.ban-ip", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.debug", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.defaultgamemode", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.deop", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.difficulty", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.gamerule", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.kick", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.me", RegGroup.GUESTS);
		event.registerPermissionLevel("mc.op", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.pardon", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.pardon-ip", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.publish", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.save-all", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.save-on", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.save-off", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.say", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.seed", RegGroup.MEMBERS);
		event.registerPermissionLevel("mc.stop", RegGroup.GUESTS);
		event.registerPermissionLevel("mc.whitelist", RegGroup.OWNERS);
		event.registerPermissionLevel("mc.xp", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.toggledownfall", RegGroup.ZONE_ADMINS);
		event.registerPermissionLevel("mc.testfor", RegGroup.MEMBERS);
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		// save all the zones
		for (Zone zone : APIRegistry.zones.getZoneList())
		{
			if (zone == null || zone.isGlobalZone() || zone.isWorldZone())
			{
				continue;
			}
			data.saveObject(ZoneHelper.container, zone);
		}

		autoPromoteManager.stop();
	}
	public void sendPermList(Player player){
		PacketDispatcher.sendPacketToPlayer(new PacketPermNodeList(permLoader.perms).getPayload(), player);
	}
}
