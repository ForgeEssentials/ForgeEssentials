package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

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
        return "feback";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "back" };
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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_BACK;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        PlayerInfo pi = PlayerInfo.get(sender.getPersistentID());
        WarpPoint point = null;
        if (PermissionManager.checkPermission(sender, TeleportModule.PERM_BACK_ONDEATH))
            point = pi.getLastDeathLocation();
        if (point == null)
            point = pi.getLastTeleportOrigin();
        if (point == null)
            throw new TranslatedCommandException("You have nowhere to get back to");

        TeleportHelper.teleport(sender, point);
    }

}
