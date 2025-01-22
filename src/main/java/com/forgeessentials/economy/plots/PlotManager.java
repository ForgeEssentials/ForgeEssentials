package com.forgeessentials.economy.plots;

import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.plots.command.CommandPlot;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlotManager extends ServerEventHandler
{

    public PlotManager()
    {
        FECommandManager.registerCommand(new CommandPlot());
    }

    public static void serverStarting()
    {
        Plot.registerPermissions();
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void permissionAfterLoadEvent(PermissionEvent.AfterLoad event)
    {
        Plot.loadPlots();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onZoneChange(PlayerChangedZone event)
    {
        Plot oldPlot = Plot.getPlot(event.beforePoint.toWorldPoint());
        Plot plot = Plot.getPlot(event.afterPoint.toWorldPoint());
        // TODO: This could fail, another (non-plot) zone starts on the same plane as the plot!!!
        // Plot plot = Plot.getPlot(event.afterZone.getId());
        if (oldPlot != plot && plot != null)
        {
            String message = Translator.format("You entered \"%s\"", plot.getNameNotNull());

            UserIdent ident = UserIdent.get(event.entityPlayer);
            Set<String> groups = plot.getZone().getStoredPlayerGroups(ident);
            if (groups.contains(Plot.GROUP_PLOT_OWNER))
            {
                message += " " + Translator.translate("as owner");
                ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
            }
            else if (groups.contains(Plot.GROUP_PLOT_USER))
            {
                message += " " + Translator.translate("with user access");
                ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
            }
            else if (!plot.hasOwner())
            {
                if (plot.isForSale())
                    message = Translator.translate("You have entered neutral plot which is open for sale");
                else
                    message = Translator.translate("You have entered a plot owned by the server");
                ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
            }
            else
            {
                message += " " + Translator.format("owned by %s", plot.getOwnerName());
                ChatOutputHandler.chatConfirmation(event.entityPlayer, message);
            }

            // TODO: fee check

            long price = plot.getPrice();
            if (price == 0)
                ChatOutputHandler.chatNotification(event.entityPlayer, Translator.translate("You can buy this plot for free"));
            else if (price > 0)
                ChatOutputHandler.chatNotification(event.entityPlayer, Translator.format("You can buy this plot for %s", APIRegistry.economy.toString(price)));
        }
    }
}
