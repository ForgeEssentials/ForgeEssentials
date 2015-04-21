package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows you to modify a bunch of interesting stuff...
 *
 * @author Dries007
 */

public class CommandCapabilities extends FEcmdModuleCommands {
    public static ArrayList<String> names;

    static
    {
        names = new ArrayList<String>();
        names.add("disabledamage");
        names.add("isflying");
        names.add("allowflying");
        names.add("iscreativemode");
        names.add("allowedit");
    }

    @Override
    public String getCommandName()
    {
        return "capabilities";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 3)
        {
        	throw new TranslatedCommandException(getCommandUsage(sender));
        }

        if (args.length == 0)
        {
            OutputHandler.chatNotification(sender, "Possible capabilities:");
            OutputHandler.chatNotification(sender, StringUtils.join(names.toArray(), ", "));
        }
        else if (args.length == 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                OutputHandler.chatNotification(sender, Translator.format("Capabilities for %s:", player.getCommandSenderName()));
                OutputHandler.chatNotification(sender, names.get(0) + " = " + player.capabilities.disableDamage);
                OutputHandler.chatNotification(sender, names.get(1) + " = " + player.capabilities.isFlying);
                OutputHandler.chatNotification(sender, names.get(2) + " = " + player.capabilities.allowFlying);
                OutputHandler.chatNotification(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
                OutputHandler.chatNotification(sender, names.get(4) + " = " + player.capabilities.allowEdit);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else if (args.length == 2)
        {
            if (sender instanceof EntityPlayer)
            {
                if (!PermissionsManager.checkPermission((EntityPlayer) sender, getPermissionNode() + ".others"))
                {
                    OutputHandler.chatError(sender, "You don't have permissions for that.");
                    return;
                }
            }
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                if (args[1].equalsIgnoreCase(names.get(0)))
                {
                    OutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(0) + " = " + player.capabilities.disableDamage);
                }
                else if (args[1].equalsIgnoreCase(names.get(1)))
                {
                    OutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(1) + " = " + player.capabilities.isFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(2)))
                {
                    OutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(2) + " = " + player.capabilities.allowFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(3)))
                {
                    OutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(3) + " = " + player.capabilities.isCreativeMode);
                }
                else if (args[1].equalsIgnoreCase(names.get(4)))
                {
                    OutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(4) + " = " + player.capabilities.allowEdit);
                }
                else
                    throw new CommandException("Capability '%s' unknown.", args[1]);
            }
        }
        else if (args.length == 3)
        {
            if (sender instanceof EntityPlayer)
            {
                if (!PermissionsManager.checkPermission((EntityPlayer) sender, getPermissionNode() + ".others"))
                {
                    OutputHandler.chatError(sender, "You don't have permissions for that.");
                    return;
                }
            }
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                if (args[1].equalsIgnoreCase(names.get(0)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.disableDamage = bln;
                    OutputHandler.chatNotification(sender, names.get(0) + " = " + player.capabilities.disableDamage);
                }
                else if (args[1].equalsIgnoreCase(names.get(1)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.isFlying = bln;
                    OutputHandler.chatNotification(sender, names.get(1) + " = " + player.capabilities.isFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(2)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.allowFlying = bln;
                    OutputHandler.chatNotification(sender, names.get(2) + " = " + player.capabilities.allowFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(3)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.isCreativeMode = bln;
                    OutputHandler.chatNotification(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
                }
                else if (args[1].equalsIgnoreCase(names.get(4)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.allowEdit = bln;
                    OutputHandler.chatNotification(sender, names.get(4) + " = " + player.capabilities.allowEdit);
                }
                else
                    throw new CommandException("command.capabilities.capabilityUnknown", args[1]);
                player.sendPlayerAbilities();
            }
        }
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, names);
        }
        else if (args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/capabilities [player] [capability] [value|default] Allows you to modify player capabilities.";
    }

}
