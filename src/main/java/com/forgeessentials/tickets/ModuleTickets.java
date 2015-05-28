package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class)
public class ModuleTickets {

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
        APIRegistry.perms.registerPermission(PERMBASE + ".new", RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERMBASE + ".view", RegisteredPermValue.TRUE);

        APIRegistry.perms.registerPermission(PERMBASE + ".tp", RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERMBASE + ".admin", RegisteredPermValue.OP);
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
        if (PermissionsManager.checkPermission(e.player, ModuleTickets.PERMBASE + ".admin"))
        {
            if (!ModuleTickets.ticketList.isEmpty())
            {
                OutputHandler.sendMessage(e.player, EnumChatFormatting.DARK_AQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
            }
        }
    }
    
}
