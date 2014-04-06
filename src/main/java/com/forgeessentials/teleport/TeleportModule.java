package com.forgeessentials.teleport;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.query.PropQueryBlanketZone;
import com.forgeessentials.api.permissions.query.PropQueryPlayerSpot;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.teleport.util.TickHandlerTP;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.AreaSelector.WorldPoint;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerPostInitEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "TeleportModule", parentMod = ForgeEssentials.class)
public class TeleportModule {
	
	public static int timeout;
	
	@FEModule.Init
	public void load(FEModuleInitEvent e){
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@FEModule.ServerInit()
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandBack());
		e.registerServerCommand(new CommandBed());
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandSpawn());
		e.registerServerCommand(new CommandTp());
		e.registerServerCommand(new CommandTphere());
		e.registerServerCommand(new CommandTppos());
		e.registerServerCommand(new CommandWarp());
		e.registerServerCommand(new CommandSetSpawn());
		e.registerServerCommand(new CommandTPA());
		e.registerServerCommand(new CommandTPAhere());
		e.registerServerCommand(new CommandPersonalWarp());
		e.registerServerCommand(new CommandTop());
	}
	
	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		PropQueryBlanketZone query = new PropQueryBlanketZone(CommandSetSpawn.SPAWN_PROP, APIRegistry.zones.getGLOBAL(), false);
		APIRegistry.perms.getPermissionProp(query);

		// nothing set for the global??
		if (!query.hasValue())
		{
			ChunkCoordinates point = FunctionHelper.getDimension(0).provider.getSpawnPoint();
			String val = "0;" + point.posX + ";" + point.posY + ";" + point.posZ;
			APIRegistry.perms.setGroupPermissionProp(APIRegistry.perms.getDEFAULT().name, CommandSetSpawn.SPAWN_PROP, val, APIRegistry.zones.getGLOBAL().getZoneName());
		}
		
		TickRegistry.registerScheduledTickHandler(new TickHandlerTP(), Side.SERVER);
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{// ensures on ServerStart
		// event.registerPermissionProp("ForgeEssentials.BasicCommands.spawnPoint", "0;0;0;0");
		event.registerPermissionProp(CommandSetSpawn.SPAWN_TYPE_PROP, "bed"); // bed, point, none
	}
	


	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		if (e.entity instanceof EntityPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
			PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
			CommandBack.justDied.add(player.username);

			// generate for un-generated dimension
			{
				int currentDim = player.worldObj.provider.dimensionId;
				int spawnDim = player.worldObj.provider.getRespawnDimension(player);

				if (spawnDim != 0 && spawnDim == currentDim && !CommandSetSpawn.dimsWithProp.contains(currentDim))
				{
					Zone z = APIRegistry.zones.getWorldZone(player.worldObj);
					ChunkCoordinates dimPoint = player.worldObj.getSpawnPoint();
					WorldPoint point = new WorldPoint(spawnDim, dimPoint.posX, dimPoint.posY, dimPoint.posZ);
					CommandSetSpawn.setSpawnPoint(point, z);
					CommandSetSpawn.dimsWithProp.add(currentDim);

					WarpPoint p = new WarpPoint(currentDim, dimPoint.posX + .5, dimPoint.posY + 1, dimPoint.posZ + .5, player.cameraYaw, player.cameraPitch);
					CommandSetSpawn.spawns.put(player.username, p);
					return;
				}
			}
			
			PropQueryPlayerSpot query = new PropQueryPlayerSpot(player, "ForgeEssentials.BasicCommands.spawnType");
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
					EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
					
					WarpPoint point = new WarpPoint(player.worldObj.provider.dimensionId, spawn.posX + .5, spawn.posY + 1, spawn.posZ + .5, player.cameraYaw, player.cameraPitch);
					CommandSetSpawn.spawns.put(player.username, point);
					
					return;
				}
			}

			query = new PropQueryPlayerSpot(player, "ForgeEssentials.BasicCommands.spawnPoint");
			APIRegistry.perms.getPermissionProp(query);

			if (!query.hasValue())
				throw new RuntimeException("NO GLOBAL SPAWN SET!!!");

			String val = query.getStringValue();
			String[] split = val.split("[;_]");

			try
			{
				int dim = Integer.parseInt(split[0]);
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);

				WarpPoint point = new WarpPoint(dim, x + .5, y + 1, z + .5, player.cameraYaw, player.cameraPitch);
				CommandSetSpawn.spawns.put(player.username, point);
			}
			catch (Exception exception)
			{
				CommandSetSpawn.spawns.put(player.username, null);
			}
		}
	}
	public class ConfigTeleport extends ModuleConfigBase{

		private Configuration config;
		
		public ConfigTeleport(File file) {
			super(file);
		}

		@Override
		public void init() {
			config = new Configuration (file, true);
			timeout = config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
			config.save();

		}

		@Override
		public void forceSave() {
			config = new Configuration (file, true);
			config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").set(timeout);
			config.save();
		}

		@Override
		public void forceLoad(ICommandSender sender) {
			config = new Configuration (file, true);
			timeout = config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
			config.save();
		}
		
	}

}
