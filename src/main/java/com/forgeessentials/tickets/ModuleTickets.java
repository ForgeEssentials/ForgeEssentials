package com.forgeessentials.tickets;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class, configClass = ConfigTickets.class)
public class ModuleTickets {
    @FEModule.Config
    public static ConfigTickets config;

    public static final String PERMBASE = "fe.tickets";

    @FEModule.ModuleDir
    public static File moduleDir;
    public static ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
    public static List<String> categories = new ArrayList<String>();

    public static int currentID;

    private PlayerTracker playerTracker;

    private static ClassContainer ticketContainer = new ClassContainer(Ticket.class);

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
        APIRegistry.permReg.registerPermissionLevel(PERMBASE + ".new", RegGroup.GUESTS);
        APIRegistry.permReg.registerPermissionLevel(PERMBASE + ".view", RegGroup.GUESTS);

        APIRegistry.permReg.registerPermissionLevel(PERMBASE + ".tp", RegGroup.GUESTS);
        APIRegistry.permReg.registerPermissionLevel(PERMBASE + ".admin", RegGroup.OWNERS);
    }

    @FEModule.ServerStop
    public void serverStopping(FEModuleServerStopEvent e)
    {
        saveAll();
        config.forceSave();
    }

    /**
     * Used to get ID for new Tickets
     *
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
            {
                return ticket;
            }
        }
        return null;
    }
}
