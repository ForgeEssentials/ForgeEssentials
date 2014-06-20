package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.commands.util.TickHandlerCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

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
            ChatUtils.sendMessage(sender, "Available kits:");

            String msg = "";
            for (Kit kit : CommandDataManager.kits.values())
            {
                if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + kit.getName())))
                {
                    msg = kit.getName() + ", " + msg;
                }
            }
            ChatUtils.sendMessage(sender, msg);
            return;
        }
        /*
		 * Give kit
		 */
        if (args.length == 1)
        {
            if (CommandDataManager.kits.containsKey(args[0].toLowerCase()))
            {
                if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
                {
                    CommandDataManager.kits.get(args[0].toLowerCase()).giveKit(sender);
                }
                else
                {
                    OutputHandler.chatError(sender,
                            "You have insufficient permission to do that. If you believe you received this message in error, please talk to a server admin.");
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
        if (args[1].equalsIgnoreCase("set") && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
        {
            if (args.length == 3)
            {
                if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                {
                    int cooldown = parseIntWithMin(sender, args[2], 0);
                    new Kit(sender, args[0].toLowerCase(), cooldown);
                    ChatUtils.sendMessage(sender, "Kit created successfully. %c sec cooldown.".replaceAll("%c", "" + FunctionHelper.parseTime(cooldown)));
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
        if (args[1].equalsIgnoreCase("del") && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
        {
            if (args.length == 2)
            {
                if (CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                {
                    CommandDataManager.removeKit(CommandDataManager.kits.get(args[0].toLowerCase()));
                    ChatUtils.sendMessage(sender, "Kit removed.");
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
    public void registerExtraPermissions(IPermRegisterEvent event)
    {
        event.registerPermissionLevel(getCommandPerm() + ".admin", RegGroup.OWNERS);
        event.registerPermissionLevel(TickHandlerCommands.BYPASS_KIT_COOLDOWN, RegGroup.OWNERS);
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
    public RegGroup getReggroup()
    {
        return RegGroup.MEMBERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/kit [name] OR [name] [set|del] <cooldown> Allows you to receive free kits which are pre-defined by the server owner.";
    }

}
