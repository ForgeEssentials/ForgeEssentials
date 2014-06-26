package com.forgeessentials.teleport;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.TeleportCenter;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

public class CommandBack extends ForgeEssentialsCommandBase {
    public static List justDied = new ArrayList<String>();

    @Override
    public String getCommandName()
    {
        return "back";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (justDied.contains(sender.username))
        {
            if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, "fe.teleport.back.ondeath")))
            {
                PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
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
                justDied.remove(sender.username);
                return;
            }
            else
            {
                OutputHandler.chatError(sender, "You have nowhere to get back to");
            }
        }
        else if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, "fe.teleport.back.ontp")))
        {
            PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
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
    public String getCommandPerm()
    {
        return "fe.teleport.back";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.MEMBERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/back Teleport you to your last death or teleport location.";
    }

}
