package com.forgeessentials.economy.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

public class CommandSetWallet extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "setwallet";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length != 2)
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player> <amount>");
        
        EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().func_152612_a(args[0]);
        int amountToSet = Integer.parseInt(args[1]);

        if (player == null)
            throw new TranslatedCommandException("Player does not exist, or is not online.");
        
        APIRegistry.wallet.setWallet(amountToSet, player);
        OutputHandler.chatConfirmation(sender, Translator.format("Wallet set to %s", APIRegistry.wallet.getMoneyString(player.getPersistentID())));
        OutputHandler.chatNotification(player, Translator.format("Your wallet was set to %s", APIRegistry.wallet.getMoneyString(player.getPersistentID())));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
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

        return "/setwallet <player> <amount> Set a player's wallet to a certain amount.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }

}
