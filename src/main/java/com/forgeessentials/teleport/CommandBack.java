package com.forgeessentials.teleport;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.commons.selections.WarpPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandBack extends ForgeEssentialsCommandBase {
    
    public static List<UUID> justDied = new ArrayList<UUID>();

    @Override
    public String getCommandName()
    {
        return "back";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (justDied.contains(sender.getPersistentID()))
        {
            if (!PermissionsManager.checkPermission(sender, TeleportModule.PERM_BACK_ONDEATH))
                throw new TranslatedCommandException("You have nowhere to get back to");
            PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
            if (info.getLastTeleportOrigin() == null)
                throw new TranslatedCommandException("You have nowhere to get back to");
            WarpPoint death = info.getLastTeleportOrigin();
            info.setLastTeleportOrigin(new WarpPoint(sender));
            TeleportHelper.teleport(sender, death);
            justDied.remove(sender.getPersistentID());
        }
        else if (PermissionsManager.checkPermission(sender, TeleportModule.PERM_BACK_ONTP))
        {
            PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
            if (info.getLastTeleportOrigin() == null)
                throw new TranslatedCommandException("You have nowhere to get back to");
            WarpPoint back = info.getLastTeleportOrigin();
            info.setLastTeleportOrigin(new WarpPoint(sender));
            EntityPlayerMP player = sender;
            TeleportHelper.teleport(player, back);
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
        return TeleportModule.PERM_BACK;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/back Teleport you to your last death or teleport location.";
    }

}
