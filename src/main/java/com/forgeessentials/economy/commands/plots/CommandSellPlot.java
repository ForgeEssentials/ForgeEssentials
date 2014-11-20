package com.forgeessentials.economy.commands.plots;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.economy.plots.PlotManager.Offer;
import com.forgeessentials.util.OutputHandler;

// Sells a plot. There must already be an existing offer made by another player.
public class CommandSellPlot extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommandPlayer(EntityPlayerMP seller, String[] args)
    {
        if (args.length != 2)
        {
            Offer offer = PlotManager.pendingOffers.get(args[0]);

            //OutputHandler.chatError(seller, "Improper syntax. Try <plotName> <yes|no|view>");
            if (args[1].equals("view"))
            {
                OutputHandler.chatNotification(seller, "Player " + offer.buyer.getDisplayName() + " offered to purchase plot " + offer.plot.getName()
                        + "for " + offer.amount + ". Type /sellplot <plotName> yes to accept, /sellplot <plotName> no to deny.");

            }
            else if (args[1].equals("no"))
            {
                OutputHandler.chatNotification(offer.buyer, "The seller declined to sell plot " + offer.plot.getName() + " to you. Transaction cancelled.");
                OutputHandler.chatNotification(seller, "Transaction cancelled.");
                PlotManager.pendingOffers.remove(args[0]);
            }
            else if (args[1].equals("yes"))
            {
                OutputHandler.chatNotification(offer.buyer, "The seller agreed to sell plot " + offer.plot.getName() + " to you. " + offer.amount + " will be deducted from your wallet.");
                APIRegistry.wallet.removeFromWallet(offer.amount, offer.buyer.getPersistentID());
                APIRegistry.wallet.addToWallet(offer.amount, seller.getPersistentID());
                Plot plot = offer.plot;
                plot.changeOwner(offer.buyer.getPersistentID());
                PlotManager.addPlot(plot);
                OutputHandler.chatNotification(seller, "Transaction complete. " + offer.amount + "added to your wallet.");
                OutputHandler.chatNotification(offer.buyer, "Transaction complete. You are now owner of " + plot.getName());
                PlotManager.pendingOffers.remove(args[0]);

            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy.plots.sell";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandName()
    {
        return "sellplot";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/sellplot <plotName> <yes|no|view> Sells a plot. There must already be an existing offer made by another player.";
    }
}
