package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigSaver;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class)
public class ModuleTickets implements ConfigSaver
{
	private static ForgeConfigSpec TICKETS_CONFIG;
	private static final ConfigData data = new ConfigData("Tickets", TICKETS_CONFIG, new ForgeConfigSpec.Builder());
	
    public static final String PERMBASE = "fe.tickets";

    public static ArrayList<Ticket> ticketList = new ArrayList<Ticket>();

    public static List<String> categories = new ArrayList<String>();

    public static int currentID;

    @SubscribeEvent
    public void load(FEModuleCommonSetupEvent e)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandTicket(true), dispatcher);
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

    static ForgeConfigSpec.ConfigValue<List<String>> FEcategories;
    static ForgeConfigSpec.IntValue FEcurrentID;

	@Override
	public void load(Builder BUILDER, boolean isReload) {
        LoggingHandler.felog.debug("Loading Tickets Config");
        BUILDER.push("Tickets");
        FEcategories = BUILDER.define("categories", new ArrayList<String>(){{add("griefing");add("overflow");add("dispute");}});
        FEcurrentID = BUILDER.comment("Don't change anythign in there.").defineInRange("currentID", 0, 0, Integer.MAX_VALUE);
        BUILDER.pop();
	}

	@Override
	public void bakeConfig(boolean reload) {
		ModuleTickets.categories = FEcategories.get();
        ModuleTickets.currentID = FEcurrentID.get();
	}

	@Override
	public ConfigData returnData() {
		return data;
	}

	@Override
	public void save(boolean reload) {
		FEcategories.set(ModuleTickets.categories);
        FEcurrentID.set(ModuleTickets.currentID);
	}
}
