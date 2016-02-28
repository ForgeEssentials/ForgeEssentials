package com.forgeessentials.economy.plots.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.Plot.PlotRedefinedException;
import com.forgeessentials.protection.MobType;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandPlot extends ParserCommandBase
{

    public static enum PlotListingType
    {
        ALL, OWN, SALE;

        public boolean check(ICommandSender sender, Plot plot)
        {
            switch (this)
            {
            case ALL:
                return true;
            case OWN:
                if (plot.getOwner() == null)
                    return true;
                if (!(sender instanceof EntityPlayerMP))
                    return false;
                return plot.getOwner().equals(sender);
            case SALE:
                return plot.isForSale();
            default:
                break;
            }
            return false;
        }

        public static Collection<String> stringValues()
        {
            List<String> values = new ArrayList<>();
            for (PlotListingType type : values())
                values.add(type.toString().toLowerCase());
            return values;
        }

    }

    @Override
    public String getCommandName()
    {
        return "plot";
    }

    @Override
    public String getPermissionNode()
    {
        return Plot.PERM_COMMAND;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/plot Manage plots.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_LIST))
                arguments.confirm("/plot list [own|sale|all]: List plots");
            if (arguments.hasPermission(Plot.PERM_DEFINE))
                arguments.confirm("/plot define: Define selection as plot");
            if (arguments.hasPermission(Plot.PERM_CLAIM))
                arguments.confirm("/plot claim: Buy your selected area as plot");
            arguments.confirm("/plot limits: Show your plot limits");
            if (arguments.hasPermission(Plot.PERM_SET))
                arguments.confirm("/plot set: Control plot settings");
            if (arguments.hasPermission(Plot.PERM_PERMS))
                arguments.confirm("/plot perms: Control plot permissions");
            arguments.confirm(Translator
                    .translate("/plot buy [amount]: Buy the plot you are standing in. Owner needs to approve the transaction if plot is not up for sale"));
            return;
        }

        arguments.tabComplete("define", "claim", "list", "select", "set", "perms", "userperms", "mods", "users", "limits", "buy", "sell", "delete");
        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "define":
            parseDefine(arguments);
            break;
        case "delete":
            parseDelete(arguments);
            break;
        case "claim":
            parseClaim(arguments);
            break;
        case "list":
            parseList(arguments);
            break;
        case "limits":
            parseLimits(arguments);
            break;
        case "select":
            parseSelect(arguments);
            break;
        case "mods":
            parseMods(arguments, false);
            break;
        case "users":
            parseMods(arguments, true);
            break;
        case "set":
            parseSet(arguments);
            break;
        case "perms":
            parsePerms(arguments, false);
            break;
        case "userperms":
            parsePerms(arguments, true);
            break;
        case "buy":
            parseBuyStart(arguments);
            break;
        case "sell":
            throw new TranslatedCommandException("Not yet implemented. Use \"/plot set price\" instead.");
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subcmd);
        }
    }

    public static void parseDefine(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(Plot.PERM_DEFINE);
        arguments.requirePlayer();

        if (arguments.isTabCompletion)
            return;

        Selection selection = SelectionHandler.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        try
        {
            Plot.define(selection, arguments.ident);
            arguments.confirm("Plot created!");
        }
        catch (PlotRedefinedException e)
        {
            throw new TranslatedCommandException("There is already a plot defined in this area");
        }
        catch (EventCancelledException e)
        {
            throw new TranslatedCommandException("Plot creation cancelled");
        }
    }

    public static void parseDelete(CommandParserArgs arguments) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        if (plot.getOwner() != UserIdent.get(arguments.senderPlayer) || arguments.hasPermission(Plot.PERM_DELETE))
        {
            arguments.confirm("Plot \"%s\" has been deleted.", plot.getNameNotNull());
            Plot.deletePlot(plot);
        }
        else
            throw new TranslatedCommandException("You are not the owner of this plot, you can't delete it!");
    }

    public static void parseClaim(final CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(Plot.PERM_CLAIM);
        arguments.requirePlayer();

        if (arguments.isTabCompletion)
            return;

        final Selection selection = SelectionHandler.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        final long price = Plot.getCalculatedPrice(selection);

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                {
                    arguments.error("Claim request timed out");
                    return;
                }
                if (response == false)
                {
                    arguments.error("Canceled");
                    return;
                }
                try
                {
                    Wallet wallet = APIRegistry.economy.getWallet(arguments.ident);
                    if (!wallet.covers(price))
                        throw new ModuleEconomy.CantAffordException();

                    checkLimits(arguments, selection);

                    try
                    {
                        Plot.define(selection, arguments.ident);
                        wallet.withdraw(price);
                        arguments.confirm("Plot created for %s!", APIRegistry.economy.toString(price));
                    }
                    catch (PlotRedefinedException e)
                    {
                        throw new TranslatedCommandException("There is already a plot defined in this area");
                    }
                    catch (EventCancelledException e)
                    {
                        throw new TranslatedCommandException("Plot creation cancelled");
                    }
                }
                catch (CommandException e)
                {
                    arguments.error(e.getMessage());
                }
            }
        };
        if (arguments.sender instanceof DoAsCommandSender)
        {
            handler.respond(true);
            return;
        }
        String message = Translator.format("Really claim this plot for %s", APIRegistry.economy.toString(price));
        Questioner.addChecked(arguments.sender, message, handler, 30);

    }

    private static void checkLimits(CommandParserArgs arguments, WorldArea newArea) throws CommandException
    {
        int plotSize = newArea.getXLength() * newArea.getZLength() * (Plot.isColumnMode(newArea.getDimension()) ? 1 : newArea.getYLength());

        int minAxis = ServerUtil.parseIntDefault(APIRegistry.perms.getGlobalPermissionProperty(Plot.PERM_SIZE_MIN), Integer.MIN_VALUE);
        int maxAxis = ServerUtil.parseIntDefault(APIRegistry.perms.getGlobalPermissionProperty(Plot.PERM_SIZE_MAX), Integer.MAX_VALUE);

        if (newArea.getXLength() < minAxis || newArea.getZLength() < minAxis)
        {
            throw new TranslatedCommandException("Plot is too small!");
        }

        if (newArea.getXLength() > maxAxis || newArea.getZLength() > maxAxis)
        {
            throw new TranslatedCommandException("Plot is too big!");
        }

        int limitCount = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(arguments.ident, Plot.PERM_LIMIT_COUNT), Integer.MAX_VALUE);
        int limitSize = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(arguments.ident, Plot.PERM_LIMIT_SIZE), Integer.MAX_VALUE);
        int usedCount = 0;
        long usedSize = 0;
        for (Plot plot : Plot.getPlots())
            if (arguments.ident.equals(plot.getOwner()))
            {
                usedCount++;
                usedSize += plot.getAccountedSize();
            }
        if (usedCount + 1 > limitCount)
            throw new TranslatedCommandException("You have reached your limit of %s plots already!", limitCount);
        if (usedSize + plotSize > limitSize)
            throw new TranslatedCommandException("You have reached your limit of %s blocks^2 already!", limitSize);
    }

    public static void parseList(final CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(Plot.PERM_LIST);

        PlotListingType listType = PlotListingType.OWN;
        if (!arguments.isEmpty())
        {
            arguments.tabComplete(PlotListingType.stringValues());
            try
            {
                listType = PlotListingType.valueOf(arguments.remove().toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
            }
        }

        if (arguments.isTabCompletion)
            return;

        final WorldPoint playerRef = arguments.senderPlayer != null ? arguments.getSenderPoint().setY(0) : new WorldPoint(0, 0, 0, 0);
        SortedSet<Plot> plots = new TreeSet<Plot>(new Comparator<Plot>() {
            @Override
            public int compare(Plot a, Plot b)
            {
                if (a.getDimension() != playerRef.getDimension())
                {
                    if (b.getDimension() == playerRef.getDimension())
                        return 1;
                }
                else
                {
                    if (b.getDimension() != playerRef.getDimension())
                        return -1;
                }
                double aDist = a.getZone().getArea().getCenter().setY(0).distance(playerRef);
                double bDist = b.getZone().getArea().getCenter().setY(0).distance(playerRef);
                return (int) Math.signum(aDist - bDist);
            }
        });

        for (Plot plot : Plot.getPlots())
            if (listType.check(arguments.sender, plot))
                plots.add(plot);

        arguments.confirm(Translator.translate("Listing " + listType.toString().toLowerCase() + " plots:"));
        for (Plot plot : plots)
            plot.printInfo(arguments.sender);
    }

    public static void parseLimits(CommandParserArgs arguments) throws CommandException
    {
        String limitCount = APIRegistry.perms.getUserPermissionProperty(arguments.ident, Plot.PERM_LIMIT_COUNT);
        if (limitCount == null || limitCount.isEmpty())
            limitCount = "infinite";

        String limitSize = APIRegistry.perms.getUserPermissionProperty(arguments.ident, Plot.PERM_LIMIT_SIZE);
        if (limitSize == null || limitSize.isEmpty())
            limitSize = "infinite";

        int usedCount = 0;
        long usedSize = 0;
        for (Plot plot : Plot.getPlots())
            if (arguments.ident.equals(plot.getOwner()))
            {
                usedCount++;
                usedSize += plot.getAccountedSize();
            }

        arguments.confirm("You use %d of %s allowed plot count.", usedCount, limitCount);
        arguments.confirm("You use %d of %s allowed plot size.", usedSize, limitSize);
    }

    public static void parseSelect(CommandParserArgs arguments) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        SelectionHandler.select(arguments.senderPlayer, plot.getDimension(), plot.getZone().getArea());
        arguments.confirm("Selected plot");
    }

    public static void parseMods(CommandParserArgs arguments, boolean modifyUsers) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        String type = modifyUsers ? "users" : "mods";
        String group = modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD;

        arguments.checkPermission(Plot.PERM_MODS);
        if (arguments.isEmpty())
        {
            arguments.confirm("/plot " + type + " add|remove <player>: Add / remove " + type);
            arguments.confirm("Plot " + type + ":");
            for (UserIdent user : APIRegistry.perms.getServerZone().getKnownPlayers())
                if (plot.getZone().getStoredPlayerGroups(user).contains(group))
                    arguments.confirm("  " + user.getUsernameOrUuid());
            return;
        }
        arguments.tabComplete("add", "remove");
        String action = arguments.remove().toLowerCase();

        UserIdent player = arguments.parsePlayer(true, false);
        if (arguments.isTabCompletion)
            return;

        switch (action)
        {
        case "add":
            plot.getZone().addPlayerToGroup(player, modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD);
            arguments.confirm("Added %s to plot " + type, player.getUsernameOrUuid());
            break;
        case "remove":
            plot.getZone().removePlayerFromGroup(player, modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD);
            arguments.confirm("Removed %s from plot " + type, player.getUsernameOrUuid());
            break;
        default:
            throw new TranslatedCommandException.InvalidSyntaxException();
        }
    }

    public static void parseSet(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_SET_PRICE))
                arguments.confirm("/plot set price: Put up plot for sale");
            if (arguments.hasPermission(Plot.PERM_SET_FEE))
                arguments.confirm(Translator.translate("/plot set fee: Set a fee (WIP)")); // TODO WIP plots
            if (arguments.hasPermission(Plot.PERM_SET_NAME))
                arguments.confirm("/plot set name: Set the plot name");
            return;
        }

        arguments.tabComplete("price", "fee", "name", "owner");
        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "price":
            parseSetPrice(arguments);
            break;
        case "fee":
            parseSetFee(arguments);
            break;
        case "name":
            parseSetName(arguments);
            break;
        case "owner":
            parseSetOwner(arguments);
            break;
        default:
            break;
        }
    }

    public static void parseSetPrice(CommandParserArgs arguments) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_SET_PRICE))
            {
                arguments.confirm("/plot set price <amount>: Offer plot for sale");
                arguments.confirm("/plot set price clear: Remove plot from sale");
            }
            long price = plot.getPrice();
            if (price >= 0)
                arguments.confirm("Current plot price: %s", APIRegistry.economy.toString(price));
            else
                arguments.confirm("Current plot is not up for sale");
            return;
        }
        arguments.checkPermission(Plot.PERM_SET_PRICE);

        arguments.tabComplete("clear");
        String priceStr = arguments.remove().toLowerCase();
        int price = -1;
        if (!priceStr.equals("clear"))
            price = parseInt(priceStr);

        if (arguments.isTabCompletion)
            return;

        if (price >= 0)
        {
            plot.setPrice(price);
            arguments.confirm("Put up plot for sale for %s", APIRegistry.economy.toString(price));
        }
        else
        {
            plot.setPrice(-1);
            arguments.confirm("Removed plot from sale");
        }
    }

    public static void parseSetFee(CommandParserArgs arguments) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_SET_FEE))
                arguments.confirm(Translator.translate("/plot set fee <amount> <timeout>: Set fee (WIP)")); // TODO WIP
                                                                                                            // plots
            arguments.confirm("Current plot fee: %s", APIRegistry.economy.toString(plot.getFee()));
            return;
        }
        arguments.checkPermission(Plot.PERM_SET_FEE);

        int amount = arguments.parseInt();
        int timeout = arguments.parseInt();

        if (arguments.isTabCompletion)
            return;
        plot.setFee(amount);
        plot.setFeeTimeout(timeout);
        arguments.confirm(Translator.format("Set plot price to %s and timeout to %d", APIRegistry.economy.toString(amount), timeout));
    }

    public static void parseSetName(CommandParserArgs arguments) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_SET_NAME))
                arguments.confirm("/plot set name <name>: Set plot name");
            String name = APIRegistry.perms.getGroupPermissionProperty(Plot.GROUP_ALL, Plot.PERM_NAME);
            if (name == null || name.isEmpty())
                name = "none";
            arguments.confirm("Current plot name: %s", name);
            return;
        }
        String name = arguments.toString();
        arguments.checkPermission(Plot.PERM_SET_NAME);
        if (arguments.isTabCompletion)
            return;
        plot.getZone().setGroupPermissionProperty(Plot.GROUP_ALL, Plot.PERM_NAME, name);
        arguments.confirm("Set plot name to \"%s\"", name);
    }

    public static void parseSetOwner(CommandParserArgs arguments) throws CommandException
    {
        Plot plot = getPlot(arguments.sender);
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_SET_OWNER))
            {
                arguments.confirm("/plot set owner <player>: Set plot owner");
                arguments.confirm("/plot set owner " + APIRegistry.IDENT_SERVER.getUsernameOrUuid() + ": Set plot owner to server");
            }
            UserIdent owner = plot.getOwner();
            if (owner == null)
                owner = APIRegistry.IDENT_SERVER;
            arguments.confirm("Current plot owner: %s", owner.getUsernameOrUuid());
            return;
        }
        UserIdent newOwner = arguments.parsePlayer(true, false);
        arguments.checkPermission(Plot.PERM_SET_OWNER);
        if (arguments.isTabCompletion)
            return;
        plot.setOwner(newOwner);
        arguments.confirm("Set plot owner to \"%s\"", newOwner.getUsernameOrUuid());
    }

    public static void parsePerms(CommandParserArgs arguments, boolean userPerms) throws CommandException
    {
        final String[] tabCompletion = new String[] { "build", "interact", "use", "chest", "button", "lever", "door", "animal" };

        arguments.checkPermission(Plot.PERM_PERMS);
        Plot plot = getPlot(arguments.sender);
        if (arguments.isEmpty())
        {
            arguments.confirm("/plot perms <type> true|false: Control what other players can do in a plot");
            arguments.confirm("Possible perms: %s", StringUtils.join(tabCompletion, ", "));
            return;
        }

        arguments.tabComplete(tabCompletion);
        String perm = arguments.remove().toLowerCase();

        arguments.tabComplete("yes", "no", "true", "false", "allow", "deny");
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing argument");
        String allowDeny = arguments.remove().toLowerCase();

        boolean allow;
        switch (allowDeny)
        {
        case "yes":
        case "true":
        case "allow":
            allow = true;
            break;
        case "no":
        case "false":
        case "deny":
            allow = false;
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }

        String msgBase = (allow ? "Allowed " : "Denied ") + (userPerms ? "users " : "guests ");
        switch (perm)
        {
        case "build":
            plot.setPermission(ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(msgBase + "to build");
            break;
        case "use":
            plot.setPermission(ModuleProtection.PERM_USE + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(msgBase + "to use items");
            break;
        case "interact":
            plot.setPermission(ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(msgBase + "to interact with objects");
            break;
        case "chest":
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.trapped_chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.trapped_chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(msgBase + "to interact with chests");
            break;
        case "button":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.wooden_button, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.stone_button, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(msgBase + "to interact with buttons");
            break;
        case "lever":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.lever, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(msgBase + "to interact with levers");
            break;
        case "door":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.oak_door, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to interact with doors"));
            break;
        case "animal":
            plot.setPermission(MobType.PASSIVE.getDamageToPermission(), userPerms, allow);
            plot.setPermission(MobType.TAMED.getDamageToPermission(), userPerms, allow);
            arguments.confirm(msgBase + "to hurt animals");
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    public static void parseBuyStart(final CommandParserArgs arguments) throws CommandException
    {
        final Plot plot = getPlot(arguments.sender);
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position");
        if (plot.getOwner() != null && plot.getOwner().equals(arguments.ident))
            throw new TranslatedCommandException("You already own this plot");

        checkLimits(arguments, plot.getZone().getWorldArea());

        final long plotPrice = plot.getCalculatedPrice();
        final long sellPrice = plot.getPrice();
        final long buyPrice;
        if (!arguments.isEmpty())
        {
            buyPrice = arguments.parseLong();
            if (sellPrice >= 0 && sellPrice < buyPrice)
                arguments.notify(Translator.format("%s is above the plots default price of %s", APIRegistry.economy.toString(buyPrice),
                        APIRegistry.economy.toString(sellPrice)));
        }
        else
        {
            if (sellPrice >= 0)
                buyPrice = sellPrice;
            else
                buyPrice = plotPrice;
        }
        final String buyPriceStr = APIRegistry.economy.toString(buyPrice);

        if (!plot.hasOwner())
        {
            if (sellPrice < 0)
            {
                arguments.error(Translator.format("This plot is not for sale!"));
                return;
            }
            if (buyPrice != sellPrice)
            {
                arguments.error(Translator.format("The fixed price of this plot is %s.", APIRegistry.economy.toString(sellPrice)));
                return;
            }
        }

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response) throws CommandException
            {
                if (response == null)
                {
                    arguments.error("Buy request timed out");
                    return;
                }
                if (response == false)
                {
                    arguments.error("Canceled");
                    return;
                }
                if (sellPrice < 0 || sellPrice > buyPrice)
                {
                    if (sellPrice < 0 && !plot.getOwner().hasPlayer())
                        throw new TranslatedCommandException("You cannot buy this plot because the owner is not online.");
                    String message = Translator.format("Player %s wants to buy your plot \"%s\" for %s.", //
                            arguments.ident.getUsernameOrUuid(), plot.getName(), buyPriceStr);
                    if (buyPrice < sellPrice && sellPrice >= 0)
                        message += " \u00a7c" + Translator.format("This is below the price of %s you set up!", APIRegistry.economy.toString(sellPrice));
                    if (buyPrice < plotPrice)
                        message += " \u00a7c" + Translator.format("This is below the plots value of %s!", APIRegistry.economy.toString(sellPrice));

                    QuestionerCallback handler = new QuestionerCallback() {
                        @Override
                        public void respond(Boolean response) throws CommandException
                        {
                            if (response == null)
                            {
                                arguments.error(Translator.format("%s did not respond to your buy request", plot.getOwner().getUsernameOrUuid()));
                                return;
                            }
                            else if (response == false)
                            {
                                ChatOutputHandler.chatError(plot.getOwner().getPlayerMP(), Translator.translate("Trade declined"));
                                arguments.error(Translator.format("%s declined to sell you plot \"%s\" for %s", //
                                        plot.getOwner().getUsernameOrUuid(), plot.getName(), buyPriceStr));
                                return;
                            }
                            buyPlot(arguments, plot, plotPrice);
                        }
                    };
                    Questioner.addChecked(plot.getOwner().getPlayerMP(), message, handler, 60);
                }
                else
                {
                    buyPlot(arguments, plot, buyPrice);
                }
            }
        };
        if (arguments.sender instanceof DoAsCommandSender)
        {
            handler.respond(true);
            return;
        }
        String message = Translator.format("Really buy this plot for %s", buyPriceStr);
        Questioner.addChecked(arguments.sender, message, handler, 30);
    }

    public static void buyPlot(CommandParserArgs arguments, Plot plot, long price) throws CommandException
    {
        String priceStr = APIRegistry.economy.toString(price);
        Wallet buyerWallet = APIRegistry.economy.getWallet(arguments.ident);
        if (!buyerWallet.withdraw(price))
        {
            arguments.error(Translator.translate("You can't afford that"));
            return;
        }
        if (plot.hasOwner())
        {
            Wallet sellerWallet = APIRegistry.economy.getWallet(plot.getOwner());
            sellerWallet.add(price);
            if (plot.getOwner().hasPlayer())
            {
                ChatOutputHandler.chatConfirmation(plot.getOwner().getPlayerMP(), Translator.format("You sold plot \"%s\" to %s for %s", //
                        plot.getName(), arguments.senderPlayer.getName(), priceStr));
                ModuleEconomy.confirmNewWalletAmount(plot.getOwner(), sellerWallet);
            }
            arguments.confirm(Translator.format("%s sold plot \"%s\" to you for %s", //
                    plot.getOwner().getUsernameOrUuid(), plot.getName(), priceStr));
            ModuleEconomy.confirmNewWalletAmount(arguments.ident, buyerWallet);
        }
        else
        {
            arguments.confirm(Translator.format("You bought plot \"%s\" from the server for %s", plot.getName(), priceStr));
            ModuleEconomy.confirmNewWalletAmount(arguments.ident, buyerWallet);
        }
        plot.setOwner(arguments.ident);
        plot.setPrice(-1);
    }

    public static Plot getPlot(ICommandSender sender) throws CommandException
    {
        Plot plot = Plot.getPlot(new WorldPoint(sender.getEntityWorld(), sender.getPosition()));
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position. You have to stand inside it to use plot commands.");
        return plot;
    }

}
