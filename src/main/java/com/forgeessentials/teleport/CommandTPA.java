package com.forgeessentials.teleport;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

public class CommandTPA extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "tpa";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/tpa [player] <player|<x> <y> <z>|accept|decline> Request to teleport yourself or another player.";
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TPA;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/tpa <player>: Request being teleportet to another player");
            arguments.confirm("/tpa <player> <here|x y z>: Propose another player to be teleported");
            return;
        }

        final UserIdent player = arguments.parsePlayer(true);
        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            try
            {
                Questioner.add(player.getPlayer(), Translator.format("Allow teleporting %s to your location?", arguments.sender.getCommandSenderName()),
                        new QuestionerCallback() {
                            @Override
                            public void respond(Boolean response)
                            {
                                if (response == null)
                                    arguments.error("TPA request timed out");
                                else if (response == false)
                                    arguments.error("TPA declined");
                                else
                                    TeleportHelper.teleport(arguments.senderPlayer, new WarpPoint(player.getPlayer()));
                            }
                        }, 20);
            }
            catch (QuestionerStillActiveException e)
            {
                throw new QuestionerStillActiveException.CommandException();
            }
            return;
        }

        arguments.tabComplete("here");

        final WarpPoint point;
        final String locationName;
        if (arguments.peek().equalsIgnoreCase("here"))
        {
            point = new WarpPoint(arguments.senderPlayer);
            locationName = arguments.sender.getCommandSenderName();
            arguments.remove();
        }
        else
        {
            point = new WarpPoint(arguments.senderPlayer.worldObj, //
                    arguments.parseDouble(), arguments.parseDouble(), arguments.parseDouble(), //
                    player.getPlayer().rotationPitch, player.getPlayer().rotationYaw);
            locationName = point.toReadableString();
        }

        if (arguments.isTabCompletion)
            return;
        try
        {
            Questioner.add(player.getPlayer(), Translator.format("Do you want to be teleported to %s?", locationName), new QuestionerCallback() {
                @Override
                public void respond(Boolean response)
                {
                    if (response == null)
                        arguments.error("TPA request timed out");
                    else if (response == false)
                        arguments.error("TPA declined");
                    else
                        TeleportHelper.teleport(player.getPlayerMP(), point);
                }
            }, 20);
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

}
