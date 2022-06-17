package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.util.text.TextFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    public void load(FEModuleCommonSetupEvent e)
    {
        FECommandManager.registerCommand(new CommandTicket());
        FMLCommonHandler.instance().bus().register(this);
        // Config ForgeEssentials.getConfigManager().registerLoader("Tickets", new ConfigTickets());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        loadAll();
        APIRegistry.perms.registerPermission(PERMBASE + ".new", DefaultPermissionLevel.ALL, "Create new tickets");
        APIRegistry.perms.registerPermission(PERMBASE + ".view", DefaultPermissionLevel.ALL, "View tickets");

        APIRegistry.perms.registerPermission(PERMBASE + ".tp", DefaultPermissionLevel.ALL, "Teleport to ticket location");
        APIRegistry.perms.registerPermission(PERMBASE + ".admin", DefaultPermissionLevel.OP, "Administer tickets");
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e)
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
        if (PermissionAPI.hasPermission(e.getPlayer(), ModuleTickets.PERMBASE + ".admin"))
        {
            if (!ModuleTickets.ticketList.isEmpty())
            {
                ChatOutputHandler.sendMessage(e.getPlayer().createCommandSourceStack(),
                        TextFormatting.DARK_AQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
            }
        }
    }

    static ForgeConfigSpec.ConfigValue<String[]> FEcategories;
    static ForgeConfigSpec.IntValue FEcurrentID;

    public static void load(ForgeConfigSpec.Builder BUILDER)
    {
        LoggingHandler.felog.debug("Loading Tickets Config");
        BUILDER.push("Tickets");
        FEcategories = BUILDER.define("categories", new String[] { "griefing", "overflow", "dispute" });
        FEcurrentID = BUILDER.comment("Don't change anythign in there.").defineInRange("currentID", 0, 0, Integer.MAX_VALUE);
        BUILDER.pop();
    }

    public static void bakeConfig(boolean reload)
    {
        ModuleTickets.categories = Arrays.asList(FEcategories.get());
        ModuleTickets.currentID = FEcurrentID.get();
    }

    public static void save()
    {
        FEcategories.set(ModuleTickets.categories.toArray(new String[0]));
        FEcurrentID.set(ModuleTickets.currentID);
    }

}
