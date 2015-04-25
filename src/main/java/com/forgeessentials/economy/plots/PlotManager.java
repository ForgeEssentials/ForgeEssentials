package com.forgeessentials.economy.plots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.economy.commands.plots.CommandBuyPlot;
import com.forgeessentials.economy.commands.plots.CommandListPlot;
import com.forgeessentials.economy.commands.plots.CommandRemovePlot;
import com.forgeessentials.economy.commands.plots.CommandSetPlot;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlotManager extends ServerEventHandler
{

    public static Map<String, Offer<AreaZone>> pendingOffers = new HashMap<>();

    public static int timeout;

    public static final String PERM_PLOT = ModuleEconomy.PERM + ".plot";
    public static final String PERM_PRICE = PERM_PLOT + ".price";
    public static final String PERM_COLUMN = PERM_PLOT + ".column";

    public static final String PLOT_PERM = "fe.economy.plots";

    public static final String PLOT_NAME_ID = "[PLOT]";
    public static final String PLOT_GROUP = "PlotOwners";
    public static final String DATA_PERM = PLOT_PERM + ".data";
    public static final String PLOT_ISPLOT = DATA_PERM + ".isplot";
    public static final String PLOT_OWNER = DATA_PERM + ".owner";
    public static final String PLOT_NAME_PERM = DATA_PERM + ".name";
    public static final String PLOT_VALUE = DATA_PERM + ".value";

    public static final String PLOT_PERMPROP_CLAIMCAP = PLOT_PERM + ".claimcap";
    public static final String PLOT_PERMPROP_CLAIMED = PLOT_PERM + ".claimed";

    public static final String PLOT_ENTRY = PLOT_PERM + ".entry";
    public static final String PLOT_PERMPROP_ENTRYFEE = PLOT_ENTRY + ".fee";
    public static final String PLOT_PERMPROP_DENYENTRY = PLOT_ENTRY + ".deny";
    public static final String PLOT_PERMPROP_EXEMPT = PLOT_ENTRY + ".exempt";

    public void registerPermissionsAndCommands()
    {
        FunctionHelper.registerServerCommand(new CommandBuyPlot());
        FunctionHelper.registerServerCommand(new CommandRemovePlot());
        FunctionHelper.registerServerCommand(new CommandSetPlot());
        FunctionHelper.registerServerCommand(new CommandListPlot());

        APIRegistry.perms.registerPermissionProperty(PERM_PRICE, "1", "Price multiplier for plots (plot size will be multiplied with this value)");
        APIRegistry.perms.registerPermission(PERM_COLUMN, RegisteredPermValue.TRUE,
                "Define, if plots only account for horizontal size, or also in vertical space (WARNING: heavily influences price!)");
        APIRegistry.perms.registerPermissionDescription(PLOT_PERMPROP_CLAIMCAP, "Maximum amount of land a player is allowed to claim.");
    }

    public static void printPlotDetails(ICommandSender sender, AreaZone plot)
    {
        if (!plot.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERM))
            return;
        OutputHandler.chatNotification(
                sender,
                "Name: " + plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_NAME_PERM) + " Owner: "
                        + UserIdent.getUsernameByUuid(plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER)) + "Location: between "
                        + plot.getArea().getHighPoint().toString() + " and " + plot.getArea().getLowPoint().toString() + " Value: "
                        + plot.getGroupPermission(Zone.GROUP_DEFAULT, PLOT_VALUE));
    }

    public static AreaZone[] getPlotList()
    {
        List<AreaZone> zones = new ArrayList<AreaZone>();
        for (Zone zone : APIRegistry.perms.getZones())
        {
            if (zone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERM) && zone instanceof AreaZone)
                zones.add((AreaZone) zone);
        }
        return zones.toArray(new AreaZone[] {});
    }

    @SubscribeEvent
    public void onPlotSet(PlotEvent.Define e)
    {
        e.plot.addPlayerToGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onOwnerAdd(PlotEvent.OwnerSet e)
    {
        e.plot.addPlayerToGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onOwnerUnset(PlotEvent.OwnerUnset e)
    {
        e.plot.removePlayerFromGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onRentDefault(PlotEvent.RentDefaulted e)
    {
        e.plot.removePlayerFromGroup(new UserIdent(e.player), PlotManager.PLOT_GROUP);
    }

    @SubscribeEvent
    public void onZoneChange(PlayerChangedZone e)
    {
//        if (e.afterZone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_ISPLOT))
//        {
//            if (e.afterZone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERMPROP_DENYENTRY)
//                    && e.afterZone.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER) != new UserIdent(e.entityPlayer).getUuid().toString())
//            {
//                // TODO implement players not allowed to enter zone
//            }
//            if (e.afterZone.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERMPROP_ENTRYFEE) != "")
//            {
//
//            }
//        }
    }
}
