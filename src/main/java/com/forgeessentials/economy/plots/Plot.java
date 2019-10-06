package com.forgeessentials.economy.plots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.events.PlotEvent.OwnerChanged;
import com.forgeessentials.util.output.ChatOutputHandler;

public class Plot
{

    public static final String GROUP_ALL = Zone.GROUP_DEFAULT;
    public static final String GROUP_PLOT_OWNER = "PLOT_OWNER";
    public static final String GROUP_PLOT_MOD = "PLOT_MOD";
    public static final String GROUP_PLOT_USER = "PLOT_USER";

    public static final String SERVER_OWNER = "SERVER";

    public static final String CATEGORY = "Plots";

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
    public static final String PERM_MODS = PERM_COMMAND + ".mods";

    public static final String PERM_SET = PERM_COMMAND + ".set";
    public static final String PERM_SET_PRICE = PERM_SET + ".price";
    public static final String PERM_SET_FEE = PERM_SET + ".fee";
    public static final String PERM_SET_NAME = PERM_SET + ".name";
    public static final String PERM_SET_OWNER = PERM_SET + ".owner";

    public static final String PERM_PERMS = PERM_COMMAND + ".perms";
    public static final String PERM_PERMS_BUILD = PERM_SET + ".build";
    public static final String PERM_PERMS_USE = PERM_SET + ".use";
    public static final String PERM_PERMS_INTERACT = PERM_SET + ".interact";
    public static final String PERM_PERMS_CHEST = PERM_SET + ".chest";

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
        return owner != null && !owner.equals(APIRegistry.IDENT_SERVER);
    }

    public UserIdent getOwner()
    {
        return owner;
    }

    public void setOwner(UserIdent newOwner)
    {
        if (newOwner == null)
            throw new IllegalArgumentException();
        if (newOwner == owner || newOwner.equals(owner))
            return;
        OwnerChanged event = new PlotEvent.OwnerChanged(this, owner);
        if (owner != null)
            zone.removePlayerFromGroup(owner, GROUP_PLOT_OWNER);

        // Set new owner
        owner = newOwner;
        zone.setGroupPermissionProperty(GROUP_ALL, PERM_OWNER, owner.getOrGenerateUuid().toString());
        zone.addPlayerToGroup(newOwner, GROUP_PLOT_OWNER);
        APIRegistry.getFEEventBus().post(event);
    }

    public String getOwnerName()
    {
        if (owner == null)
            return SERVER_OWNER;
        else
            return owner.getUsernameOrUuid();
    }

    public String getName()
    {
        String name = zone.getGroupPermission(GROUP_ALL, PERM_NAME);
        if (name == null)
            name = APIRegistry.perms.getGroupPermissionProperty(GROUP_ALL, PERM_NAME);
        if (name == null)
            return null;
        return name.replaceAll("@p", Matcher.quoteReplacement(owner.getUsernameOrUuid()));
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
     * Gets the size that counts for price / limit calculation. Depending on whether the column flag is set or not, this is the area or volume of the plot.
     * 
     * @return accounted size
     */
    public long getAccountedSize()
    {
        return Plot.getAccountedSize(getZone().getWorldArea());
    }

    public long getCalculatedPrice()
    {
        return Plot.getCalculatedPrice(getZone().getWorldArea());
    }

    public long getPrice()
    {
        return ServerUtil.parseLongDefault(zone.getGroupPermission(GROUP_ALL, PERM_SELL_PRICE), -1);
    }

    public void setPrice(long value)
    {
        if (value < 0)
            zone.clearGroupPermission(GROUP_ALL, PERM_SELL_PRICE);
        else
            zone.setGroupPermissionProperty(GROUP_ALL, PERM_SELL_PRICE, Long.toString(value));
    }

    public boolean isForSale()
    {
        return getPrice() >= 0;
    }

    /* ------------------------------------------------------------ */

    public int getFee()
    {
        return Math.max(0, ServerUtil.parseIntDefault(zone.getGroupPermission(GROUP_ALL, PERM_FEE), 0));
    }

    public void setFee(int value)
    {
        if (value < 0)
            zone.setGroupPermissionProperty(GROUP_ALL, PERM_FEE, Long.toString(value));
        else
            zone.clearGroupPermission(GROUP_ALL, PERM_FEE);
    }

    public int getFeeTimeout()
    {
        return Math.max(0, ServerUtil.parseIntDefault(zone.getGroupPermission(GROUP_ALL, PERM_FEE_TIMEOUT), 0));
    }

    public void setFeeTimeout(int minutes)
    {
        if (minutes <= 0)
            zone.setGroupPermissionProperty(GROUP_ALL, PERM_FEE_TIMEOUT, Long.toString(minutes));
        else
            zone.clearGroupPermission(GROUP_ALL, PERM_FEE_TIMEOUT);
    }

    /* ------------------------------------------------------------ */

    public void setPermission(String permission, boolean userPerm, boolean value)
    {
        zone.setGroupPermission(GROUP_PLOT_OWNER, permission, true);
        zone.setGroupPermission(GROUP_PLOT_MOD, permission, true);
        zone.setGroupPermission(userPerm ? GROUP_PLOT_USER : GROUP_ALL, permission, value);
    }

    public void setDefaultPermissions()
    {
        zone.setGroupPermissionProperty(GROUP_ALL, PERM_OWNER, owner == null ? SERVER_OWNER : owner.getOrGenerateUuid().toString());
        if (owner != null)
            zone.addPlayerToGroup(owner, GROUP_PLOT_OWNER);

        setPermission(ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, false, false);
        setPermission(ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, false, false);
        setPermission(ModuleProtection.PERM_USE + Zone.ALL_PERMS, false, false);
        setPermission(ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, false, false);

        setPermission(ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, true, false);
        setPermission(ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, true, true);
        setPermission(ModuleProtection.PERM_USE + Zone.ALL_PERMS, true, true);
        setPermission(ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, true, true);

        zone.setGroupPermission(GROUP_ALL, ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_ALL, ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_ALL, ModuleProtection.PERM_USE + Zone.ALL_PERMS, false);
        zone.setGroupPermission(GROUP_ALL, ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, false);
        // zone.setGroupPermission(GROUP_ALL, ModuleProtection.PERM_INTERACT_ENTITY + Zone.ALL_PERMS, false);
    }

    public void printInfo(ICommandSender sender)
    {
        ChatOutputHandler.chatNotification(sender, String.format("#%d: \"%s\" at %s", zone.getId(), getName(), getCenter().toString()));
    }

    public void printDetails(ICommandSender sender)
    {
        ChatOutputHandler.chatNotification(sender, String.format("Plot #%d: %s", zone.getId(), getName()));
        ChatOutputHandler.chatNotification(sender, String.format("  Owner: %s", owner.getUsernameOrUuid()));
        ChatOutputHandler.chatNotification(sender,
                String.format("  Location between %s and %s", zone.getArea().getHighPoint().toString(), zone.getArea().getLowPoint().toString()));
        long price = getPrice();
        if (price >= 0)
            ChatOutputHandler.chatNotification(sender, String.format("  Price: %d", price));
        else
            ChatOutputHandler.chatNotification(sender, "  Not open for sale");
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
        String permValue = s.getPermissionProperty(zones, null, Arrays.asList(GROUP_ALL), PERM_COLUMN, null);
        return APIRegistry.perms.checkBooleanPermission(permValue);
    }

    /**
     * Gets the size that counts for price / limit calculation. Depending on whether the column flag is set or not, this is the area or volume of the plot.
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
        String priceStr = APIRegistry.perms.getGroupPermissionProperty(GROUP_ALL, area.getCenter(), PERM_PRICE);
        if (priceStr == null)
            return 0;
        double pricePerUnit = ServerUtil.parseDoubleDefault(priceStr, 0);
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

    public static boolean hasPlots(Selection selection) {
        return plots.values().stream().anyMatch(isPartOfZone(selection));
    }

    public static Predicate<Plot> isPartOfZone(Selection selection) {
        return plot -> plot.getZone().isPartOfZone(selection);
    }

    /* ------------------------------------------------------------ */

    private static void registerPlot(Plot plot)
    {
        plots.put(plot.getZone().getId(), plot);
    }

    public static Plot define(WorldArea area, UserIdent owner) throws EventCancelledException, PlotRedefinedException
    {
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(area.getDimension());
        for (Plot zone : plots.values())
            if (zone.getZone().getArea().contains(area) || zone.getZone().getArea().intersectsWith(area))
            {
                throw new PlotRedefinedException();
            }

        if (isColumnMode(area.getDimension()))
            area = new WorldArea(area.getDimension(), area.getHighPoint().setY(FMLCommonHandler.instance().getMinecraftServerInstance().getBuildLimit()), area.getLowPoint().setY(0));

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
                UserIdent ownerIdent = UserIdent.getFromUuid(zone.getGroupPermission(GROUP_ALL, PERM_OWNER));
                if (ownerIdent != null)
                    registerPlot(new Plot((AreaZone) zone, ownerIdent));
            }
    }

    public static void deletePlot(Plot plot)
    {
        plot.getZone().getWorldZone().removeAreaZone(plot.getZone());
        plots.remove(plot.getZone().getId());
    }

    static void registerPermissions()
    {
        IPermissionsHelper perms = APIRegistry.perms;
        RootZone root = perms.getServerZone().getRootZone();

        perms.registerPermissionDescription(PERM_OWNER, "UUID of the owner of this plot (DO NOT MODIFY!)");

        perms.registerPermissionDescription(PERM, "Plot permissions");
        perms.registerPermissionProperty(PERM_PRICE, "1", "Price multiplier for plots (plot size will be multiplied with this value)");
        // perms.registerPermissionPropertyOp(PERM_PRICE, "0");
        perms.registerPermission(PERM_COLUMN, DefaultPermissionLevel.ALL,
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

        perms.registerPermission(PERM_COMMAND, DefaultPermissionLevel.ALL, "Plot management command");
        perms.registerPermission(PERM_DEFINE, DefaultPermissionLevel.OP, "Allows to define plots without paying");
        perms.registerPermission(PERM_CLAIM, DefaultPermissionLevel.ALL, "Allows to claim plots in exchange for money");
        perms.registerPermission(PERM_DELETE, DefaultPermissionLevel.OP, "Allows a player to delete any plots, including plots not owned by him.");
        perms.registerPermission(PERM_BUY, DefaultPermissionLevel.ALL, "Allows buying plots");
        perms.registerPermission(PERM_SELL, DefaultPermissionLevel.OP, "Allows selling plots");
        perms.registerPermission(PERM_MODS, DefaultPermissionLevel.OP, "Allows managing plot administrators");

        perms.registerPermission(PERM_LIST, DefaultPermissionLevel.ALL, "Allows listing plots");
        perms.registerPermission(PERM_LIST_ALL, DefaultPermissionLevel.OP, "List all plots");
        perms.registerPermission(PERM_LIST_OWN, DefaultPermissionLevel.ALL, "List own plots");
        perms.registerPermission(PERM_LIST_SALE, DefaultPermissionLevel.ALL, "List plots open for sale");

        perms.registerPermission(PERM_SET + ".*", DefaultPermissionLevel.OP, "Control plot settings");

        perms.registerPermission(PERM_PERMS, DefaultPermissionLevel.OP, "Control plot settings");
        perms.registerPermission(PERM_PERMS_BUILD, DefaultPermissionLevel.OP, "Control build permissions");
        perms.registerPermission(PERM_PERMS_USE, DefaultPermissionLevel.OP, "Control item usage permissions");
        perms.registerPermission(PERM_PERMS_INTERACT, DefaultPermissionLevel.OP, "Control interaction permissions");
        perms.registerPermission(PERM_PERMS_CHEST, DefaultPermissionLevel.OP, "Control chest permissions");

        root.setGroupPermission(GROUP_PLOT_OWNER, PERM_SET + ".*", true);
        root.setGroupPermission(GROUP_PLOT_OWNER, PERM_SELL, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, PERM_PERMS, true);
        root.setGroupPermission(GROUP_PLOT_OWNER, PERM_MODS, true);

        CommandFeSettings.addAlias(CATEGORY, "price", PERM_PRICE);
        CommandFeSettings.addAlias(CATEGORY, "limit.count", PERM_LIMIT_COUNT);
        CommandFeSettings.addAlias(CATEGORY, "limit.size", PERM_LIMIT_SIZE);
        CommandFeSettings.addAlias(CATEGORY, "columnMode", PERM_COLUMN);
        CommandFeSettings.addAlias(CATEGORY, "size.min", PERM_SIZE_MIN);
        CommandFeSettings.addAlias(CATEGORY, "size.max", PERM_SIZE_MAX);
    }

    public static class PlotRedefinedException extends Exception
    {
        /* */
    }

}
