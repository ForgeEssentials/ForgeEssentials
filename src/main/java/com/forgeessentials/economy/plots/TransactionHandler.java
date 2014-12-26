package com.forgeessentials.economy.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.questioner.QuestionCenter.IReplyHandler;

// Sells a plot. There must already be an existing offer made by another player.
public class TransactionHandler implements IReplyHandler
{
    private Offer<Zone> offer;

    public TransactionHandler(Offer<Zone> offer)
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
            OutputHandler.chatNotification(offer.buyer,
                    "The seller agreed to sell plot " + offer.item.getName() + " to you. " + offer.price + " will be deducted from your wallet.");
            APIRegistry.wallet.removeFromWallet(offer.price, offer.buyer.getPersistentID());
            APIRegistry.wallet.addToWallet(offer.price, offer.seller.getPersistentID());
            Zone plot = offer.item;
            plot.setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_OWNER, offer.buyer.getPersistentID().toString());
            OutputHandler.chatNotification(offer.seller, "Transaction complete. " + offer.price + "added to your wallet.");
            OutputHandler.chatNotification(offer.buyer, "Transaction complete. You are now owner of " + plot.getName());
            PlotManager.pendingOffers.remove(offer.item.getName());

        }
    }
}

