package com.forgeessentials.economy.commands.plots;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.economy.ModuleEconomy.CantAffordException;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.economy.plots.TransactionHandler;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.questioner.QuestionData;
import com.forgeessentials.util.questioner.Questioner;

// This class only allows people to offer to buy plots. Actual transaction is done in CommandSellPlot.
public class CommandBuyPlot extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommandPlayer(EntityPlayerMP buyer, String[] args)
    {
        if (args.length < 1)
            throw new TranslatedCommandException("Incorrect syntax. Try this instead: <plotName> <amount>");

        long value;
        AreaZone plot = (AreaZone) APIRegistry.perms.getZoneById(PlotManager.PLOT_NAME_ID + args[0]);
        if (!plot.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERM))
        {
            throw new TranslatedCommandException("No such plot!");
        }
        EntityPlayerMP seller = UserIdent.getPlayerByUuid(UUID.fromString(plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER)));
        if (args[1] != null)
        {
            value = Long.parseLong(args[1]);
        }
        else
        {
            value = Long.parseLong(plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_VALUE));
            OutputHandler.chatNotification(buyer, "No value specified. Will use current valuation of plot, which is " + APIRegistry.economy.currency(value));
        }

        Wallet buyerWallet = APIRegistry.economy.getWallet(new UserIdent(buyer));
        if (!buyerWallet.withdraw(value))
            throw new CantAffordException();

        Offer<AreaZone> item = new Offer<AreaZone>(buyer, seller, plot, value);
        Questioner.addToQuestionQueue(new QuestionData(seller, "Player " + buyer.getDisplayName() + " offered to purchase plot " + plot.getName() + " for "
                + APIRegistry.economy.currency(value) + ". Type /yes to accept, /no to deny. This offer will expire in " + PlotManager.timeout + " seconds.",
                new TransactionHandler(item), PlotManager.timeout));
        PlotManager.pendingOffers.put(plot.getName(), item);
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
