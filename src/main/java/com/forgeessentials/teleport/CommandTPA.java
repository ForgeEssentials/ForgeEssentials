package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;

public class CommandTPA extends ParserCommandBase
{

    public static final String PERM_HERE = TeleportModule.PERM_TPA + ".here";
    public static final String PERM_LOCATION = TeleportModule.PERM_TPA + ".loc";

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
        return TeleportModule.PERM_TPA;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_HERE, PermissionLevel.TRUE, "Allow teleporting other players to your own location (inversed TPA)");
        APIRegistry.perms.registerPermission(PERM_LOCATION, PermissionLevel.OP, "Allow teleporting other players to any location");
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/tpa <player>: Request being teleported to another player");
            arguments.confirm("/tpa <player> <here|x y z>: Propose another player to be teleported");
            return;
        }

        final UserIdent player = arguments.parsePlayer(true, true);
        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            arguments.confirm("Waiting for response by %s", player.getUsernameOrUuid());
            QuestionerCallback callback = new QuestionerCallback() {
                @Override
                public void respond(Boolean response)
                {
                    if (response == null)
                        arguments.error("TPA request timed out");
                    else if (response == false)
                        arguments.error("TPA declined");
                    else
                    	try
                    	{
                    		TeleportHelper.teleport(arguments.senderPlayer, new WarpPoint(player.getPlayer()));
                    	}
                    	catch (CommandException e)
                    	{
                    		arguments.error(e.getMessage());
                    	}
                }
            };
            Questioner.addChecked(player.getPlayer(), Translator.format("Allow teleporting %s to your location?", arguments.sender.getName()),
                    callback, 20);
            return;
        }

        arguments.tabComplete("here");

        final WarpPoint point;
        final String locationName;
        if (arguments.peek().equalsIgnoreCase("here"))
        {
            arguments.checkPermission(PERM_HERE);
            point = new WarpPoint(arguments.senderPlayer);
            locationName = arguments.sender.getName();
            arguments.remove();
        }
        else
        {
            arguments.checkPermission(PERM_LOCATION);
            point = new WarpPoint((WorldServer) arguments.senderPlayer.worldObj, //
                    arguments.parseDouble(), arguments.parseDouble(), arguments.parseDouble(), //
                    player.getPlayer().rotationPitch, player.getPlayer().rotationYaw);
            locationName = point.toReadableString();
        }

        if (arguments.isTabCompletion)
            return;
        Questioner.addChecked(player.getPlayer(), Translator.format("Do you want to be teleported to %s?", locationName), new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                    arguments.error("TPA request timed out");
                else if (response == false)
                    arguments.error("TPA declined");
                else
                	try
                	{
                		TeleportHelper.teleport(player.getPlayerMP(), point);
                	}
                	catch (CommandException e)
                	{
                		arguments.error(e.getMessage());
                	}
            }
        }, 20);
    }

}
