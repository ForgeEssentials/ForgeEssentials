package com.forgeessentials.tickets;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@FEModule(name = "Tickets", parentMod = ForgeEssentials.class, configClass = ConfigTickets.class)
public class ModuleTickets {
    public static final String PERMBASE = "fe.tickets";
    @FEModule.Config
    public static ConfigTickets config;
    @FEModule.ModuleDir
    public static File moduleDir;
    public static ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
    public static List<String> categories = new ArrayList<String>();

    public static int currentID;

    private static ClassContainer ticketContainer = new ClassContainer(Ticket.class);

    @PermRegister
    public static void registerPermissions(IPermRegisterEvent event)
    {
        event.registerPermissionLevel(PERMBASE + ".new", RegGroup.GUESTS);
        event.registerPermissionLevel(PERMBASE + ".view", RegGroup.GUESTS);

        event.registerPermissionLevel(PERMBASE + ".tp", RegGroup.GUESTS);
        event.registerPermissionLevel(PERMBASE + ".admin", RegGroup.OWNERS);
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

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {
        FMLCommonHandler.instance().bus().register(this);
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

    @SubscribeEvent
    public void loadData(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(e.player, ModuleTickets.PERMBASE + ".admin")))
        {
            if (!ModuleTickets.ticketList.isEmpty())
            {
                ChatUtils.sendMessage(e.player, EnumChatFormatting.DARK_AQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
            }
        }
    }
}
