package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;

public class CommandRename extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "rename";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: /rename <name>");
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
                is.setStackDisplayName(sb.toString().trim());
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