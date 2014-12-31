package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.PlotEvent;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.UUID;

public class CommandRepossess extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        AreaZone plot = (AreaZone) APIRegistry.perms.getZoneById(PlotManager.PLOT_NAME_ID + args[0]);
        if (!plot.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERM))
        {
            throw new CommandException("No such plot!");
        }
        EntityPlayer newowner = UserIdent.getPlayerByUsername(args[1]);
        EntityPlayer oldowner = UserIdent.getPlayerByUuid(UUID.fromString(plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER)));
        APIRegistry.getFEEventBus().post(new PlotEvent.OwnerUnset(plot, oldowner));
        APIRegistry.getFEEventBus().post(new PlotEvent.OwnerSet(plot, newowner));
        plot.setGroupPermissionProperty(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER, new UserIdent(newowner).getUuid().toString());
        OutputHandler.chatNotification(oldowner, "Plot " + plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_NAME_PERM) + " has been repossessed by the server admin.");
        OutputHandler.chatConfirmation(sender, "Reposession complete.");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return null;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return null;
    }

    @Override
    public String getCommandName()
    {
        return "repossess";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/repossess <plotname> <player> Forcibly reassign ownership of a plot.";
    }
}
