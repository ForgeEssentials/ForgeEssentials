package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.teleport.util.TPAdata;
import com.forgeessentials.teleport.util.TeleportDataManager;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@FEModule(name = "Teleport", parentMod = ForgeEssentials.class)
public class TeleportModule {

    public static final String PERM_TP = "fe.teleport.tp";
    public static final String PERM_TP_OTHERS = "fe.teleport.tp.others";

    public static final String PERM_TPPOS = "fe.teleport.tppos";

    public static final String PERM_TPA = "fe.teleport.tpa";
    public static final String PERM_TPA_SENDREQUEST = "fe.teleport.tpa.sendrequest";
    public static final String PERM_TPA_TIMEOUT = "fe.teleport.tpa.timeout";

    public static final String PERM_TOP = "fe.teleport.top";
    public static final String PERM_TOP_OTHERS = "fe.teleport.top.others";

    public static final String PERM_SPAWN = "fe.teleport.spawn";
    public static final String PERM_SPAWN_OTHERS = "fe.teleport.spawn.others";

    public static final String PERM_HOME = "fe.teleport.home.set";
    public static final String PERM_HOME_SET = "fe.teleport.home.set";
    
    public static final String PERM_BED = "fe.teleport.bed";
    public static final String PERM_BED_OTHERS = "fe.teleport.bed.others";

    public static final String PERM_BACK = "fe.teleport.back";
    public static final String PERM_BACK_ONTP = "fe.teleport.back.ontp";
    public static final String PERM_BACK_ONDEATH = "fe.teleport.back.ondeath";

    public static final String PERM_TPAHERE = "fe.teleport.tpahere";
    public static final String PERM_TPAHERE_SENDREQUEST = "fe.teleport.tpahere.sendrequest";

    public static final String PERM_WARP = "fe.teleport.warp";
    public static final String PERM_WARP_ADMIN = "fe.teleport.warp.admin";
    
    public static List<TPAdata> tpaList = new ArrayList<TPAdata>();
    public static List<TPAdata> tpaListToAdd = new ArrayList<TPAdata>();
    public static List<TPAdata> tpaListToRemove = new ArrayList<TPAdata>();
    private static List<ForgeEssentialsCommandBase> commands = new ArrayList<ForgeEssentialsCommandBase>();

    static
    {
        commands.add(new CommandBack());
        commands.add(new CommandBed());
        commands.add(new CommandHome());
        commands.add(new CommandSpawn());
        commands.add(new CommandTp());
        commands.add(new CommandTphere());
        commands.add(new CommandTppos());
        commands.add(new CommandWarp());
        commands.add(new CommandSetSpawn());
        commands.add(new CommandTPA());
        commands.add(new CommandTPAhere());
        commands.add(new CommandPersonalWarp());
        commands.add(new CommandTop());
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        for (ForgeEssentialsCommandBase cmd : commands)
        {
            FunctionHelper.registerServerCommand(cmd);
        }

        APIRegistry.perms.registerPermissionProperty(PERM_TPA_TIMEOUT, "20", "Amount of sec a user has to accept a TPA request");

        APIRegistry.perms.registerPermission(PERM_BACK_ONDEATH, RegisteredPermValue.TRUE, "Allow returning to the last death location with back-command");
        APIRegistry.perms.registerPermission(PERM_BACK_ONTP, RegisteredPermValue.TRUE, "Allow returning to the last location before teleport with back-command");
        APIRegistry.perms.registerPermission(PERM_BED_OTHERS, RegisteredPermValue.OP, "Allow teleporting to other player's bed location");
        APIRegistry.perms.registerPermission(PERM_HOME_SET, RegisteredPermValue.TRUE, "Allow setting of home location");
        APIRegistry.perms.registerPermission(PERM_SPAWN_OTHERS, RegisteredPermValue.OP, "Allow setting other player's spawn");
        APIRegistry.perms.registerPermission(PERM_TOP_OTHERS, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_TPA_SENDREQUEST, RegisteredPermValue.TRUE, "Allow sending teleport-to requests");
        APIRegistry.perms.registerPermission(PERM_TPAHERE_SENDREQUEST, RegisteredPermValue.TRUE, "Allow sending teleport-here requests");
        APIRegistry.perms.registerPermission(PERM_WARP_ADMIN, RegisteredPermValue.OP);

        for (ForgeEssentialsCommandBase cmd : commands)
        {
            APIRegistry.perms.registerPermission(cmd.getPermissionNode(), cmd.getDefaultPermission(), "Command: " + cmd.getCommandUsage(null));
        }

    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        TeleportDataManager.load();
    }

    @SubscribeEvent
    public void serverStop(FEModuleServerStopEvent e)
    {
        TeleportDataManager.save();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            if (e.entityLiving instanceof EntityPlayer)
            {
                EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
                PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
                CommandBack.justDied.add(player.getPersistentID());
            }
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent e)
    {
        handleTick();
    }

    private static void handleTick()
    {
        try
        {
            tpaList.addAll(tpaListToAdd);
            tpaListToAdd.clear();
            for (TPAdata data : tpaList)
            {
                data.count();
            }
            tpaList.removeAll(tpaListToRemove);
            tpaListToRemove.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
