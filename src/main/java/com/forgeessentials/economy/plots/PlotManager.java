package com.forgeessentials.economy.plots;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.economy.plots.command.CommandBuyPlot;
import com.forgeessentials.economy.plots.command.CommandListPlot;
import com.forgeessentials.economy.plots.command.CommandRemovePlot;
import com.forgeessentials.economy.plots.command.CommandSetPlot;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlotManager extends ServerEventHandler
{

    public static final String GROUP_PLOT_OWNER = "PLOT_OWNER";
    public static final String GROUP_PLOT_USER = "PLOT_USER";

    // Internal data permission properties (should NEVER be edited by hand)
    public static final String PERM_OWNER = FEPermissions.FE_INTERNAL + ".plot.owner";

    // Basic plot permissions
    public static final String PERM = ModuleEconomy.PERM + ".plot";
    public static final String PERM_COMMAND = PERM + ".command";
    public static final String PERM_PRICE = PERM + ".price";
    public static final String PERM_COLUMN = PERM + ".column";
    public static final String PERM_ADMIN = PERM + ".admin";

    // Maximum number and total size of plots a user is allowed to claim
    public static final String PERM_LIMIT = PERM + ".limit";
    public static final String PERM_LIMIT_NUM = PERM_LIMIT + ".count";
    public static final String PERM_LIMIT_SIZE = PERM_LIMIT + ".size";

    // Maximum and minimum size a plot can be
    public static final String PERM_SIZE = PERM + ".size";
    public static final String PERM_SIZE_MIN = PERM_SIZE + ".min";
    public static final String PERM_SIZE_MAX = PERM_SIZE + ".max";

    // User editable plot data permissions
    public static final String PERM_DATA = PERM + ".data";
    public static final String PERM_NAME = PERM_DATA + ".name";
    public static final String PERM_FEE = PERM_DATA + ".fee";
    public static final String PERM_FEE_TIMEOUT = PERM_DATA + ".fee.timeout";
    public static final String PERM_SELL_PRICE = PERM_DATA + ".price";

    public static Map<String, Offer<AreaZone>> pendingOffers = new HashMap<>();

    public static int timeout = 120;

    public static void serverStarting()
    {
        registerCommands();
        registerPermissions();
    }

    private static void registerCommands()
    {
        FunctionHelper.registerServerCommand(new CommandBuyPlot());
        FunctionHelper.registerServerCommand(new CommandRemovePlot());
        FunctionHelper.registerServerCommand(new CommandSetPlot());
        FunctionHelper.registerServerCommand(new CommandListPlot());
    }

    private static void registerPermissions()
    {
        APIRegistry.perms.registerPermissionDescription(PERM_OWNER, "UUID of the owner of this plot (DO NOT MODIFY!)");

        APIRegistry.perms.registerPermissionDescription(PERM, "Plot permissions");
        APIRegistry.perms.registerPermissionProperty(PERM_PRICE, "1", "Price multiplier for plots (plot size will be multiplied with this value)");
        // APIRegistry.perms.registerPermissionPropertyOp(PERM_PRICE, "0");
        APIRegistry.perms.registerPermission(PERM_COLUMN, RegisteredPermValue.TRUE,
                "If true, all plots will always extend from bottom to top of the world. Price will only depend on X and Z dimensions.");
        APIRegistry.perms.registerPermission(PERM_ADMIN, RegisteredPermValue.OP, "Makes a player a plot admin, who can configure plots");

        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT_NUM, "20", "Maximum number of plots a player can claim");
        APIRegistry.perms.registerPermissionDescription(PERM_LIMIT_SIZE, "Maximum total size of all plots a player can claim");

        APIRegistry.perms.registerPermissionProperty(PERM_SIZE_MIN, "3", "Minimum size of one plot axis");
        APIRegistry.perms.registerPermissionDescription(PERM_SIZE_MAX, "Maximum size of one plot axis");

        APIRegistry.perms.registerPermissionDescription(PERM_DATA, "Individual settings for plots");
        APIRegistry.perms.registerPermissionProperty(PERM_NAME, "%s's plot", "Name of the plot (%s inserts owner name)");
        APIRegistry.perms.registerPermissionDescription(PERM_FEE, "Price players have to pay in order to enter this plot");
        APIRegistry.perms.registerPermissionDescription(PERM_FEE_TIMEOUT, "Duration that a player can access the plot after paying the fee");
        APIRegistry.perms.registerPermissionDescription(PERM_SELL_PRICE, "Price that the plot can be bought for by other players (sell offer)");
    }

    @SubscribeEvent
    public void permissionAfterLoadEvent(PermissionEvent.AfterLoad event)
    {
        Plot.registerPlots();
    }

    @SubscribeEvent
    public void onPlotSet(PlotEvent.Define e)
    {
        e.plot.addPlayerToGroup(new UserIdent(e.player), PlotManager.GROUP_PLOT_OWNER);
    }

    @SubscribeEvent
    public void onOwnerAdd(PlotEvent.OwnerSet e)
    {
        e.plot.addPlayerToGroup(new UserIdent(e.player), PlotManager.GROUP_PLOT_OWNER);
    }

    @SubscribeEvent
    public void onOwnerUnset(PlotEvent.OwnerUnset e)
    {
        e.plot.removePlayerFromGroup(new UserIdent(e.player), PlotManager.GROUP_PLOT_OWNER);
    }

    @SubscribeEvent
    public void onRentDefault(PlotEvent.RentDefaulted e)
    {
        e.plot.removePlayerFromGroup(new UserIdent(e.player), PlotManager.GROUP_PLOT_OWNER);
    }

    @SubscribeEvent
    public void onZoneChange(PlayerChangedZone event)
    {
        // if (e.afterZone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_ISPLOT))
        // {
        // if (e.afterZone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERMPROP_DENYENTRY)
        // && e.afterZone.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER) != new
        // UserIdent(e.entityPlayer).getUuid().toString())
        // {
        // // TODO implement players not allowed to enter zone
        // }
        // if (e.afterZone.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERMPROP_ENTRYFEE) != "")
        // {
        //
        // }
        // }
    }
}
