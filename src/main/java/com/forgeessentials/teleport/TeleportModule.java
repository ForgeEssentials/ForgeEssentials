package com.forgeessentials.teleport;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.query.PropQueryBlanketZone;
import com.forgeessentials.api.permissions.query.PropQueryPlayerSpot;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerPostInit;
import com.forgeessentials.teleport.util.ConfigTeleport;
import com.forgeessentials.teleport.util.PlayerTrackerTP;
import com.forgeessentials.teleport.util.TeleportDataManager;
import com.forgeessentials.teleport.util.TickHandlerTP;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.AreaSelector.WorldPoint;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;

@FEModule(name = "TeleportModule", parentMod = ForgeEssentials.class, configClass = ConfigTeleport.class)
public class TeleportModule {

    public static int timeout;

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

    @Init
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        for (ForgeEssentialsCommandBase cmd : commands)
        {
            e.registerServerCommand(cmd);
        }

        // ensures
        // on
        // ServerStart
        // event.registerPermissionProp("fe.teleport.spawnPoint",
        // "0;0;0;0");
        APIRegistry.permReg.registerPermissionProp(CommandSetSpawn.SPAWN_TYPE_PROP, "bed"); // bed,
        // point,
        // none
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.back.ondeath",
                RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.back.ontp", RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.bed.others", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.home.set", RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.spawn.others",
                RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.top.others", RegGroup.OWNERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.tpa.sendrequest",
                RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.tpahere.sendrequest",
                RegGroup.MEMBERS);
        APIRegistry.permReg.registerPermissionLevel("fe.teleport.warp.admin", RegGroup.OWNERS);

        for (ForgeEssentialsCommandBase cmd : commands)
        {
            APIRegistry.permReg.registerPermissionLevel(cmd.getCommandPerm(), cmd.getReggroup());
        }

    }

    @ServerPostInit
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        PropQueryBlanketZone query = new PropQueryBlanketZone(
                CommandSetSpawn.SPAWN_PROP, APIRegistry.zones.getGLOBAL(),
                false);
        APIRegistry.perms.getPermissionProp(query);

        // nothing set for the global??
        if (!query.hasValue())
        {
            ChunkCoordinates point = FunctionHelper.getDimension(0).provider
                    .getSpawnPoint();
            String val = "0;" + point.posX + ";" + point.posY + ";"
                    + point.posZ;
            APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms
                            .getDEFAULT().name, CommandSetSpawn.SPAWN_PROP, val,
                    APIRegistry.zones.getGLOBAL().getZoneName());
        }

        GameRegistry.registerPlayerTracker(new PlayerTrackerTP());
        TickRegistry.registerScheduledTickHandler(new TickHandlerTP(),
                Side.SERVER);
        TeleportDataManager.load();
    }

    @FEModule.ServerStop
    public void serverStop(FEModuleServerStopEvent e){ TeleportDataManager.save();}

    @ForgeSubscribe(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        if (e.entity instanceof EntityPlayer)
        {
            EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
            PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(
                    player);
            CommandBack.justDied.add(player.username);

            // generate for un-generated dimension
            {
                int currentDim = player.worldObj.provider.dimensionId;
                int spawnDim = player.worldObj.provider
                        .getRespawnDimension(player);

                if (spawnDim != 0 && spawnDim == currentDim
                        && !CommandSetSpawn.dimsWithProp.contains(currentDim))
                {
                    Zone z = APIRegistry.zones.getWorldZone(player.worldObj);
                    ChunkCoordinates dimPoint = player.worldObj.getSpawnPoint();
                    WorldPoint point = new WorldPoint(spawnDim, dimPoint.posX,
                            dimPoint.posY, dimPoint.posZ);
                    CommandSetSpawn.setSpawnPoint(point, z);
                    CommandSetSpawn.dimsWithProp.add(currentDim);

                    WarpPoint p = new WarpPoint(currentDim, dimPoint.posX + .5,
                            dimPoint.posY + 1, dimPoint.posZ + .5,
                            player.cameraYaw, player.cameraPitch);
                    CommandSetSpawn.spawns.put(player.username, p);
                    return;
                }
            }

            PropQueryPlayerSpot query = new PropQueryPlayerSpot(player,
                    "fe.teleport.spawnType");
            APIRegistry.perms.getPermissionProp(query);

            if (query.getStringValue().equalsIgnoreCase("none"))
            {
                return;
            }
            else if (query.getStringValue().equalsIgnoreCase("bed"))
            {
                if (player.getBedLocation() != null)
                {
                    ChunkCoordinates spawn = player.getBedLocation();
                    EntityPlayer.verifyRespawnCoordinates(player.worldObj,
                            spawn, true);

                    WarpPoint point = new WarpPoint(
                            player.worldObj.provider.dimensionId,
                            spawn.posX + .5, spawn.posY + 1, spawn.posZ + .5,
                            player.cameraYaw, player.cameraPitch);
                    CommandSetSpawn.spawns.put(player.username, point);

                    return;
                }
            }

            query = new PropQueryPlayerSpot(player, "fe.teleport.spawnPoint");
            APIRegistry.perms.getPermissionProp(query);

            if (!query.hasValue())
            {
                throw new RuntimeException("NO GLOBAL SPAWN SET!!!");
            }

            String val = query.getStringValue();
            String[] split = val.split("[;_]");

            try
            {
                int dim = Integer.parseInt(split[0]);
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);

                WarpPoint point = new WarpPoint(dim, x + .5, y + 1, z + .5,
                        player.cameraYaw, player.cameraPitch);
                CommandSetSpawn.spawns.put(player.username, point);
            }
            catch (Exception exception)
            {
                CommandSetSpawn.spawns.put(player.username, null);
            }
        }
    }

}
