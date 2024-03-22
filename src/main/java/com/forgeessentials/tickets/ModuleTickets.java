package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigSaver;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleTickets implements ConfigSaver
{
    private static ForgeConfigSpec TICKETS_CONFIG;
    private static final ConfigData data = new ConfigData("Tickets", TICKETS_CONFIG, new ForgeConfigSpec.Builder());

    public static final String PERMBASE = "fe.tickets";

    public static ArrayList<Ticket> ticketList = new ArrayList<>();

    public static List<String> categories = new ArrayList<>();

    public static int currentID;

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandTicket(true), event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        loadAll();
        APIRegistry.perms.registerPermission(PERMBASE + ".new", DefaultPermissionLevel.ALL, "Create new tickets");
        APIRegistry.perms.registerPermission(PERMBASE + ".view", DefaultPermissionLevel.ALL, "View tickets");

        APIRegistry.perms.registerPermission(PERMBASE + ".tp", DefaultPermissionLevel.ALL,
                "Teleport to ticket location");
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
        ticketList.addAll(loadedTickets.values());
    }

    public static void saveAll()
    {
        for (Ticket ticket : ticketList)
        {
            DataManager.getInstance().save(ticket, Integer.toString(ticket.id));
        }
        FEcategories.set(ModuleTickets.categories);
        FEcurrentID.set(ModuleTickets.currentID);
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
        if (APIRegistry.perms.checkPermission(e.getPlayer(), ModuleTickets.PERMBASE + ".admin"))
        {
            if (!ModuleTickets.ticketList.isEmpty())
            {
                ChatOutputHandler.sendMessage(e.getPlayer().createCommandSourceStack(),
                        ChatFormatting.DARK_AQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
            }
        }
    }

    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEcategories;
    static ForgeConfigSpec.IntValue FEcurrentID;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        LoggingHandler.felog.debug("Loading Tickets Config");
        BUILDER.push("Tickets");
        FEcategories = BUILDER.defineList("categories", new ArrayList<String>() {
            {
                add("griefing");
                add("overflow");
                add("dispute");
            }
        }, ConfigBase.stringValidator);
        FEcurrentID = BUILDER.comment("Don't change anything in there.").defineInRange("currentID", 0, 0,
                Integer.MAX_VALUE);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        ModuleTickets.categories = new ArrayList<>(FEcategories.get());
        ModuleTickets.currentID = FEcurrentID.get();
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }

    @Override
    public void save(boolean reload)
    {
        FEcategories.set(ModuleTickets.categories);
        FEcurrentID.set(ModuleTickets.currentID);
    }
}
