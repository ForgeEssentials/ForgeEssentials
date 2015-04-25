package com.forgeessentials.economy.plots.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.questioner.QuestionData;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.Questioner.IReplyHandler;

// This class only allows people to offer to buy plots. Actual transaction is done in CommandSellPlot.
public class CommandBuyPlot extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "buyplot";
    }

    @Override
    public String getPermissionNode()
    {
        return PlotManager.PERM_COMMAND + ".buy";
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

    @Override
    public void parse(final CommandParserArgs arguments)
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
        final long price = buyPrice;
        final String priceStr = APIRegistry.economy.toString(price);
        final Wallet buyerWallet = APIRegistry.economy.getWallet(arguments.userIdent);
        final Wallet sellerWallet = APIRegistry.economy.getWallet(plot.getOwner());

        if (sellPrice < 0 || sellPrice > buyPrice)
        {
            String message = Translator.format("Player %s wants to buy your plot \"%s\" for %s.", //
                    arguments.senderPlayer.getCommandSenderName(), plot.getName(), APIRegistry.economy.toString(buyPrice));
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
                        if (!buyerWallet.withdraw(price))
                        {
                            arguments.error(Translator.translate("You can't afford that"));
                            return;
                        }
                        sellerWallet.add(price);

                        arguments.confirm(Translator.format("%s sold plot \"%s\" to you for %s", //
                                plot.getOwner().getUsernameOrUUID(), plot.getName(), priceStr));
                        OutputHandler.chatConfirmation(plot.getOwner().getPlayer(), Translator.format("You sold plot \"%s\" to %s for %s", //
                                plot.getName(), arguments.senderPlayer.getCommandSenderName(), priceStr));
                        plot.setOwner(arguments.userIdent);
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
            if (!buyerWallet.withdraw(price))
            {
                arguments.error(Translator.translate("You can't afford that"));
                return;
            }
            sellerWallet.add(price);
            plot.setOwner(arguments.userIdent);
        }
    }
}
