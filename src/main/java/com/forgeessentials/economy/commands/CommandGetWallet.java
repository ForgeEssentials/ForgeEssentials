package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class CommandGetWallet extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "getwallet";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().func_152612_a(args[0]);

            if (player == null)
            {
                ChatUtils.sendMessage(sender, "The specified player does not exist, or is not online.");
            }
            else
            {
                ChatUtils.sendMessage(sender, player.getCommandSenderName() + "'s wallet contains:" + APIRegistry.wallet.getMoneyString(player.getPersistentID()));
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: [<player>]");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.economy." + getCommandName();
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
    public String getCommandUsage(ICommandSender sender)
    {

        return "/getwallet Get the wallet amount of a player.";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }
}
