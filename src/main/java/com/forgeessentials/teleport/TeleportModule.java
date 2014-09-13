package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerPostInit;
import com.forgeessentials.teleport.util.ConfigTeleport;
import com.forgeessentials.teleport.util.TPAdata;
import com.forgeessentials.teleport.util.TeleportDataManager;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@FEModule(name = "TeleportModule", parentMod = ForgeEssentials.class, configClass = ConfigTeleport.class)
public class TeleportModule {

	public static int timeout;
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

	@Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
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
		APIRegistry.perms.registerPermissionProperty(CommandSetSpawn.SPAWN_TYPE_PROP, "bed"); // bed,
		// point,
		// none
		PermissionsManager.registerPermission("fe.teleport.back.ondeath", RegisteredPermValue.TRUE);
		PermissionsManager.registerPermission("fe.teleport.back.ontp", RegisteredPermValue.TRUE);
		PermissionsManager.registerPermission("fe.teleport.bed.others", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.teleport.home.set", RegisteredPermValue.TRUE);
		PermissionsManager.registerPermission("fe.teleport.spawn.others", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.teleport.top.others", RegisteredPermValue.OP);
		PermissionsManager.registerPermission("fe.teleport.tpa.sendrequest", RegisteredPermValue.TRUE);
		PermissionsManager.registerPermission("fe.teleport.tpahere.sendrequest", RegisteredPermValue.TRUE);
		PermissionsManager.registerPermission("fe.teleport.warp.admin", RegisteredPermValue.OP);

		for (ForgeEssentialsCommandBase cmd : commands)
		{
			PermissionsManager.registerPermission(cmd.getPermissionNode(), cmd.getDefaultPermission());
		}

	}

	@ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		String prop = APIRegistry.perms.getServerZone().getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, CommandSetSpawn.SPAWN_PROP);

		// nothing set for the global??
		if (prop == null)
		{
			ChunkCoordinates point = FunctionHelper.getDimension(0).provider.getSpawnPoint();
			String val = "0;" + point.posX + ";" + point.posY + ";" + point.posZ;
			APIRegistry.perms.setGroupPermissionProperty(APIRegistry.perms.GROUP_DEFAULT, CommandSetSpawn.SPAWN_PROP, val);
		}
		TeleportDataManager.load();

	}

	@FEModule.ServerStop
	public void serverStop(FEModuleServerStopEvent e)
	{
		TeleportDataManager.save();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		if (e.entity instanceof EntityPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
			PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
			CommandBack.justDied.add(player.getPersistentID());

			// generate for un-generated dimension
			{
				int currentDim = player.worldObj.provider.dimensionId;
				int spawnDim = player.worldObj.provider.getRespawnDimension(player);

				if (spawnDim != 0 && spawnDim == currentDim && !CommandSetSpawn.dimsWithProp.contains(currentDim))
				{
					Zone z = APIRegistry.perms.getWorldZone(player.worldObj);
					ChunkCoordinates dimPoint = player.worldObj.getSpawnPoint();
					WorldPoint point = new WorldPoint(spawnDim, dimPoint.posX, dimPoint.posY, dimPoint.posZ);
					CommandSetSpawn.setSpawnPoint(point, z);
					CommandSetSpawn.dimsWithProp.add(currentDim);

					WarpPoint p = new WarpPoint(currentDim, dimPoint.posX + .5, dimPoint.posY + 1, dimPoint.posZ + .5, player.cameraYaw, player.cameraPitch);
					CommandSetSpawn.spawns.put(player.getPersistentID(), p);
					return;
				}
			}

			String prop = APIRegistry.perms.getPermissionProperty(player, "fe.teleport.spawnType");
			if (prop != null && prop.equalsIgnoreCase("none"))
			{
				return;
			}
			else if (prop != null && prop.equalsIgnoreCase("bed"))
			{
				if (player.getBedLocation() != null)
				{
					ChunkCoordinates spawn = player.getBedLocation();
					EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);

					WarpPoint point = new WarpPoint(player.worldObj.provider.dimensionId, spawn.posX + .5, spawn.posY + 1, spawn.posZ + .5, player.cameraYaw,
							player.cameraPitch);
					CommandSetSpawn.spawns.put(player.getPersistentID(), point);

					return;
				}
			}

			prop = APIRegistry.perms.getPermissionProperty(player, "fe.teleport.spawnPoint");
			if (prop == null)
			{
				throw new RuntimeException("NO GLOBAL SPAWN SET!!!");
			}

			String[] split = prop.split("[;_]");
			try
			{
				int dim = Integer.parseInt(split[0]);
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);

				WarpPoint point = new WarpPoint(dim, x + .5, y + 1, z + .5, player.cameraYaw, player.cameraPitch);
				CommandSetSpawn.spawns.put(player.getPersistentID(), point);
			}
			catch (NumberFormatException exception)
			{
				CommandSetSpawn.spawns.put(player.getPersistentID(), null);
			}
		}
	}

	@SubscribeEvent
	public void doRespawn(PlayerEvent.PlayerRespawnEvent e)
	{
		// send to spawn point
		WarpPoint p = CommandSetSpawn.spawns.get(e.player.getPersistentID());
		if (p != null)
		{
			FunctionHelper.setPlayer((EntityPlayerMP) e.player, p);
			e.player.posX = p.xd;
			e.player.posY = p.yd;
			e.player.posZ = p.zd;
		}
		else
		{

		}
	}

	@SubscribeEvent
	public void serverTick(TickEvent.ServerTickEvent e)
	{
		handleTick();
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent e)
	{
		handleTick();
	}

	private void handleTick()
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
