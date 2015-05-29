package com.forgeessentials.teleport;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;

public class CommandBack extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "back";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/back: Teleport you to your last death or teleport location.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_BACK;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        PlayerInfo pi = PlayerInfo.get(sender.getPersistentID());
        WarpPoint point = null;
        if (PermissionsManager.checkPermission(sender, TeleportModule.PERM_BACK_ONDEATH))
            point = pi.getLastDeathLocation();
        if (point == null)
            point = pi.getLastTeleportOrigin();
        if (point == null)
            throw new TranslatedCommandException("You have nowhere to get back to");

        TeleportHelper.teleport(sender, point);
    }

}
