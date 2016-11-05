package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionContext;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.commons.CommandParserArgs;
import com.forgeessentials.commons.MessageConstants;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

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

    public void confirm(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatConfirmation(sender, Translator.format(message, args));
    }

    public void notify(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatNotification(sender, Translator.format(message, args));
    }

    public void warn(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatWarning(sender, Translator.format(message, args));
    }

    public void error(String message, Object... args)
    {
        if (!isTabCompletion)
            ChatOutputHandler.chatError(sender, Translator.format(message, args));
    }

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

    public void checkPermission(String perm)
    {
        if (!isTabCompletion && sender != null && !hasPermission(perm))
            throw new TranslatedCommandException(MessageConstants.MSG_NO_COMMAND_PERM);
    }

    public boolean hasPermission(String perm)
    {
        return APIRegistry.perms.checkPermission(permissionContext, perm);
    }

    public WorldServer parseWorld()
    {
        if (isTabCompletion && size() == 1)
        {
            tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), APIRegistry.namedWorldHandler.getWorldNames());
            throw new CancelParsingException();
        }
        if (isEmpty())
        {
            if (senderPlayer != null)
                return (WorldServer) senderPlayer.worldObj;
            else
                throw new TranslatedCommandException(MessageConstants.MSG_NOT_ENOUGH_ARGUMENTS);
        }
        else
        {
            String name = remove();
            if (name.equalsIgnoreCase("here"))
            {
                if (senderPlayer == null)
                    throw new TranslatedCommandException("\"here\" cannot be used in console.");
                return (WorldServer) senderPlayer.worldObj;
            }
            else
            {
                return APIRegistry.namedWorldHandler.getWorld(name);
            }
        }
    }

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
