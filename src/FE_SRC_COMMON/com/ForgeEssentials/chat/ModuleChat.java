package com.ForgeEssentials.chat;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
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

	public ModuleChat() {
		
		
		conf = new ConfigChat();

	}
	@Override
	public void preLoad(FMLPreInitializationEvent e) {
		OutputHandler.SOP("Chat module is enabled. Loading...");

	}

	@Override
	public void load(FMLInitializationEvent e) {
		Chat chat = new Chat();
		MinecraftForge.EVENT_BUS.register(chat);
	    NetworkRegistry.instance().registerChatListener(chat);

	}

	@Override
	public void postLoad(FMLPostInitializationEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStarted(FMLServerStartedEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e) {
		// TODO Auto-generated method stub

	}

}
