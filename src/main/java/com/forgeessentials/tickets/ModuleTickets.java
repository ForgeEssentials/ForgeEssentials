package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.text.TextFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class)
public class ModuleTickets
{

    public static final String PERMBASE = "fe.tickets";

    public static ArrayList<Ticket> ticketList = new ArrayList<Ticket>();

    public static List<String> categories = new ArrayList<String>();

    public static int currentID;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        FECommandManager.registerCommand(new CommandTicket());
        FMLCommonHandler.instance().bus().register(this);
        ForgeEssentials.getConfigManager().registerLoader("Tickets", new ConfigTickets());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        loadAll();
        APIRegistry.perms.registerPermission(PERMBASE + ".new", DefaultPermissionLevel.ALL, "Create new tickets");
        APIRegistry.perms.registerPermission(PERMBASE + ".view", DefaultPermissionLevel.ALL, "View tickets");

        APIRegistry.perms.registerPermission(PERMBASE + ".tp", DefaultPermissionLevel.ALL, "Teleport to ticket location");
        APIRegistry.perms.registerPermission(PERMBASE + ".admin", DefaultPermissionLevel.OP, "Administer tickets");
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        saveAll();
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
        Map<String, Ticket> loadedTickets = DataManager.getInstance().loadAll(Ticket.class);
        ticketList.clear();
        for (Ticket ticket : loadedTickets.values())
            ticketList.add(ticket);
    }

    public static void saveAll()
    {
        for (Ticket ticket : ticketList)
        {
            DataManager.getInstance().save(ticket, Integer.toString(ticket.id));
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

    @SubscribeEvent
    public void loadData(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (PermissionAPI.hasPermission(e.player, ModuleTickets.PERMBASE + ".admin"))
        {
            if (!ModuleTickets.ticketList.isEmpty())
            {
                ChatOutputHandler.sendMessage(e.player, TextFormatting.DARK_AQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
            }
        }
    }

}
