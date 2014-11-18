package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.teleport.util.TPAdata;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WarpPoint;

public class CommandTPAhere extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "tpahere";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: /tpahere [player] <player|<x> <y> <z>|accept|decline>");
            return;
        }

        if (args[0].equalsIgnoreCase("accept"))
        {
            for (TPAdata data : TeleportModule.tpaList)
            {
                if (data.tphere)
                {
                    if (data.receiver.getCommandSenderName().equalsIgnoreCase(sender.getCommandSenderName()))
                    {
                        OutputHandler.chatNotification(data.sender, "Teleport request accepted.");
                        OutputHandler.chatConfirmation(data.receiver, "Teleport request accepted by other party. Teleporting..");
                        TeleportModule.tpaListToRemove.add(data);
                        TeleportHelper.teleport(data.receiver, new WarpPoint(data.sender));
                        return;
                    }
                }
            }
            return;
        }

        if (args[0].equalsIgnoreCase("decline"))
        {
            for (TPAdata data : TeleportModule.tpaList)
            {
                if (data.tphere)
                {
                    if (data.receiver.getCommandSenderName().equalsIgnoreCase(sender.getCommandSenderName()))
                    {
                        OutputHandler.chatNotification(data.sender, "Teleport request declined.");
                        OutputHandler.chatError(data.receiver, "Teleport request declined by other party.");
                        TeleportModule.tpaListToRemove.add(data);
                        return;
                    }
                }
            }
            return;
        }

        if (!PermissionsManager.checkPermission(sender, TeleportModule.PERM_TPAHERE_SENDREQUEST))
        {
            OutputHandler.chatError(sender,
                    "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
            return;
        }

        EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (receiver == null)
        {
            OutputHandler.chatError(sender, args[0] + " not found.");
        }
        else
        {
            TeleportModule.tpaListToAdd.add(new TPAdata((EntityPlayerMP) sender, receiver, true));

            OutputHandler.chatNotification(sender, String.format("Teleport request sent to %s", receiver.getCommandSenderName()));
            OutputHandler.chatNotification(receiver,
                    String.format("Received teleport request from %s. Enter '/tpahere accept' to accept, '/tpahere decline' to decline.",
                            sender.getCommandSenderName()));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TPAHERE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        if (args.length == 1)
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add("accept");
            list.add("decline");
            list.addAll(Arrays.asList(MinecraftServer.getServer().getAllUsernames()));
            return getListOfStringsFromIterableMatchingLastWord(args, list);
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/tpahere [player] <player|<x> <y> <z>|accept|decline> Teleports you or a player to a player or x y z.";
    }
}
