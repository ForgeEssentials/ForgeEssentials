package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.autoPromote.AutoPromote;
import com.forgeessentials.permissions.autoPromote.AutoPromoteManager;
import com.forgeessentials.permissions.autoPromote.CommandAutoPromote;
import com.forgeessentials.permissions.commands.CommandFEPerm;
import com.forgeessentials.permissions.commands.CommandZone;
import com.forgeessentials.permissions.forge.ForgePermissionsHelper;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.events.modules.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.server.CommandHandlerForge;

import java.io.File;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions {
    public static SqlHelper sql;

    @FEModule.Config
    public static ConfigPermissions config;

    @FEModule.ModuleDir
    public static File permsFolder;

    protected static AbstractDataDriver data;
    private AutoPromoteManager autoPromoteManager;

    public static void regPerms()
    {
        APIRegistry.permReg.registerPermissionLevel("fe.perm", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.perm._ALL_", RegGroup.OWNERS, true);
        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone.define", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone.redefine._ALL_", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone.remove._ALL_", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel(TeleportCenter.BYPASS_COOLDOWN, RegGroup.OWNERS);

        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone", RegGroup.ZONE_ADMINS);
        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone.setparent", RegGroup.ZONE_ADMINS);
        APIRegistry.permReg.registerPermissionLevel("fe.perm.autoPromote", RegGroup.ZONE_ADMINS);

        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone.info._ALL_", RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.perm.zone.list", RegGroup.MEMBERS);

        APIRegistry.permReg.registerPermissionLevel("fe.perm.list", RegGroup.GUESTS);

        CommandSetChecker.regMCOverrides();
        APIRegistry.permReg.registerPermissionLevel("fe.core.info", RegGroup.OWNERS);

    }

    @FEModule.PreInit
    public void preLoad(FEModulePreInitEvent e)
    {
        APIRegistry.zones = new ZoneHelper();
        APIRegistry.perms = new PermissionsHelper();// new one for new API

        MinecraftForge.EVENT_BUS.register(APIRegistry.zones);
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.permReg = new PermRegLoader();

        DataStorageManager.registerSaveableType(new ClassContainer(Zone.class));

        PermissionsManager.setPermProvider(new ForgePermissionsHelper());
    }

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {
        // setup SQL
        sql = new SqlHelper(config);

        DataStorageManager.registerSaveableType(Zone.class);
        DataStorageManager.registerSaveableType(AutoPromote.class);

        MinecraftForge.EVENT_BUS.register(new PermsEventHandler());
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

        autoPromoteManager = new AutoPromoteManager();

        regPerms();

    }

    @FEModule.ServerPostInit
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        sql.putRegistrationPerms(APIRegistry.permReg.getRegisteredPerms());
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkCommandPerm(CommandEvent e)
    {
        if (!(e.sender instanceof EntityPlayer)) {return;}
        else if (!CommandHandlerForge.canUse(e.command, e.sender));
        e.setCanceled(true);
    }
}
