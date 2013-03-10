package com.ForgeEssentials.snooper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.KeyGenerator;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.snooper.snooperAPI;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.snooper.response.MCstatsInfo;
import com.ForgeEssentials.snooper.response.PlayerInfoResonce;
import com.ForgeEssentials.snooper.response.PlayerInv;
import com.ForgeEssentials.snooper.response.Responces;
import com.ForgeEssentials.snooper.response.ServerInfo;

@FEModule(name = "SnooperModule", parentMod = ForgeEssentials.class, configClass = ConfigSnooper.class)
public class ModuleSnooper
{
	@Config
	public static ConfigSnooper		configSnooper;
	
	@ModuleDir
	public File						folder;

	public static int				port;
	public static String			hostname;
	public static boolean			enable;

	public static boolean			autoReboot;

	public static SocketListner		socketListner;

	private static int				id = 0;

	public static String			key;

	public static int				keysize;

	public ModuleSnooper()
	{
		MinecraftForge.EVENT_BUS.register(this);

		snooperAPI.registerResponce(0, new Responces());
		
		snooperAPI.registerResponce(1, new ServerInfo());
		snooperAPI.registerResponce(2, new MCstatsInfo());

		snooperAPI.registerResponce(5, new PlayerInfoResonce());
		snooperAPI.registerResponce(6, new PlayerInv()); 
	}

	@ServerInit
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
			if(file.exists())
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
				kgen.init(keysize);
				byte[] buffer  = kgen.generateKey().getEncoded();
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

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		stop();
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
		event.registerPermissionLevel("ForgeEssentials.Snooper._ALL_", RegGroup.OWNERS);
	}

	public static int id()
	{
		return id++;
	}
}
