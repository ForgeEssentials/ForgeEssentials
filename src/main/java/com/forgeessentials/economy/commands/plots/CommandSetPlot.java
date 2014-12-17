package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.PlotManager;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.events.EventCancelledException;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;

public class CommandSetPlot extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        PlayerInfo info = PlayerInfo.getPlayerInfo(player);
        try
        {
            AreaZone zone = new AreaZone(APIRegistry.perms.getWorldZone(player.worldObj), PlotManager.PLOT_NAME_ID + args[0], info.getSelection());
            zone.setGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_PERM, true);
            zone.setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_OWNER, new UserIdent(player).getUuid().toString());
            zone.setHidden(true);

            if (args[1] != null)
            {
                int price = Integer.parseInt(args[1]); // checks if it's a valid number
                zone.setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_VALUE, Integer.toString(price));
            }
            else
            {
                int price = zone.getArea().getXLength() * zone.getArea().getZLength() * ModuleEconomy.psfPrice;
                zone.setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_VALUE, Integer.toString(price));
            }
        }
        catch (EventCancelledException e)
        {
            throw new CommandException("Something went wrong - couldn't create plot");
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
        return "fe.economy.plots.set";
    }

    @Override
    public PermissionsManager.RegisteredPermValue getDefaultPermission()
    {
        return PermissionsManager.RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "setplot";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/setplot <name> [value] Set the current selection as a tradeable plot";
    }
}
