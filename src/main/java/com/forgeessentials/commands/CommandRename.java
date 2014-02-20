package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}