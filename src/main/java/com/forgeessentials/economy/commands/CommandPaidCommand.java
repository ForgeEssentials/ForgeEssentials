package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class CommandPaidCommand extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "paidcommand";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("pc", "pcmd");
    }

    /*
     * Expected structure: "/paidcommand <player> <amount> <command [args]>"
     */
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        System.out.print(sender);
        if (args.length >= 3)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                int amount = parseIntWithMin(sender, args[1], 0);
                if (APIRegistry.wallet.getWallet(player.getPersistentID()) >= amount)
                {
                    APIRegistry.wallet.removeFromWallet(amount, player.getPersistentID());
                    // Do command in name of player

                    StringBuilder cmd = new StringBuilder(args.toString().length());
                    for (int i = 2; i < args.length; i++)
                    {
                        cmd.append(args[i]);
                        cmd.append(" ");
                    }

                    MinecraftServer.getServer().getCommandManager().executeCommand(player, cmd.toString());
                    OutputHandler.chatConfirmation(player, "That cost you " + amount + " " + APIRegistry.wallet.currency(amount));
                }
                else
                {
                    OutputHandler.chatError(player, "You can't afford that!!");
                }
            }
            else
            {
                //this should be removed
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            //this should be removed
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player> <amount> <command [args]>");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return null;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/paidcommand <player> <amount> <command [args]>";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return null;
    }

}
