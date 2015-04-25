package com.forgeessentials.economy.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.questioner.Questioner.IReplyHandler;

// Sells a plot. There must already be an existing offer made by another player.
public class TransactionHandler implements IReplyHandler
{

    private Offer<AreaZone> offer;

    public TransactionHandler(Offer<AreaZone> offer)
    {
        this.offer = offer;
    }

    @Override
    public void replyReceived(boolean status)
    {
        if (!status)
        {
            OutputHandler.chatNotification(offer.buyer, "The seller declined to sell plot " + offer.item.getName() + " to you. Transaction cancelled.");
            OutputHandler.chatNotification(offer.seller, "Transaction cancelled.");
            PlotManager.pendingOffers.remove(offer.item.getName());
        }
        else
        {
            OutputHandler.chatNotification(offer.buyer, "The seller agreed to sell plot " + offer.item.getName() + " to you. " + offer.price
                    + " will be deducted from your wallet.");

            AreaZone plot = offer.item;
            APIRegistry.getFEEventBus().post(new PlotEvent.OwnerUnset(plot, offer.seller));
            
            if (!APIRegistry.economy.getWallet(offer.buyer).withdraw(offer.price))
            {
                OutputHandler.chatError(offer.buyer, Translator.format("You do not have enough %s to buy this plot", APIRegistry.economy.currency(2)));
                return;
            }
            APIRegistry.economy.getWallet(offer.seller).add(offer.price);
            
            plot.setGroupPermissionProperty(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER, offer.buyer.getPersistentID().toString());
            OutputHandler.chatNotification(offer.seller, "Transaction complete. " + offer.price + "added to your wallet.");
            OutputHandler.chatNotification(offer.buyer, "Transaction complete. You are now owner of " + plot.getName());
            PlotManager.pendingOffers.remove(offer.item.getName());

        }
    }

}
