package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

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
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: [player] [capability] [value|default]");
            return;
        }

        if (args.length == 0)
        {
            OutputHandler.chatConfirmation(sender, "Possible capabilities:");
            OutputHandler.chatConfirmation(sender, StringUtils.join(names.toArray(), ", "));
        }
        else if (args.length == 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatch(sender, args[0]);
            if (player != null)
            {
                OutputHandler.chatConfirmation(sender, String.format("Capabilities for %s:", player.getCommandSenderName()));
                ChatUtils.sendMessage(sender, names.get(0) + " = " + player.capabilities.disableDamage);
                ChatUtils.sendMessage(sender, names.get(1) + " = " + player.capabilities.isFlying);
                ChatUtils.sendMessage(sender, names.get(2) + " = " + player.capabilities.allowFlying);
                ChatUtils.sendMessage(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
                ChatUtils.sendMessage(sender, names.get(4) + " = " + player.capabilities.allowEdit);
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
            EntityPlayerMP player = UserIdent.getPlayerByMatch(sender, args[0]);
            if (player != null)
            {
                if (args[1].equalsIgnoreCase(names.get(0)))
                {
                    ChatUtils.sendMessage(sender, player.getCommandSenderName() + " => " + names.get(0) + " = " + player.capabilities.disableDamage);
                }
                else if (args[1].equalsIgnoreCase(names.get(1)))
                {
                    ChatUtils.sendMessage(sender, player.getCommandSenderName() + " => " + names.get(1) + " = " + player.capabilities.isFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(2)))
                {
                    ChatUtils.sendMessage(sender, player.getCommandSenderName() + " => " + names.get(2) + " = " + player.capabilities.allowFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(3)))
                {
                    ChatUtils.sendMessage(sender, player.getCommandSenderName() + " => " + names.get(3) + " = " + player.capabilities.isCreativeMode);
                }
                else if (args[1].equalsIgnoreCase(names.get(4)))
                {
                    ChatUtils.sendMessage(sender, player.getCommandSenderName() + " => " + names.get(4) + " = " + player.capabilities.allowEdit);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Capability '%s' unknown.", args[1]));
                    return;
                }
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
            EntityPlayerMP player = UserIdent.getPlayerByMatch(sender, args[0]);
            if (player != null)
            {
                if (args[1].equalsIgnoreCase(names.get(0)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.disableDamage = bln;
                    ChatUtils.sendMessage(sender, names.get(0) + " = " + player.capabilities.disableDamage);
                }
                else if (args[1].equalsIgnoreCase(names.get(1)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.isFlying = bln;
                    ChatUtils.sendMessage(sender, names.get(1) + " = " + player.capabilities.isFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(2)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.allowFlying = bln;
                    ChatUtils.sendMessage(sender, names.get(2) + " = " + player.capabilities.allowFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(3)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.isCreativeMode = bln;
                    ChatUtils.sendMessage(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
                }
                else if (args[1].equalsIgnoreCase(names.get(4)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.allowEdit = bln;
                    ChatUtils.sendMessage(sender, names.get(4) + " = " + player.capabilities.allowEdit);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("command.capabilities.capabilityUnknown", args[1]));
                    return;
                }
                player.sendPlayerAbilities();
            }
        }
    }

    @Override
    public void registerExtraPermissions()
    {
        PermissionsManager.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
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
            return getListOfStringsFromIterableMatchingLastWord(args, names);
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
