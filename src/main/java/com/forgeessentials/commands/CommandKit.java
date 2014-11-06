package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 *
 * @author Dries007
 */

public class CommandKit extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "kit";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        /*
         * Print kits
		 */
        if (args.length == 0)
        {
            OutputHandler.chatNotification(sender, "Available kits:");

            String msg = "";
            for (Kit kit : CommandDataManager.kits.values())
            {
                if (PermissionsManager.checkPermission(sender, getPermissionNode() + "." + kit.getName()))
                {
                    msg = kit.getName() + ", " + msg;
                }
            }
            OutputHandler.chatNotification(sender, msg);
            return;
        }
        /*
         * Give kit
		 */
        if (args.length == 1)
        {
            if (CommandDataManager.kits.containsKey(args[0].toLowerCase()))
            {
                if (PermissionsManager.checkPermission(sender, getPermissionNode() + "." + args[0].toLowerCase()))
                {
                    CommandDataManager.kits.get(args[0].toLowerCase()).giveKit(sender);
                }
                else
                {
                    OutputHandler.chatError(sender,
                            "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
                }
            }
            else
            {
                OutputHandler.chatError(sender, "Kit doesn't exist - either make it or try another kit?");
            }
            return;
        }
		/*
		 * Make kit
		 */
        if (args[1].equalsIgnoreCase("set") && PermissionsManager.checkPermission(sender, getPermissionNode() + ".admin"))
        {
            if (args.length == 3)
            {
                if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                {
                    int cooldown = parseIntWithMin(sender, args[2], 0);
                    new Kit(sender, args[0].toLowerCase(), cooldown);
                    OutputHandler.chatConfirmation(sender, "Kit created successfully. %c sec cooldown.".replaceAll("%c", "" + FunctionHelper.parseTime(cooldown)));
                }
                else
                {
                    OutputHandler.chatError(sender, "This kit already exists.");
                }
                return;
            }
        }

		/*
		 * Delete kit
		 */
        if (args[1].equalsIgnoreCase("del") && PermissionsManager.checkPermission(sender, getPermissionNode() + ".admin"))
        {
            if (args.length == 2)
            {
                if (CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                {
                    CommandDataManager.removeKit(CommandDataManager.kits.get(args[0].toLowerCase()));
                    OutputHandler.chatConfirmation(sender, "Kit removed.");
                }
                else
                {
                    OutputHandler.chatError(sender, "Kit doesn't exist - either make it or try another kit?");
                }
                return;
            }
        }

		/*
		 * You're doing it wrong!
		 */
        OutputHandler.chatError(sender, "Improper syntax. Please try this instead: [name] OR [name] [set|del] <cooldown>");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".admin", RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(CommandsEventHandler.BYPASS_KIT_COOLDOWN, RegisteredPermValue.OP);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            ArrayList<String> list = new ArrayList<String>();
            list.addAll(CommandDataManager.kits.keySet());
            list.add("set");
            list.add("del");

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

        return "/kit [name] OR [name] [set|del] <cooldown> Allows you to receive free kits which are pre-defined by the server owner.";
    }

}
