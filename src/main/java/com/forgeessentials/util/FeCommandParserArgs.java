package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionContext;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.commons.CommandParserArgs;
import com.forgeessentials.commons.MessageConstants;

/**
 *
 */
public class FeCommandParserArgs extends CommandParserArgs
{

    public final PermissionContext permissionContext;

    public FeCommandParserArgs(ICommand command, String[] args, ICommandSender sender, boolean isTabCompletion)
    {
        super(command, args, sender, isTabCompletion);
        this.permissionContext = new PermissionContext(sender, command);
    }

    public FeCommandParserArgs(ICommand command, String[] args, ICommandSender sender)
    {
        this(command, args, sender, false);
    }

    @Override
    public List<String> completePlayer(String arg)
    {
        Set<String> result = new TreeSet<>();
        for (UserIdent knownPlayerIdent : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (CommandBase.doesStringStartWith(arg, knownPlayerIdent.getUsernameOrUuid()))
                result.add(knownPlayerIdent.getUsernameOrUuid());
        }
        for (EntityPlayerMP player : Utils.getPlayerList())
        {
            if (CommandBase.doesStringStartWith(arg, player.getCommandSenderName()))
                result.add(player.getCommandSenderName());
        }
        return new ArrayList<>(result);
    }

    @Override
    public String parsePermission()
    {
        if (isTabCompletion && size() == 1)
        {
            String permission = peek();
            Set<String> permissionSet = APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions();
            Set<String> result = new TreeSet<>();
            for (String perm : permissionSet)
            {
                int nodeIndex = perm.indexOf('.', permission.length());
                if (nodeIndex >= 0)
                    perm = perm.substring(0, nodeIndex);
                if (CommandBase.doesStringStartWith(permission, perm))
                    result.add(perm);
            }
            tabCompletion = new ArrayList<>(result);
            throw new CancelParsingException();
        }
        return remove();
    }

    @Override
    public boolean hasPermission(String perm)
    {
        return APIRegistry.perms.checkPermission(permissionContext, perm);
    }

    @Override
    public void requirePlayer()
    {
        if (senderPlayer == null)
            throw new TranslatedCommandException(MessageConstants.MSG_NO_CONSOLE_COMMAND);
    }

    public WorldZone getWorldZone()
    {
        if (senderPlayer == null)
            throw new TranslatedCommandException("Player needed");
        return APIRegistry.perms.getServerZone().getWorldZone(senderPlayer.dimension);
    }

}
