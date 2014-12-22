package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.economy.Offer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.PlotManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import java.util.UUID;

// This class only allows people to offer to buy plots. Actual transaction is done in CommandSellPlot.
public class CommandBuyPlot extends ForgeEssentialsCommandBase{

    @Override
    public void processCommandPlayer(EntityPlayerMP buyer, String[] args)
    {
        if (args.length >= 1)
        {
            int value;
            Zone plot = APIRegistry.perms.getZoneById(PlotManager.PLOT_NAME_ID + args[0]);
            if (!plot.checkGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_PERM))
            {
                throw new CommandException("No such plot!");
            }
            EntityPlayer seller = UserIdent.getPlayerByUuid(UUID.fromString(plot.getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_OWNER)));
            if (args[1] != null)
            {
                value = Integer.parseInt(args[1]);
            }
            else
            {
                value = Integer.parseInt(plot.getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_VALUE));
                OutputHandler.chatNotification(buyer, "No value specified. Will use current valuation of plot, which is " + value);
            }

            // check if the player can afford it...
            if (!(APIRegistry.wallet.getWallet(new UserIdent(buyer).getUuid()) < value))
            {
                throw new CommandException("You can't afford that!");
            }

            OutputHandler.chatNotification(seller, "Player " + buyer.getDisplayName() + " offered to purchase plot " + plot.getName() + " for " + value
                    + ". Type /sellplot <plotName> yes to accept, /sellplot <plotName> no to deny. This offer will expire in " + PlotManager.timeout + " seconds.");
            PlotManager.pendingOffers.put(plot.getName(), new Offer<Zone>(buyer, seller, plot, value));
        }
        else{
            OutputHandler.chatError(buyer, "Incorrect syntax. Try this instead: <plotName> <amount>");
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
        return "fe.economy.plots.buy";
    }

    @Override
    public PermissionsManager.RegisteredPermValue getDefaultPermission()
    {
        return PermissionsManager.RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandName()
    {
        return "buyplot";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/buyplot <plotName> <amount> Offer to buy a plot. The owner of the plot is required to approve the transaction.";
    }
}
