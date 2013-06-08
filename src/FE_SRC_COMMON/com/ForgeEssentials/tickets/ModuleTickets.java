package com.ForgeEssentials.tickets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerStopEvent;

import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class, configClass = ConfigTickets.class)
public class ModuleTickets
{
	@FEModule.Config
	public static ConfigTickets		config;

	public static final String		PERMBASE		= "ForgeEssentials.Tickets";

	@FEModule.ModuleDir
	public static File				moduleDir;
	public static ArrayList<Ticket>	ticketList		= new ArrayList<Ticket>();
	public static List<String>		categories		= new ArrayList<String>();

	public static int				currentID;

	private PlayerTracker			playerTracker;

	private static ClassContainer	ticketContainer	= new ClassContainer(Ticket.class);

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		playerTracker = new PlayerTracker();
		GameRegistry.registerPlayerTracker(playerTracker);
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new Command());
		loadAll();
	}

	@FEModule.ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		saveAll();
		config.forceSave();
	}

	@PermRegister
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
			if (ticket.id == i)
				return ticket;
		}
		return null;
	}
}
