package com.forgeessentials.economy.plots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.events.PlotEvent.OwnerChanged;

public class Plot
{

    private static final String GROUP_DEFAULT = Zone.GROUP_DEFAULT;
    public static final String GROUP_PLOT_OWNER = "PLOT_OWNER";
    public static final String GROUP_PLOT_USER = "PLOT_USER";

    public static final String SERVER_OWNER = "SERVER";

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

    public static final String PERM_SET = PERM_COMMAND + ".set";
    public static final String PERM_SET_BUILD = PERM_SET + ".build";
    public static final String PERM_SET_INTERACT = PERM_SET + ".interact";

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

    private static Map<Integer, Plot> plots = new HashMap<>();

    private AreaZone zone;

    private UserIdent owner;

    private Plot(AreaZone zone, UserIdent owner)
    {
        this.zone = zone;
        this.owner = owner;
    }

    public AreaZone getZone()
    {
        return zone;
    }

    public int getDimension()
    {
        return zone.getWorldZone().getDimensionID();
    }

    public WorldPoint getCenter()
    {
        return new WorldPoint(getDimension(), zone.getArea().getCenter());
    }

    public boolean hasOwner()
    {
        return owner != null;
    }

    public UserIdent getOwner()
    {
        return owner;
    }

    public void setOwner(UserIdent newOwner)
    {
        if (newOwner == owner || (newOwner != null && newOwner.equals(owner)))
            return;
        OwnerChanged event = new PlotEvent.OwnerChanged(this, owner);
        if (owner != null)
            zone.removePlayerFromGroup(owner, GROUP_PLOT_OWNER);

        // Set new owner
        owner = newOwner;
        if (owner == null)
            zone.clearGroupPermission(GROUP_DEFAULT, PERM_OWNER);
        else
            zone.setGroupPermissionProperty(GROUP_DEFAULT, PERM_OWNER, owner.getOrGenerateUuid().toString());
        if (owner != null)
            zone.addPlayerToGroup(newOwner, GROUP_PLOT_OWNER);
        APIRegistry.getFEEventBus().post(event);
    }

    public String getOwnerName()
    {
        if (owner == null)
            return SERVER_OWNER;
        else
            return owner.getUsernameOrUUID();
    }

    public String getName()
    {
        String name = zone.getGroupPermission(GROUP_DEFAULT, PERM_NAME);
        if (name == null)
            name = APIRegistry.perms.getGroupPermissionProperty(GROUP_DEFAULT, PERM_NAME);
        if (name == null)
            return null;
        return name.replaceAll("@p", owner.getUsernameOrUUID());
    }

    public String getNameNotNull()
    {
        String name = getName();
        if (name != null)
            return name;
        return "<unnamed>";
    }

    public WorldPoint getPlotCenter()
    {
        return new WorldPoint(getDimension(), zone.getArea().getCenter());
    }

    /**
     * Gets the size that counts for price / limit calculation. Depending on whether the column flag is set or not, this
     * is the area or volume of the plot.
     * 
     * @return accounted size
     */
    public long getAccountedSize()
    {
        AreaBase area = zone.getArea();
        boolean columnMode = APIRegistry.perms.checkGroupPermission(GROUP_DEFAULT, zone, PERM_PRICE);
        return columnMode ? area.getXLength() * area.getZLength() : area.getXLength() * area.getYLength() * area.getZLength();
    }

    public long getCalculatedPrice()
    {
        String priceStr = APIRegistry.perms.getGroupPermissionProperty(GROUP_DEFAULT, getPlotCenter(), PERM_PRICE);
        if (priceStr == null)
            return 0;
        double pricePerUnit = FunctionHelper.parseDoubleDefault(priceStr, 0);
        if (pricePerUnit == 0)
            return 0;
        return (long) (getAccountedSize() * pricePerUnit);
    }

    public long getPrice()
    {
        try
        {
            String price = zone.getGroupPermission(GROUP_DEFAULT, PERM_SELL_PRICE);
            if (price == null)
                return -1;
            return Long.parseLong(price);
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    public void setPrice(int price)
    {
        if (price < 0)
            zone.setGroupPermissionProperty(GROUP_DEFAULT, PERM_SELL_PRICE, Long.toString(price));
        else
            zone.clearGroupPermission(GROUP_DEFAULT, PERM_SELL_PRICE);
    }

    public boolean isForSale()
    {
        return getPrice() >= 0;
    }

    public void setDefaultPermissions()
    {
        zone.setGroupPermissionProperty(GROUP_DEFAULT, PERM_OWNER, owner == null ? SERVER_OWNER : owner.getOrGenerateUuid().toString());
        if (owner != null)
            zone.addPlayerToGroup(owner, GROUP_PLOT_OWNER);

        zone.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, true);
        zone.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, true);
        zone.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_USE + Zone.ALL_PERMS, true);
        zone.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, true);

        //zone.setGroupPermission(GROUP_PLOT_USER, ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, false);
        //zone.setGroupPermission(GROUP_PLOT_USER, ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_PLOT_USER, ModuleProtection.PERM_USE + Zone.ALL_PERMS, true);
        zone.setGroupPermission(GROUP_PLOT_USER, ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, true);

        zone.setGroupPermission(GROUP_DEFAULT, ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_DEFAULT, ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_DEFAULT, ModuleProtection.PERM_USE + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_DEFAULT, ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, false);
        // zone.setGroupPermission(GROUP_DEFAULT, ModuleProtection.PERM_INTERACT_ENTITY, true);
    }

    public void printInfo(ICommandSender sender)
    {
        OutputHandler.chatNotification(sender, String.format("#%d: \"%s\" at %s", zone.getId(), getName(), getCenter().toString()));
    }

    public void printDetails(ICommandSender sender)
    {
        OutputHandler.chatNotification(sender, String.format("Plot #%d: %s", zone.getId(), getName()));
        OutputHandler.chatNotification(sender, String.format("  Owner: %s", owner.getUsernameOrUUID()));
        OutputHandler.chatNotification(sender,
                String.format("  Location between %s and %s", zone.getArea().getHighPoint().toString(), zone.getArea().getLowPoint().toString()));
        long price = getPrice();
        if (price >= 0)
            OutputHandler.chatNotification(sender, String.format("  Price: %d", price));
        else
            OutputHandler.chatNotification(sender, "  Not open for sale");
    }

    /* ------------------------------------------------------------ */
    /* Static plot management functions */
    /* ------------------------------------------------------------ */

    public static boolean isPlot(AreaZone zone)
    {
        return plots.containsKey(zone.getId());
    }

    public static boolean isColumnMode(int dimension)
    {
        ServerZone s = APIRegistry.perms.getServerZone();
        List<Zone> zones = new ArrayList<>();
        zones.add(s.getWorldZone(dimension));
        zones.add(s);
        zones.add(s.getRootZone());
        String permValue = s.getPermissionProperty(zones, null, Arrays.asList(GROUP_DEFAULT), PERM_COLUMN);
        return APIRegistry.perms.checkBooleanPermission(permValue);
    }

    /**
     * Gets the size that counts for price / limit calculation. Depending on whether the column flag is set or not, this
     * is the area or volume of the plot.
     * 
     * @return accounted size
     */
    public static long getAccountedSize(WorldArea area)
    {
        boolean columnMode = isColumnMode(area.getDimension());
        return columnMode ? area.getXLength() * area.getZLength() : area.getXLength() * area.getYLength() * area.getZLength();
    }

    public static long getCalculatedPrice(WorldArea area)
    {
        String priceStr = APIRegistry.perms.getGroupPermissionProperty(GROUP_DEFAULT, area.getCenter(), PERM_PRICE);
        if (priceStr == null)
            return 0;
        double pricePerUnit = FunctionHelper.parseDoubleDefault(priceStr, 0);
        if (pricePerUnit == 0)
            return 0;
        return (long) (getAccountedSize(area) * pricePerUnit);
    }

    /* ------------------------------------------------------------ */

    public static Plot getPlot(int zoneId)
    {
        return plots.get(zoneId);
    }

    public static Plot getPlot(WorldPoint point)
    {
        List<Zone> zones = APIRegistry.perms.getServerZone().getZonesAt(point);
        for (Zone zone : zones)
        {
            Plot plot = plots.get(zone.getId());
            if (plot != null)
                return plot;
        }
        return null;
    }

    public static Collection<Plot> getPlots()
    {
        return plots.values();
    }

    /* ------------------------------------------------------------ */

    private static void registerPlot(Plot plot)
    {
        plots.put(plot.getZone().getId(), plot);
    }

    public static Plot define(WorldArea area, UserIdent owner) throws EventCancelledException, PlotRedefinedException
    {
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(area.getDimension());
        for (AreaZone zone : worldZone.getAreaZones())
            if (isPlot(zone) && zone.getArea().contains(area))
                throw new PlotRedefinedException();

        if (isColumnMode(area.getDimension()))
            area = new WorldArea(area.getDimension(), area.getHighPoint().setY(MinecraftServer.getServer().getBuildLimit()), area.getLowPoint().setY(0));

        AreaZone zone = new AreaZone(worldZone, "_PLOT_" + (APIRegistry.perms.getServerZone().getMaxZoneID() + 1), area);
        Plot plot = new Plot(zone, owner);
        registerPlot(plot);
        zone.setHidden(true);
        plot.setDefaultPermissions();
        return plot;
    }

    public static void loadPlots()
    {
        plots.clear();
        for (Zone zone : APIRegistry.perms.getZones())
            if (zone instanceof AreaZone)
            {
                String ownerId = zone.getGroupPermission(GROUP_DEFAULT, PERM_OWNER);
                if (ownerId != null)
                    registerPlot(new Plot((AreaZone) zone, ownerId.equals(SERVER_OWNER) ? null : new UserIdent(ownerId)));
            }
    }

    static void registerPermissions()
    {
        IPermissionsHelper perms = APIRegistry.perms;
        RootZone root = perms.getServerZone().getRootZone();

        perms.registerPermissionDescription(PERM_OWNER, "UUID of the owner of this plot (DO NOT MODIFY!)");

        perms.registerPermissionDescription(PERM, "Plot permissions");
        perms.registerPermissionProperty(PERM_PRICE, "1", "Price multiplier for plots (plot size will be multiplied with this value)");
        // perms.registerPermissionPropertyOp(PERM_PRICE, "0");
        perms.registerPermission(PERM_COLUMN, RegisteredPermValue.TRUE,
                "If true, all plots will always extend from bottom to top of the world. Price will only depend on X and Z dimensions.");

        perms.registerPermissionProperty(PERM_LIMIT_COUNT, "20", "Maximum number of plots a player can claim");
        perms.registerPermissionDescription(PERM_LIMIT_SIZE, "Maximum total size of all plots a player can claim");

        perms.registerPermissionProperty(PERM_SIZE_MIN, "3", "Minimum size of one plot axis");
        perms.registerPermissionDescription(PERM_SIZE_MAX, "Maximum size of one plot axis");

        perms.registerPermissionDescription(PERM_DATA, "Individual settings for plots");
        perms.registerPermissionProperty(PERM_NAME, "@p's plot", "Name of the plot (@p inserts owner name)");
        perms.registerPermissionDescription(PERM_FEE, "Price players have to pay in order to enter this plot");
        perms.registerPermissionDescription(PERM_FEE_TIMEOUT, "Duration that a player can access the plot after paying the fee");
        perms.registerPermissionDescription(PERM_SELL_PRICE, "Price that the plot can be bought for by other players (sell offer)");

        perms.registerPermission(PERM_COMMAND, RegisteredPermValue.TRUE, "Plot management command");
        perms.registerPermission(PERM_DEFINE, RegisteredPermValue.OP, "Allows to define plots without paying");
        perms.registerPermission(PERM_CLAIM, RegisteredPermValue.TRUE, "Allows to claim plots in exchange for money");
        perms.registerPermission(PERM_BUY, RegisteredPermValue.TRUE, "Allows buying plots");
        perms.registerPermission(PERM_SELL, RegisteredPermValue.OP, "Allows selling plots");

        perms.registerPermission(PERM_LIST, RegisteredPermValue.TRUE, "Allows listing plots");
        perms.registerPermission(PERM_LIST_ALL, RegisteredPermValue.OP, "List all plots");
        perms.registerPermission(PERM_LIST_OWN, RegisteredPermValue.TRUE, "List own plots");
        perms.registerPermission(PERM_LIST_SALE, RegisteredPermValue.TRUE, "List plots open for sale");

        perms.registerPermission(PERM_SET, RegisteredPermValue.OP, "Control plot settings");
        perms.registerPermission(PERM_SET_BUILD, RegisteredPermValue.OP, "Control build permissions");
        perms.registerPermission(PERM_SET_INTERACT, RegisteredPermValue.OP, "Control interaction permissions");

        root.setGroupPermission(GROUP_PLOT_OWNER, PERM_SET, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, PERM_SELL, true);

        // TODO: below permissions don't really have any use, because they are overwritten by the default deny
        // permissions for _ALL_ in the plot... Thinking of a way to get this to work would be great...
        root.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_BREAK, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_PLACE, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_USE, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_INTERACT, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, ModuleProtection.PERM_INTERACT_ENTITY, true);
    }

    public static class PlotRedefinedException extends Exception
    {
    }


}
