package com.forgeessentials.economy.plots.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
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
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;
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
            return null;
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
    public PermissionsManager.RegisteredPermValue getDefaultPermission()
    {
        return PermissionsManager.RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/buyplot [amount]: Offer to buy the plot you are standing in. Owner needs to approve the transaction if plot not up for sale.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    public static final String[] completeMain = new String[] { "define", "claim", "list", "select", "set", "perms", "userperms", "mods", "users", "limits",
            "buy", "sell", };
    public static final String[] completeSet = new String[] { "price", "fee", };
    public static final String[] completePerms = new String[] { "build", "interact", "use", "chest", "button", "lever", "door", "animal" };
    public static final String[] completeTrueFalse = new String[] { "yes", "no", "true", "false", "allow", "deny", };

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_LIST))
                arguments.confirm(Translator.translate("/plot list [own|sale|all]: List plots"));
            if (arguments.hasPermission(Plot.PERM_DEFINE))
                arguments.confirm(Translator.translate("/plot define: Define selection as plot"));
            if (arguments.hasPermission(Plot.PERM_CLAIM))
                arguments.confirm(Translator.translate("/plot claim: Buy your selected area as plot"));
            arguments.confirm(Translator.translate("/plot limits: Show your plot limits"));
            if (arguments.hasPermission(Plot.PERM_SET))
                arguments.confirm(Translator.translate("/plot set: Control plot settings"));
            if (arguments.hasPermission(Plot.PERM_PERMS))
                arguments.confirm(Translator.translate("/plot perms: Control plot permissions"));
            arguments.confirm(Translator
                    .translate("/plot buy [amount]: Buy the plot you are standing in. Owner needs to approve the transaction if plot is not up for sale"));
            return;
        }

        if (arguments.tabComplete(completeMain))
            return;

        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "define":
            parseDefine(arguments);
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
            arguments.error("Not yet implemented");
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND);
        }
    }

    public static void parseDefine(CommandParserArgs arguments)
    {
        arguments.checkPermission(Plot.PERM_DEFINE);
        arguments.requirePlayer();

        if (arguments.isTabCompletion)
            return;

        Selection selection = SelectionHandler.selectionProvider.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        try
        {
            Plot.define(selection, arguments.ident);
            arguments.confirm(Translator.translate("Plot created!"));
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

    public static void parseClaim(CommandParserArgs arguments)
    {
        arguments.checkPermission(Plot.PERM_CLAIM);
        arguments.requirePlayer();

        if (arguments.isTabCompletion)
            return;

        Selection selection = SelectionHandler.selectionProvider.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        long price = Plot.getCalculatedPrice(selection);
        Wallet wallet = APIRegistry.economy.getWallet(arguments.ident);
        if (!wallet.covers(price))
            throw new ModuleEconomy.CantAffordException();

        try
        {
            Plot.define(selection, arguments.ident);
            wallet.withdraw(price);
            arguments.confirm(Translator.format("Plot created for %s!", APIRegistry.economy.toString(price)));
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

    public static void parseList(final CommandParserArgs arguments)
    {
        arguments.checkPermission(Plot.PERM_LIST);

        PlotListingType listType = PlotListingType.OWN;
        if (!arguments.isEmpty())
        {
            if (arguments.tabComplete(PlotListingType.stringValues()))
                return;
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

        final WorldPoint playerRef = arguments.senderPlayer != null ? new WorldPoint(arguments.senderPlayer).setY(0) : new WorldPoint(0, 0, 0, 0);
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

    public static void parseLimits(CommandParserArgs arguments)
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

        arguments.confirm(Translator.format("You use %d of %s allowed plot count.", usedCount, limitCount));
        arguments.confirm(Translator.format("You use %d of %s allowed plot size.", usedSize, limitSize));
    }

    public static void parseSelect(CommandParserArgs arguments)
    {
        Plot plot = getPlot(arguments.senderPlayer);
        SelectionHandler.selectionProvider.select(arguments.senderPlayer, plot.getDimension(), plot.getZone().getArea());
        arguments.confirm("Selected plot");
    }

    public static void parseMods(CommandParserArgs arguments, boolean modifyUsers)
    {
        Plot plot = getPlot(arguments.senderPlayer);
        String type = modifyUsers ? "users" : "mods";
        String group = modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD;
        
        arguments.checkPermission(Plot.PERM_MODS);
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.translate("/plot " + type + " add|remove <player>: Add / remove " + type));
            arguments.confirm(Translator.translate("Plot " + type + ":"));
            for (UserIdent user : APIRegistry.perms.getServerZone().getKnownPlayers())
                if (plot.getZone().getStoredPlayerGroups(user).contains(group))
                    arguments.confirm("  " + user.getUsernameOrUUID());
            return;
        }
        arguments.tabComplete("add", "remove");
        String action = arguments.remove().toLowerCase();

        UserIdent player = arguments.parsePlayer(true);
        if (arguments.isTabCompletion || player == null)
            return;

        switch (action)
        {
        case "add":
            plot.getZone().addPlayerToGroup(player, modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD);
            arguments.confirm(Translator.format("Added %s to plot " + type, player.getUsernameOrUUID()));
            break;
        case "remove":
            plot.getZone().removePlayerFromGroup(player, modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD);
            arguments.confirm(Translator.format("Removed %s from plot " + type, player.getUsernameOrUUID()));
            break;
        default:
            throw new TranslatedCommandException.InvalidSyntaxException();
        }
    }

    public static void parseSet(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            if (arguments.hasPermission(Plot.PERM_SET_PRICE))
                arguments.confirm(Translator.translate("/plot set price <amount>"));
            if (arguments.hasPermission(Plot.PERM_SET_FEE))
                arguments.confirm(Translator.translate("/plot set fee <amount> <timeout>"));
            return;
        }

        if (arguments.tabComplete(completeSet))
            return;

        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "price":
            parseSetPrice(arguments);
            break;
        case "fee":
            parseSetFee(arguments);
            break;
        default:
            break;
        }
    }

    public static void parseSetPrice(CommandParserArgs arguments)
    {
        Plot plot = getPlot(arguments.senderPlayer);
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.format("Current plot price: %s", APIRegistry.economy.toString(plot.getPrice())));
            if (arguments.hasPermission(Plot.PERM_SET_PRICE))
            {
                arguments.confirm(Translator.translate("/plot set price <amount>: Offer plot for sale"));
                arguments.confirm(Translator.translate("/plot set price clear: Remove plot from sale"));
            }
            return;
        }
        arguments.checkPermission(Plot.PERM_SET_PRICE);

        if (arguments.isTabCompletion && arguments.size() == 1)
        {
            arguments.tabCompletion.add("clear");
            return;
        }

        String priceStr = arguments.remove().toLowerCase();
        int price = -1;
        if (!priceStr.equals("clear"))
            price = parseInt(arguments.sender, priceStr);

        if (arguments.isTabCompletion)
            return;

        if (price >= 0)
        {
            plot.setPrice(price);
            arguments.confirm(Translator.format("Put up plot for sale for %s", APIRegistry.economy.toString(price)));
        }
        else
        {
            plot.setPrice(-1);
            arguments.confirm(Translator.translate("Removed plot from sale"));
        }
    }

    public static void parseSetFee(CommandParserArgs arguments)
    {
        Plot plot = getPlot(arguments.senderPlayer);
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.format("Current plot fee: %s", APIRegistry.economy.toString(plot.getFee())));
            if (arguments.hasPermission(Plot.PERM_SET_FEE))
                arguments.confirm(Translator.translate("/plot set fee <amount> <timeout>: Set fee"));
            return;
        }
        arguments.checkPermission(Plot.PERM_SET_FEE);

        int amount = arguments.parseInt();
        int timeout = arguments.parseInt();

        if (arguments.isTabCompletion)
            return;
        plot.setFee(amount);
        plot.setFeeTimeout(timeout);
    }

    public static void parsePerms(CommandParserArgs arguments, boolean userPerms)
    {
        arguments.checkPermission(Plot.PERM_PERMS);
        Plot plot = getPlot(arguments.senderPlayer);
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.translate("/plot perms <type> true|false: Control what other players can do in a plot"));
            arguments.confirm(Translator.format("Possible perms: %s", StringUtils.join(completePerms, ", ")));
            return;
        }

        if (arguments.tabComplete(completePerms))
            return;
        String perm = arguments.remove().toLowerCase();

        if (arguments.tabComplete(completeTrueFalse))
            return;
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
            arguments.confirm(Translator.translate(msgBase + "to build"));
            break;
        case "use":
            plot.setPermission(ModuleProtection.PERM_USE + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to use items"));
            break;
        case "interact":
            plot.setPermission(ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to interact with objects"));
            break;
        case "chest":
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.trapped_chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.trapped_chest, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to interact with chests"));
            break;
        case "button":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.wooden_button, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.stone_button, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to interact with buttons"));
            break;
        case "lever":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.lever, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to interact with levers"));
            break;
        case "door":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.wooden_door, 0) + Zone.ALL_PERMS, userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to interact with doors"));
            break;
        case "animal":
            plot.setPermission(MobType.PASSIVE.getDamageToPermission(), userPerms, allow);
            plot.setPermission(MobType.TAMED.getDamageToPermission(), userPerms, allow);
            arguments.confirm(Translator.translate(msgBase + "to hurt animals"));
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    public static void parseBuyStart(final CommandParserArgs arguments)
    {
        final Plot plot = Plot.getPlot(new WorldPoint(arguments.senderPlayer));
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position");

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

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
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
                    String message = Translator.format("Player %s wants to buy your plot \"%s\" for %s.", //
                            arguments.senderPlayer.getCommandSenderName(), plot.getName(), buyPriceStr);
                    if (buyPrice < sellPrice && sellPrice >= 0)
                        message += " \u00a7c" + Translator.format("This is below the price of %s you set up!", APIRegistry.economy.toString(sellPrice));
                    if (buyPrice < plotPrice)
                        message += " \u00a7c" + Translator.format("This is below the plots value of %s!", APIRegistry.economy.toString(sellPrice));

                    QuestionerCallback handler = new QuestionerCallback() {
                        @Override
                        public void respond(Boolean response)
                        {
                            if (response == null)
                            {
                                arguments.error(Translator.format("%s did not respond to your buy request", plot.getOwner().getUsernameOrUUID()));
                                return;
                            }
                            else if (response == false)
                            {
                                OutputHandler.chatError(plot.getOwner().getPlayer(), Translator.translate("Trade declined"));
                                arguments.error(Translator.format("%s declined to sell you plot \"%s\" for %s", //
                                        plot.getOwner().getUsernameOrUUID(), plot.getName(), buyPriceStr));
                                return;
                            }
                            buyPlot(arguments, plot, plotPrice);
                        }
                    };
                    try
                    {
                        Questioner.add(plot.getOwner().getPlayer(), message, handler, 60);
                    }
                    catch (QuestionerStillActiveException e)
                    {
                        throw new QuestionerStillActiveException.CommandException();
                    }
                }
                else
                {
                    buyPlot(arguments, plot, buyPrice);
                }
            }
        };
        try
        {
            String message = Translator.format("Really buy this plot for %s", buyPriceStr);
            Questioner.add(arguments.sender, message, handler, 30);
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

    public static void buyPlot(CommandParserArgs arguments, Plot plot, long price)
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
                OutputHandler.chatConfirmation(plot.getOwner().getPlayer(), Translator.format("You sold plot \"%s\" to %s for %s", //
                        plot.getName(), arguments.senderPlayer.getCommandSenderName(), priceStr));
                ModuleEconomy.confirmNewWalletAmount(plot.getOwner(), sellerWallet);
            }
            arguments.confirm(Translator.format("%s sold plot \"%s\" to you for %s", //
                    plot.getOwner().getUsernameOrUUID(), plot.getName(), priceStr));
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

    public static Plot getPlot(EntityPlayerMP player)
    {
        Plot plot = Plot.getPlot(new WorldPoint(player));
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position");
        return plot;
    }

}
