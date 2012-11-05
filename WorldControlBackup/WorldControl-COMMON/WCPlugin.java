package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.CommandBase;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public abstract class WCPlugin {
	
	public static APIHelper helper;
	
	private List<CommandBase> commands = new ArrayList<CommandBase>();
	
	public void load() {
		
	}
	
	public void registerCommand(CommandBase comm) {
		commands.add(comm);
	}
	
	public void loadCommands(FMLServerStartingEvent event) {
		for(int i = 0;i<commands.size();i++) {
			event.registerServerCommand(commands.get(i));
		}
	}
	
}
