package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.economy.Offer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.PlotManager;
import com.forgeessentials.util.OutputHandler;

// Sells a plot. There must already be an existing offer made by another player.
public class CommandSellPlot extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommandPlayer(EntityPlayerMP seller, String[] args)
    {
        if (args.length != 2)
        {
            Offer<Zone> offer = PlotManager.pendingOffers.get(args[0]);

            //OutputHandler.chatError(seller, "Improper syntax. Try <plotName> <yes|no|view>");
            if (args[1].equals("view"))
            {
                OutputHandler.chatNotification(seller, "Player " + offer.buyer.getDisplayName() + " offered to purchase plot " + offer.item.getName()
                        + "for " + offer.price + ". Type /sellplot <plotName> yes to accept, /sellplot <plotName> no to deny.");

            }
            else if (args[1].equals("no"))
            {
                OutputHandler.chatNotification(offer.buyer, "The seller declined to sell plot " + offer.item.getName() + " to you. Transaction cancelled.");
                OutputHandler.chatNotification(seller, "Transaction cancelled.");
                PlotManager.pendingOffers.remove(args[0]);
            }
            else if (args[1].equals("yes"))
            {
                OutputHandler.chatNotification(offer.buyer, "The seller agreed to sell plot " + offer.item.getName() + " to you. " + offer.price + " will be deducted from your wallet.");
                APIRegistry.wallet.removeFromWallet(offer.price, offer.buyer.getPersistentID());
                APIRegistry.wallet.addToWallet(offer.price, seller.getPersistentID());
                Zone plot = offer.item;
                plot.setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_OWNER, offer.buyer.getPersistentID().toString());
                OutputHandler.chatNotification(seller, "Transaction complete. " + offer.price + "added to your wallet.");
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
