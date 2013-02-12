package com.ForgeEssentials.backup;

import java.io.File;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class, configClass = BackupConfig.class)
public class ModuleBackup
{
	@Config
	public static BackupConfig	config;

	@ModuleDir
	public static File			moduleDir;

	public static File			baseFolder;

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		OutputHandler.info("Backup module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandBackup());
	}

	@PermRegister(ident = "ModuleBackups")
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.backup", RegGroup.OWNERS);
	}

	@ForgeSubscribe
	public void worldUnload(WorldEvent.Unload e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			if (config.backupIfUnloaded)
			{
				new Backup((WorldServer) e.world, false);
			}
		}
	}
}
