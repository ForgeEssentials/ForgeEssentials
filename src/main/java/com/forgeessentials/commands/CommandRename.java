package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CommandRename extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "rename";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <name>");
        }
        else
        {
            ItemStack is = sender.inventory.getCurrentItem();
            if (is == null)
            {
                OutputHandler.chatError(sender, "You are not holding a valid item.");
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                for (String arg : args)
                {
                    sb.append(arg + " ");
                }
                is.setItemName(sb.toString().trim());
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/rename <new name> Renames the item you are currently holding.";
    }
}