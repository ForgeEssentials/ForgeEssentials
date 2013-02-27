package com.ForgeEssentials.tickets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.PostInit;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerPostInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class, configClass = ConfigTickets.class)
public class ModuleTickets
{
	@Config
	public static ConfigTickets		config;

	public static final String		PERMBASE	= "ForgeEssentials.Tickets";

	@ModuleDir
	public static File				moduleDir;
	public static ArrayList<Ticket>	ticketList	= new ArrayList<Ticket>();
	public static List<String>		categories	= new ArrayList<String>();
	
	public static int				currentID;

	private PlayerTracker	playerTracker;
	
	private static ClassContainer ticketContainer = new ClassContainer(Ticket.class);

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		playerTracker = new PlayerTracker();
		GameRegistry.registerPlayerTracker(playerTracker);
	}

	@PostInit
	public void postLoad(FEModulePostInitEvent e)
	{
		
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new Command());
		loadAll();
	}

	@ServerPostInit
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		saveAll();
		config.forceSave();
	}

	@PermRegister(ident = "ModuleTickets")
	public static void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(PERMBASE + ".command", RegGroup.GUESTS);
		event.registerPermissionLevel(PERMBASE + ".new", RegGroup.GUESTS);
		event.registerPermissionLevel(PERMBASE + ".view", RegGroup.GUESTS);

		event.registerPermissionLevel(PERMBASE + ".tp", RegGroup.GUESTS);
		event.registerPermissionLevel(PERMBASE + ".admin", RegGroup.OWNERS);
	}

	/**
	 * Used to get ID for new Tickets
	 * @return
	 */
	public static int getNextID()
	{
		currentID++;
		return currentID;
	}

	public static void loadAll()
	{
		for (Object obj : DataStorageManager.getReccomendedDriver().loadAllObjects(ticketContainer))
		{
			ticketList.add((Ticket) obj);
		}
	}

	public static void saveAll()
	{
		for (Ticket ticket : ticketList)
		{
			DataStorageManager.getReccomendedDriver().saveObject(ticketContainer, ticket);
		}
	}
	
	public static Ticket getID(int i)
	{
		for (Ticket ticket : ticketList)
		{
			if(ticket.id == i)
				return ticket;
		}
		return null;
	}
}
