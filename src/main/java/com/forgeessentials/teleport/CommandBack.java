package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.selections.WarpPoint;

public class CommandBack extends ForgeEssentialsCommandBase {
    public static List justDied = new ArrayList<UUID>();

    @Override
    public String getCommandName()
    {
        return "back";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (justDied.contains(sender.getPersistentID()))
        {
            if (PermissionsManager.checkPerm(sender, "fe.teleport.back.ondeath"))
            {
                PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
                if (info.back != null)
                {
                    WarpPoint death = info.back;
                    info.back = new WarpPoint(sender);
                    EntityPlayerMP player = (EntityPlayerMP) sender;
                    TeleportCenter.addToTpQue(death, player);
                }
                else
                {
                    OutputHandler.chatError(sender, "You have nowhere to get back to");
                }
                justDied.remove(sender.getPersistentID());
                return;
            }
            else
            {
                OutputHandler.chatError(sender, "You have nowhere to get back to");
            }
        }
        else if (PermissionsManager.checkPerm(sender, "fe.teleport.back.ontp"))
        {
            PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
            if (info.back != null)
            {
                WarpPoint back = info.back;
                info.back = new WarpPoint(sender);
                EntityPlayerMP player = (EntityPlayerMP) sender;
                TeleportCenter.addToTpQue(back, player);
            }
            else
            {
                OutputHandler.chatError(sender, "You have nowhere to get back to");
            }
            return;
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
        return "fe.teleport.back";
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
