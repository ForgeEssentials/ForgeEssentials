package com.forgeessentials.economy.plots.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.Plot.PlotRedefinedException;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.questioner.QuestionData;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.Questioner.IReplyHandler;
import com.forgeessentials.util.selections.SelectionHandler;

public class CommandPlot extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "plot";
    }

    @Override
    public String getPermissionNode()
    {
        return PlotManager.PERM_COMMAND;
    }

    @Override
    public PermissionsManager.RegisteredPermValue getDefaultPermission()
    {
        return PermissionsManager.RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/buyplot [amount]: Offer to buy the plot you are standing in. Owner needs to approve the transaction if plot not open for sale.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    public static final String[] mainCommands = new String[] { "define", "claim", "select", "list", "own", "market", "buysel", "limits", "buy", "sell", };

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.translate("/plot list: List all plots"));
            arguments.confirm(Translator.translate("/plot own: List own plots"));
            arguments.confirm(Translator.translate("/plot market: List plots open for sale"));
            arguments.confirm(Translator.translate("/plot buysel: Buy the selected plot from the server"));
            arguments.confirm(Translator.translate("/plot limits: Show your plot limits"));
            arguments.confirm(Translator
                    .translate("/plot buy [amount]: Buy the plot you are standing in. Owner needs to approve the transaction if plot is not open for sale"));
            arguments.confirm(Translator.translate("/plot sell <amount> [player]: Open the plot for sale or sell it to a player"));
            return;
        }

        if (arguments.tabComplete(mainCommands))
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
        case "select":
            parseSelect(arguments);
            break;
        case "own":
            arguments.error("Not yet implemented");
            break;
        case "market":
            arguments.error("Not yet implemented");
            break;
        case "buysel":
            arguments.error("Not yet implemented");
            break;
        case "limits":
            parseLimits(arguments);
            break;
        case "buy":
            parseBuy(arguments);
            break;
        case "sell":
            arguments.error("Not yet implemented");
            break;
        default:
            break;
        }
    }

    public static void parseDefine(CommandParserArgs arguments)
    {
        arguments.checkPermission(PlotManager.PERM_DEFINE);
        arguments.requirePlayer();

        if (arguments.isTabCompletion)
            return;

        Selection selection = SelectionHandler.selectionProvider.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        try
        {
            Plot.define(selection, arguments.userIdent);
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
        arguments.checkPermission(PlotManager.PERM_DEFINE);
        arguments.requirePlayer();

        if (arguments.isTabCompletion)
            return;

        Selection selection = SelectionHandler.selectionProvider.getSelection(arguments.senderPlayer);
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        long price = Plot.getCalculatedPrice(selection);
        Wallet wallet = APIRegistry.economy.getWallet(arguments.userIdent);
        if (!wallet.covers(price))
            throw new ModuleEconomy.CantAffordException();

        try
        {
            Plot.define(selection, arguments.userIdent);
            wallet.withdraw(price);
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

    public void parseSelect(CommandParserArgs arguments)
    {
        Plot plot = Plot.getPlot(new WorldPoint(arguments.senderPlayer));
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position");

        SelectionHandler.selectionProvider.select(arguments.senderPlayer, plot.getZone().getWorldZone().getDimensionID(), plot.getZone().getArea());
        arguments.confirm("Selected plot");
    }

    public static void parseList(CommandParserArgs arguments)
    {
        arguments.error("Not yet implemented");
    }

    public static void parseLimits(CommandParserArgs arguments)
    {
        String limitCount = APIRegistry.perms.getUserPermissionProperty(arguments.userIdent, PlotManager.PERM_LIMIT_COUNT);
        if (limitCount == null || limitCount.isEmpty())
            limitCount = "infinite";

        String limitSize = APIRegistry.perms.getUserPermissionProperty(arguments.userIdent, PlotManager.PERM_LIMIT_SIZE);
        if (limitSize == null || limitSize.isEmpty())
            limitSize = "infinite";

        int usedCount = 0;
        long usedSize = 0;
        for (Plot plot : Plot.getPlots())
            if (arguments.userIdent.equals(plot.getOwner()))
            {
                usedCount++;
                usedSize += plot.getAccountedSize();
            }

        arguments.confirm(Translator.format("You use %d of %s allowed plot count.", usedCount, limitCount));
        arguments.confirm(Translator.format("You use %d of %s allowed plot size.", usedSize, limitSize));
    }

    public static void parseBuy(final CommandParserArgs arguments)
    {
        final Plot plot = Plot.getPlot(new WorldPoint(arguments.senderPlayer));
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position");

        final long plotPrice = plot.getCalculatedPrice();
        final long sellPrice = plot.getPrice();

        long buyPrice = 0;
        if (!arguments.isEmpty())
        {
            buyPrice = arguments.parseLong();
            if (sellPrice >= 0 && sellPrice < buyPrice)
                buyPrice = sellPrice;
        }
        else
        {
            if (sellPrice < plotPrice)
                buyPrice = sellPrice;
            else
                buyPrice = plotPrice;
            arguments.notify(Translator.format("No price specified. Using listed plot price of %s.", APIRegistry.economy.toString(buyPrice)));
        }

        if (sellPrice < 0 || sellPrice > buyPrice)
        {
            final long price = buyPrice;
            final String priceStr = APIRegistry.economy.toString(buyPrice);

            String message = Translator.format("Player %s wants to buy your plot \"%s\" for %s.", //
                    arguments.senderPlayer.getCommandSenderName(), plot.getName(), priceStr);
            if (buyPrice < sellPrice)
                message += " §c" + Translator.format("This is below the price of %s you set up!", APIRegistry.economy.toString(sellPrice));
            if (buyPrice < plotPrice)
                message += " §c" + Translator.format("This is below the plots value of %s!", APIRegistry.economy.toString(sellPrice));
            message += " " + Translator.format("Type /yes to accept or /no to decline (timeout: %d).", PlotManager.timeout);

            IReplyHandler handler = new IReplyHandler() {
                @Override
                public void replyReceived(boolean ok)
                {
                    if (ok)
                    {
                        buyPlot(arguments, plot, price);
                    }
                    else
                    {
                        arguments.error(Translator.format("%s declined to sell you plot \"%s\" for %s", //
                                plot.getOwner().getUsernameOrUUID(), plot.getName(), priceStr));
                    }
                }
            };
            Questioner.addToQuestionQueue(new QuestionData(plot.getOwner().getPlayer(), message, handler, PlotManager.timeout));
        }
        else
        {
            buyPlot(arguments, plot, buyPrice);
        }
    }

    public static void buyPlot(CommandParserArgs arguments, Plot plot, long price)
    {
        String priceStr = APIRegistry.economy.toString(price);
        Wallet buyerWallet = APIRegistry.economy.getWallet(arguments.userIdent);
        if (!buyerWallet.withdraw(price))
        {
            arguments.error(Translator.translate("You can't afford that"));
            return;
        }
        if (plot.hasOwner())
        {
            Wallet sellerWallet = APIRegistry.economy.getWallet(plot.getOwner());
            sellerWallet.add(price);
            OutputHandler.chatConfirmation(plot.getOwner().getPlayer(), Translator.format("You sold plot \"%s\" to %s for %s", //
                    plot.getName(), arguments.senderPlayer.getCommandSenderName(), priceStr));
            arguments.confirm(Translator.format("%s sold plot \"%s\" to you for %s", //
                    plot.getOwner().getUsernameOrUUID(), plot.getName(), priceStr));
            ModuleEconomy.confirmNewWalletAmount(arguments.userIdent, buyerWallet);
        }
        else
        {
            arguments.confirm(Translator.format("You bought plot \"%s\" from the server for %s", plot.getName(), priceStr));
            ModuleEconomy.confirmNewWalletAmount(arguments.userIdent, buyerWallet);
        }
        plot.setOwner(arguments.userIdent);
    }

}
