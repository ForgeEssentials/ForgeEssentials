package com.ForgeEssentials.snooper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.KeyGenerator;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.snooper.response.MCstatsInfo;
import com.ForgeEssentials.snooper.response.PlayerInfoResonce;
import com.ForgeEssentials.snooper.response.PlayerInv;
import com.ForgeEssentials.snooper.response.Responces;
import com.ForgeEssentials.snooper.response.ServerInfo;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerStopEvent;

@FEModule(name = "SnooperModule", parentMod = ForgeEssentials.class, configClass = ConfigSnooper.class)
public class ModuleSnooper
{
	@FEModule.Config
	public static ConfigSnooper	configSnooper;

	@FEModule.ModuleDir
	public File					folder;

	public static int			port;
	public static String		hostname;
	public static boolean		enable;

	public static SocketListner	socketListner;

	private static int			id	= 0;

	public static String		key;

	public ModuleSnooper()
	{
		MinecraftForge.EVENT_BUS.register(this);

		APIRegistry.registerResponse(0, new Responces());

		APIRegistry.registerResponse(1, new ServerInfo());
		APIRegistry.registerResponse(2, new MCstatsInfo());

		APIRegistry.registerResponse(5, new PlayerInfoResonce());
		APIRegistry.registerResponse(6, new PlayerInv());
	}

	@FEModule.ServerInit()
	public void serverStarting(FEModuleServerInitEvent e)
	{
		getKey();
		e.registerServerCommand(new CommandReloadQuery());
		start();
	}

	private void getKey()
	{
		try
		{
			File file = new File(folder.getAbsolutePath(), "key.key");
			if (file.exists())
			{
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				in.close();
				key = new String(buffer);
			}
			else
			{
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file.getAbsoluteFile());
				KeyGenerator kgen = KeyGenerator.getInstance("AES");
				kgen.init(128);
				byte[] buffer = kgen.generateKey().getEncoded();
				out.write(buffer);
				out.close();
				key = new String(buffer);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@FEModule.ServerStop()
	public void serverStopping(FEModuleServerStopEvent e)
	{
		stop();
	}

	@FEModule.Reload()
	public void reload(ICommandSender sender)
	{
		stop();
		getKey();
		start();
	}

	public static void start()
	{
		socketListner = new SocketListner();
	}

	public static void stop()
	{
		socketListner.stop();
	}

	@PermRegister
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.Snooper.commands.queryreload", RegGroup.OWNERS);
	}

	public static int id()
	{
		return id++;
	}
}
