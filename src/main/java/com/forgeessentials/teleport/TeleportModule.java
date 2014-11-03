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

@FEModule(name = "TeleportModule", parentMod = ForgeEssentials.class)
public class TeleportModule {

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

        APIRegistry.perms.registerPermission("fe.teleport.back.ondeath", RegisteredPermValue.TRUE,
                "Allow returning to the last death location with back-command");
        APIRegistry.perms.registerPermission("fe.teleport.back.ontp", RegisteredPermValue.TRUE,
                "Allow returning to the last location before teleport with back-command");
        APIRegistry.perms.registerPermission("fe.teleport.bed.others", RegisteredPermValue.OP, "Allow teleporting to other player's bed location");
        APIRegistry.perms.registerPermission("fe.teleport.home.set", RegisteredPermValue.TRUE, "Allow setting of home location");
        APIRegistry.perms.registerPermission("fe.teleport.spawn.others", RegisteredPermValue.OP, "Allow setting other player's spawn");
        APIRegistry.perms.registerPermission("fe.teleport.top.others", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission("fe.teleport.tpa.sendrequest", RegisteredPermValue.TRUE, "Allow sending teleport-to requests");
        APIRegistry.perms.registerPermission("fe.teleport.tpahere.sendrequest", RegisteredPermValue.TRUE, "Allow sending teleport-here requests");
        APIRegistry.perms.registerPermission("fe.teleport.warp.admin", RegisteredPermValue.OP);

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
