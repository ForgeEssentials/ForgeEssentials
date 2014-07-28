package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CommandRepair extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "repair";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            ItemStack item = sender.getHeldItem();

            if (item == null)
            {
                OutputHandler.chatError(sender, "You are not holding a valid item.");
            }

            item.setItemDamage(0);

        }
        else if (args.length == 1 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {

                ItemStack item = player.getHeldItem();

                if (item != null)
                {
                    item.setItemDamage(0);
                }

            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: ");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            // PlayerSelector.matchPlayers(sender, args[0])
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {

                ItemStack item = player.getHeldItem();

                if (item != null)
                {
                    item.setItemDamage(0);
                }

            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: ");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.permReg.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/repair [player] Repair the item you or another player is holding.";
    }

}
