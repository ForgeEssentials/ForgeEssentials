package com.ForgeEssentials.commands;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PropQueryBlanketZone;
import com.ForgeEssentials.commands.util.BlockPoweredOreMod;
import com.ForgeEssentials.commands.util.CommandDataManager;
import com.ForgeEssentials.commands.util.CommandRegistrar;
import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.commands.util.EventHandler;
import com.ForgeEssentials.commands.util.MCStatsHelper;
import com.ForgeEssentials.commands.util.MobTypeLoader;
import com.ForgeEssentials.commands.util.PlayerTrackerCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.compat.CompatMCStats;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(configClass = ConfigCmd.class, name = "CommandsModule", parentMod = ForgeEssentials.class)
public class ModuleCommands
{
	@FEModule.Config
	public static ConfigCmd			conf;

	@FEModule.ModuleDir
	public static File				cmddir;

	public static EventHandler		eventHandler	= new EventHandler();
	private static MCStatsHelper	mcstats			= new MCStatsHelper();

	@FEModule.PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		MobTypeLoader.preLoad(e);
		GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(eventHandler);
		CommandRegistrar.commandConfigs(conf.config);
		CompatMCStats.registerStats(mcstats);
		//override BlockPoweredOre
		registerBlocks();
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
	}

	@FEModule.ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		TickRegistry.registerScheduledTickHandler(new TickHandlerCommands(), Side.SERVER);
		CommandDataManager.load();

		PropQueryBlanketZone query = new PropQueryBlanketZone(CommandSetSpawn.SPAWN_PROP, ZoneManager.getGLOBAL(), false);
		PermissionsAPI.getPermissionProp(query);

		// nothing set for the global??
		if (!query.hasValue())
		{
			ChunkCoordinates point = FunctionHelper.getDimension(0).provider.getSpawnPoint();
			String val = "0;" + point.posX + ";" + point.posY + ";" + point.posZ;
			PermissionsAPI.setGroupPermissionProp(PermissionsAPI.getDEFAULT().name, CommandSetSpawn.SPAWN_PROP, val, ZoneManager.getGLOBAL().getZoneName());
		}
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		CommandRegistrar.registerPermissions(event);
		event.registerPermissionLevel("ForgeEssentials.BasicCommands._ALL_", RegGroup.OWNERS);

		// ensures on ServerStart
		// event.registerPermissionProp("ForgeEssentials.BasicCommands.spawnPoint", "0;0;0;0");
		event.registerPermissionProp(CommandSetSpawn.SPAWN_TYPE_PROP, "bed"); // bed, point, none
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		CommandDataManager.save();
	}

	void registerBlocks(){
		Block instance = Block.dirt;
		Field[] blockList = Block.class.getFields();
		for (Field block : blockList) {
		    try {
				if (block.get(instance) instanceof Block && ((Block)block.get(instance)).blockID == Block.blockRedstone.blockID){
					try {
						setFinalStatic(block, null);					
						Block.blocksList[152] = null;
						setFinalStatic(block,(new BlockPoweredOreMod(152)).setHardness(5.0F).setResistance(10.0F).setStepSound(Block.soundMetalFootstep).setUnlocalizedName("blockRedstone"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		
			
			
		      
	}
	static void setFinalStatic(Field field, Object newValue) throws Exception {
	    field.setAccessible(true);

	    // remove final modifier from field
	    Field modifiersField = Field.class.getDeclaredField("modifiers");
	    modifiersField.setAccessible(true);
	    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

	    field.set(null, newValue);
	}

}
