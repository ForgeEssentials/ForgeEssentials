package com.ForgeEssentials.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.chat.commands.CommandMsg;
import com.ForgeEssentials.chat.commands.CommandMute;
import com.ForgeEssentials.chat.commands.CommandNickname;
import com.ForgeEssentials.chat.commands.CommandR;
import com.ForgeEssentials.chat.commands.CommandUnmute;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ModuleChat implements IFEModule
{
	public static ConfigChat conf;

	public ModuleChat()
	{
		conf = new ConfigChat();
	}

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Chat module is enabled. Loading...");
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		Chat chat = new Chat();
		MinecraftForge.EVENT_BUS.register(chat);
		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		NetworkRegistry.instance().registerChatListener(chat);

	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		
		File banedFile = new File(ForgeEssentials.FEDIR, "bannedwords.txt");
		try 
		{
			if(!banedFile.exists()) banedFile.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(banedFile));
			String line;
			while ((line = br.readLine()) != null) 
			{
				System.out.println(line.trim());
				Chat.bannedWords.add(line.trim());
			}
			br.close();
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandMsg());
		e.registerServerCommand(new CommandR());
		e.registerServerCommand(new CommandNickname());
		e.registerServerCommand(new CommandMute());
		e.registerServerCommand(new CommandUnmute());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		// TODO Auto-generated method stub

	}
	
	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerPermissionDefault("ForgeEssentials.chat.commands.msg", true);
		event.registerPermissionDefault("ForgeEssentials.chat.commands.r", true);
		event.registerPermissionDefault("ForgeEssentials.chat.usecolor", false);
		event.registerPermissionDefault("ForgeEssentials.chat.mute", false);
		event.registerPermissionDefault("ForgeEssentials.chat.unmutemu", false);
		event.registerPermissionDefault("ForgeEssentials.chat.nickname.self", false);
		event.registerPermissionDefault("ForgeEssentials.chat.nickname.others", false);
	}

}
