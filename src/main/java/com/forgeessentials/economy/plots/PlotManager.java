package com.forgeessentials.economy.plots;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.economy.plots.command.CommandPlot;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlotManager extends ServerEventHandler
{

    public static final String GROUP_PLOT_OWNER = "PLOT_OWNER";
    public static final String GROUP_PLOT_USER = "PLOT_USER";

    // Internal data permission properties (should NEVER be edited by hand)
    public static final String PERM_OWNER = FEPermissions.FE_INTERNAL + ".plot.owner";

    // Basic plot permissions
    public static final String PERM = ModuleEconomy.PERM + ".plot";
    public static final String PERM_PRICE = PERM + ".price";
    public static final String PERM_COLUMN = PERM + ".column";
    // public static final String PERM_ADMIN = PERM + ".admin";

    public static final String PERM_COMMAND = PERM + ".command";
    public static final String PERM_DEFINE = PERM_COMMAND + ".define";
    public static final String PERM_CLAIM = PERM_COMMAND + ".claim";
    public static final String PERM_DELETE = PERM_COMMAND + ".delete";
    public static final String PERM_BUY = PERM_COMMAND + ".buy";
    public static final String PERM_SELL = PERM_COMMAND + ".sell";

    public static final String PERM_LIST = PERM_COMMAND + ".list";
    public static final String PERM_LIST_OWN = PERM_LIST + ".own";
    public static final String PERM_LIST_ALL = PERM_LIST + ".all";
    public static final String PERM_LIST_SALE = PERM_LIST + ".sale";

    // Maximum number and total size of plots a user is allowed to claim
    public static final String PERM_LIMIT = PERM + ".limit";
    public static final String PERM_LIMIT_COUNT = PERM_LIMIT + ".count";
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

    /* ------------------------------------------------------------ */

    public static Map<String, Offer<AreaZone>> pendingOffers = new HashMap<>();

    public static int timeout = 120;

    /* ------------------------------------------------------------ */

    public static void serverStarting()
    {
        registerCommands();
        registerPermissions();
    }

    private static void registerCommands()
    {
        FunctionHelper.registerServerCommand(new CommandPlot());
    }

    private static void registerPermissions()
    {
        APIRegistry.perms.registerPermissionDescription(PERM_OWNER, "UUID of the owner of this plot (DO NOT MODIFY!)");

        APIRegistry.perms.registerPermissionDescription(PERM, "Plot permissions");
        APIRegistry.perms.registerPermissionProperty(PERM_PRICE, "1", "Price multiplier for plots (plot size will be multiplied with this value)");
        // APIRegistry.perms.registerPermissionPropertyOp(PERM_PRICE, "0");
        APIRegistry.perms.registerPermission(PERM_COLUMN, RegisteredPermValue.TRUE,
                "If true, all plots will always extend from bottom to top of the world. Price will only depend on X and Z dimensions.");

        APIRegistry.perms.registerPermission(PERM_COMMAND, RegisteredPermValue.TRUE, "Plot management command");
        APIRegistry.perms.registerPermission(PERM_DEFINE, RegisteredPermValue.OP, "Allows to define plots without paying");
        APIRegistry.perms.registerPermission(PERM_CLAIM, RegisteredPermValue.TRUE, "Allows to claim plots in exchange for money");
        APIRegistry.perms.registerPermission(PERM_BUY, RegisteredPermValue.TRUE, "Allows buying plots");
        APIRegistry.perms.registerPermission(PERM_SELL, RegisteredPermValue.TRUE, "Allows selling plots");

        APIRegistry.perms.registerPermission(PERM_LIST_ALL, RegisteredPermValue.OP, "List all plots");
        APIRegistry.perms.registerPermission(PERM_LIST_OWN, RegisteredPermValue.TRUE, "List own plots");
        APIRegistry.perms.registerPermission(PERM_LIST_SALE, RegisteredPermValue.TRUE, "List plots open for sale");

        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT_COUNT, "20", "Maximum number of plots a player can claim");
        APIRegistry.perms.registerPermissionDescription(PERM_LIMIT_SIZE, "Maximum total size of all plots a player can claim");

        APIRegistry.perms.registerPermissionProperty(PERM_SIZE_MIN, "3", "Minimum size of one plot axis");
        APIRegistry.perms.registerPermissionDescription(PERM_SIZE_MAX, "Maximum size of one plot axis");

        APIRegistry.perms.registerPermissionDescription(PERM_DATA, "Individual settings for plots");
        APIRegistry.perms.registerPermissionProperty(PERM_NAME, "@p's plot", "Name of the plot (@p inserts owner name)");
        APIRegistry.perms.registerPermissionDescription(PERM_FEE, "Price players have to pay in order to enter this plot");
        APIRegistry.perms.registerPermissionDescription(PERM_FEE_TIMEOUT, "Duration that a player can access the plot after paying the fee");
        APIRegistry.perms.registerPermissionDescription(PERM_SELL_PRICE, "Price that the plot can be bought for by other players (sell offer)");
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void permissionAfterLoadEvent(PermissionEvent.AfterLoad event)
    {
        Plot.loadPlots();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onZoneChange(PlayerChangedZone event)
    {
        Plot oldPlot = Plot.getPlot(event.beforePoint.toWorldPoint());
        Plot plot = Plot.getPlot(event.afterPoint.toWorldPoint());
        // TODO: This could fail, another (non-plot) zone starts on the same plane as the plot!!!
        // Plot plot = Plot.getPlot(event.afterZone.getId());
        if (oldPlot != plot && plot != null)
        {
            String plotName = plot.getName();
            if (plotName == null)
                plotName ="<unnamed>";
            
            UserIdent ident = new UserIdent(event.entityPlayer);
            Set<String> groups = plot.getZone().getStoredPlayerGroups(ident);
            if (groups.contains(PlotManager.GROUP_PLOT_OWNER))
            {
                OutputHandler.chatConfirmation(event.entityPlayer, Translator.format("You entered \"%s\". You are the owner here.", plotName));
            }
            else if (groups.contains(PlotManager.GROUP_PLOT_USER))
            {
                OutputHandler.chatConfirmation(event.entityPlayer, Translator.format("You entered \"%s\". You have user access.", plotName));
            }
            else
            {
                OutputHandler.chatConfirmation(event.entityPlayer,
                        Translator.format("You entered \"%s\". This plot belongs to %s", plotName, plot.getOwnerName()));

            }
        }
    }
}
