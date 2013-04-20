package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandRename extends FEcmdModuleCommands
{
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
            OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
        }
        else
        {
            ItemStack is = sender.inventory.getCurrentItem();
            if (is == null)
            {
                OutputHandler.chatError(sender, Localization.get("message.error.noItemPlayer"));
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                for (String arg : args)
                    sb.append(arg + " ");
                is.setItemName(sb.toString().trim());
            }
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
    public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public String getCommandPerm()
    {
        return "ForgeEssentials.BasicCommands." + getCommandName();
    }
}