package com.forgeessentials.economy.commands.plots;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.economy.plots.PlotManager.Offer;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

// This class only allows people to offer to buy plots. Actual transaction is done in CommandSellPlot.
public class CommandBuyPlot extends ForgeEssentialsCommandBase{

    @Override
    public void processCommandPlayer(EntityPlayer buyer, String[] args)
    {
        if (args.length == 3)
        {
            Plot plot = PlotManager.plotList.get(args[0]);
            EntityPlayer seller = UserIdent.getPlayerByUuid(plot.getOwner());
            OutputHandler.chatNotification(seller, "Player " + buyer.getDisplayName() + " offered to purchase plot " + plot.getName() + " for " + args[1]
                    + ". Type /sellplot <plotName> yes to accept, /sellplot <plotName> no to deny. This offer will expire in " + PlotManager.timeout + " seconds.");
            PlotManager.pendingOffers.put(plot.getName(), new Offer(plot, buyer, seller, Integer.parseInt(args[1])));
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
