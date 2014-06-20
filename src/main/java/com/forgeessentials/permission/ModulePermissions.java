package com.forgeessentials.permission;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.DuplicateCommandRemoval;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.network.FEServerPacketHandler;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permission.autoPromote.AutoPromote;
import com.forgeessentials.permission.autoPromote.AutoPromoteManager;
import com.forgeessentials.permission.autoPromote.CommandAutoPromote;
import com.forgeessentials.permission.commands.CommandFEPerm;
import com.forgeessentials.permission.commands.CommandZone;
import com.forgeessentials.permission.network.PacketPermNodeList;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.events.modules.*;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@FEModule(name = "Permissions", parentMod = ForgeEssentials.class, configClass = ConfigPermissions.class)
public class ModulePermissions {
    public static SqlHelper sql;

    @FEModule.Config
    public static ConfigPermissions config;

    @FEModule.ModuleDir
    public static File permsFolder;

    protected static AbstractDataDriver data;
    private static Map<String, RegGroup> permList = new HashMap<String, RegGroup>();
    // permission registrations here...
    protected PermRegLoader permLoader;
    private AutoPromoteManager autoPromoteManager;

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

        event.registerPermissionLevel("fe.perm.list", RegGroup.GUESTS);

        Iterator it = permList.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry) it.next();
            event.registerPermissionLevel((String) pairs.getKey(), (RegGroup) pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    @FEModule.PreInit
    public void preLoad(FEModulePreInitEvent e)
    {
        APIRegistry.zones = new ZoneHelper();
        APIRegistry.perms = new PermissionsHelper();// new one for new API

        MinecraftForge.EVENT_BUS.register(APIRegistry.zones);
        MinecraftForge.EVENT_BUS.register(this);
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

        FEServerPacketHandler.registerPacket(3, PacketPermNodeList.class);
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

        doMCOverrides();

    }

    private void doMCOverrides()
    {
        Set<ICommand> cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) MinecraftServer.getServer().getCommandManager(),
                DuplicateCommandRemoval.FIELDNAME);

        for (ICommand cmd : cmdList)
        {
            if (cmd.getClass().getCanonicalName().startsWith("net.minecraft.command"))
            {
                permList.put("mc." + cmd.getCommandName(), RegGroup.fromInt(((CommandBase) cmd).getRequiredPermissionLevel()));
            }

        }
    }

    @FEModule.ServerPostInit
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        permLoader.loadAllPerms();
        permLoader.clearMethods();

        sql.putRegistrationPerms(permLoader.registerredPerms);
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

    public void sendPermList(Player player)
    {
        PacketDispatcher.sendPacketToPlayer(new PacketPermNodeList(permLoader.perms).getPayload(), player);
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void checkCommandPerm(CommandEvent e)
    {
        if (!(e.sender instanceof EntityPlayer)) {return;}
        ICommand command = e.command;
        if (e.command.getClass().getCanonicalName().startsWith("net.minecraft.command"))
        {
            boolean allow = APIRegistry.perms.checkPermAllowed((EntityPlayer) e.sender, "mc." + e.command.getCommandName());
            if (!allow)
            {
                e.setCanceled(true);
            }

        }
    }
}
