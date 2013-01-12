package com.ForgeEssentials.commands.util;

import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.commands.CommandBack;
import com.ForgeEssentials.commands.CommandBed;
import com.ForgeEssentials.commands.CommandBurn;
import com.ForgeEssentials.commands.CommandButcher;
import com.ForgeEssentials.commands.CommandCapabilities;
import com.ForgeEssentials.commands.CommandColorize;
import com.ForgeEssentials.commands.CommandCraft;
import com.ForgeEssentials.commands.CommandEnderchest;
import com.ForgeEssentials.commands.CommandHeal;
import com.ForgeEssentials.commands.CommandHome;
import com.ForgeEssentials.commands.CommandJump;
import com.ForgeEssentials.commands.CommandKit;
import com.ForgeEssentials.commands.CommandModlist;
import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandPing;
import com.ForgeEssentials.commands.CommandPotion;
import com.ForgeEssentials.commands.CommandRemove;
import com.ForgeEssentials.commands.CommandRepair;
import com.ForgeEssentials.commands.CommandRules;
import com.ForgeEssentials.commands.CommandSeeInventory;
import com.ForgeEssentials.commands.CommandServerDo;
import com.ForgeEssentials.commands.CommandSetspawn;
import com.ForgeEssentials.commands.CommandSmite;
import com.ForgeEssentials.commands.CommandSpawn;
import com.ForgeEssentials.commands.CommandSpawnMob;
import com.ForgeEssentials.commands.CommandTPS;
import com.ForgeEssentials.commands.CommandTp;
import com.ForgeEssentials.commands.CommandTphere;
import com.ForgeEssentials.commands.CommandTppos;
import com.ForgeEssentials.commands.CommandVirtualchest;
import com.ForgeEssentials.commands.CommandWarp;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommandRegistrar {

	protected static boolean general;
	protected static boolean utility;
	protected static boolean op;
	protected static boolean fun;
	protected static boolean teleport;
	protected static boolean cheat;
	public static void load(FMLServerStartingEvent e) {

		// general
		if (general){
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandRules());
		e.registerServerCommand(new CommandModlist());
		}
		// utility
		if (utility){
		e.registerServerCommand(new CommandButcher());
		e.registerServerCommand(new CommandRemove());
		e.registerServerCommand(new CommandSpawnMob());
		e.registerServerCommand(new CommandTPS());
		e.registerServerCommand(new CommandAFK());
		e.registerServerCommand(new CommandKit());
		e.registerServerCommand(new CommandEnderchest());
		e.registerServerCommand(new CommandVirtualchest());
		e.registerServerCommand(new CommandCapabilities());
		e.registerServerCommand(new CommandSetspawn());
		e.registerServerCommand(new CommandJump());
		e.registerServerCommand(new CommandCraft());
		e.registerServerCommand(new CommandPing());
		}
		// op
		if (op){
		e.registerServerCommand(new CommandServerDo());
		e.registerServerCommand(new CommandSeeInventory());
		}
		// fun
		if (fun){
		e.registerServerCommand(new CommandSmite());
		e.registerServerCommand(new CommandBurn());
		e.registerServerCommand(new CommandPotion());
		e.registerServerCommand(new CommandColorize());
		}
		// teleport
		if (teleport){
		e.registerServerCommand(new CommandBack());
		e.registerServerCommand(new CommandBed());
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandSpawn());
		e.registerServerCommand(new CommandTp());
		e.registerServerCommand(new CommandTphere());
		e.registerServerCommand(new CommandTppos());
		e.registerServerCommand(new CommandWarp());
		}
		// cheat
		if (cheat){
		e.registerServerCommand(new CommandRepair());
		e.registerServerCommand(new CommandHeal());
		}
		
	}

}
