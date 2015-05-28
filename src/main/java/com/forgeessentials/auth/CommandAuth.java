package com.forgeessentials.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAuthLoginEvent;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandAuth extends ForgeEssentialsCommandBase
{
    private static String[] playerCommands = new String[] { "help", "login", "register", "changepass", "kick", "setpass", "unregister" };
    private static String[] serverCommands = new String[] { "help", "kick", "setpass", "unregister" };

    @Override
    public String getCommandName()
    {
        return "auth";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "AUTH" };
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            throw new TranslatedCommandException("command.auth.usage");
        }

        boolean hasAdmin = PermissionsManager.checkPermission(sender, getPermissionNode() + ".admin");

        // one arg? must be help.
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatConfirmation(sender, " - /auth register <password>");
                OutputHandler.chatConfirmation(sender, " - /auth login <password>");
                OutputHandler.chatConfirmation(sender, " - /auth changepass <oldpass> <newpass>  - changes your password");

                if (!hasAdmin)
                {
                    return;
                }

                OutputHandler.chatConfirmation(sender, " - /auth kick <player>  - forces the player to login again");
                OutputHandler.chatConfirmation(sender, " - /auth setpass <player> <password>  - sets the players password");
                OutputHandler.chatConfirmation(sender, " - /auth unregister <player>  - forces the player to register again");
                return;
            }
            else
            {
                throw new TranslatedCommandException("/auth help");
            }
        }

        // 2 args? seconds needs to be the player.
        if (args.length == 2)
        {
            // parse login
            if (args[0].equalsIgnoreCase("login"))
            {
                if (!PlayerPassData.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", sender.getPersistentID());

                if (PlayerPassData.checkPassword(sender.getPersistentID(), args[1]))
                {
                    // login worked
                    ModuleAuth.hasSession.add(sender.getPersistentID());
                    OutputHandler.chatConfirmation(sender, "Login successful.");
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent(sender));
                }
                else
                {
                    throw new TranslatedCommandException("Login failed.");
                }

                return;

            }
            // parse register
            else if (args[0].equalsIgnoreCase("register"))
            {
                if (PlayerPassData.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is already registered!", sender.getPersistentID());

                if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineReg)
                    throw new TranslatedCommandException("Registrations have been disabled.");

                PlayerPassData.setPassword(sender.getPersistentID(), args[1]);
                OutputHandler.chatConfirmation(sender, "Registration successful.");
                return;
            }

            // stop if unlogged.
            if (!ModuleAuth.hasSession.contains(sender.getPersistentID()))
                throw new TranslatedCommandException("Login required. Try /auth help.");

            // check for players.. all the rest of these should be greated than 1.
            UUID userID = UserIdent.getUuidByUsername(args[1]);
            boolean isLogged = true;

            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                OutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
                isLogged = false;
            }

            // parse ./auth kick
            if (args[0].equalsIgnoreCase("kick"))
            {
                if (!hasAdmin)
                {
                    throw new PermissionDeniedException();
                }
                else if (!isLogged)
                {
                    throw new PlayerNotFoundException();
                }
                else
                {
                    ModuleAuth.hasSession.remove(userID);
                    OutputHandler.chatConfirmation(sender, Translator.format("Player %s was logged out from the authentication service.", userID));
                    OutputHandler.chatWarning(player, "You have been logged out from the authentication service. Please login again.");
                    return;
                }
            }
            // parse ./auth setpass
            else if (args[0].equalsIgnoreCase("setPass"))
            {
                if (!hasAdmin)
                {
                    throw new PermissionDeniedException();
                }

                throw new TranslatedCommandException("/auth setpass <player> <password>");
            }

            // parse ./auth unregister
            else if (args[0].equalsIgnoreCase("unregister"))
            {
                if (!hasAdmin)
                    throw new PermissionDeniedException();

                if (!PlayerPassData.isRegistered(userID))
                    throw new TranslatedCommandException("Player %s is not registered!", userID);

                PlayerPassData.setPassword(userID, null);
                OutputHandler.chatConfirmation(sender, Translator.format("Player %s has been removed from the authentication service.", userID));
                return;
            }

            // ERROR! :D
            else
            {
                throw new TranslatedCommandException("/auth help");
            }
        }
        // 3 args? must be a comtmand - player - pass
        else if (args.length == 3)
        {
            if (!ModuleAuth.hasSession.contains(sender.getPersistentID()))
                throw new TranslatedCommandException("Login required. Try /auth help.");

            // parse changePass
            if (args[0].equalsIgnoreCase("changepass"))
            {
                if (args[1].equals(args[2]))
                {
                    OutputHandler.chatConfirmation(sender, "You can't use this new password - it's the same as what was previously there.");
                    return;
                }

                if (!PlayerPassData.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", sender.getCommandSenderName());

                if (!PlayerPassData.checkPassword(sender.getPersistentID(), args[1]))
                {
                    OutputHandler.chatConfirmation(sender, "Could not change the password - your old password is wrong");
                    return;
                }

                PlayerPassData.setPassword(sender.getPersistentID(), args[2]);
                OutputHandler.chatConfirmation(sender, "Password change successful.");
                return;

            }

            // check for players.. all the rest of these should be greated than 1.
            UUID name = UserIdent.getUuidByUsername(args[1]);
            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                OutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
            }

            // pasre setPass
            if (args[0].equalsIgnoreCase("setPass"))
            {
                if (!hasAdmin)
                    throw new PermissionDeniedException();
                PlayerPassData.setPassword(name, args[2]);
                OutputHandler.chatConfirmation(sender, Translator.format("Password set for %s", name));
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            throw new TranslatedCommandException("/auth help");
        }

        // one arg? must be help.
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatNotification(sender, " - /auth kick <player>  - forces the player to login again");
                OutputHandler.chatNotification(sender, " - /auth setpass <player> <password>  - sets the players password to the specified");
                OutputHandler.chatNotification(sender, " - /auth unregister <player>  - forces the player to register again");
                return;
            }
            else
            {
                throw new TranslatedCommandException("/auth help");
            }
        }

        // check for players.. all the rest of these should be greated than 1.
        UUID userID = UserIdent.getUuidByUsername(args[1]);
        boolean isLogged = true;

        // check if the player is logged.
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
        if (player == null)
        {
            OutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
            isLogged = false;
        }

        // 2 args? seconds needs to be the player.
        if (args.length == 2)
        {
            // parse ./auth kick
            if (args[0].equalsIgnoreCase("kick"))
            {
                if (!isLogged)
                {
                    throw new TranslatedCommandException("/auth kick <player");
                }
                else
                {
                    ModuleAuth.hasSession.remove(userID);
                    OutputHandler.chatConfirmation(sender, Translator.format("Player %s was logged out from the authentication service.", userID));
                    OutputHandler.chatWarning(player, "You have been logged out from the authentication service. Please login again.");
                    return;
                }
            }
            // parse ./auth setpass
            else if (args[0].equalsIgnoreCase("setPass"))
            {
                throw new TranslatedCommandException("/auth setpass <player> <password>");
            }
            else if (args[0].equalsIgnoreCase("unregister"))
            {
                if (!PlayerPassData.isRegistered(userID))
                    throw new TranslatedCommandException("message.auth.error.notregisterred", args[1]);
                PlayerPassData.setPassword(userID, null);
                return;
            }

            // ERROR! :D
            else
            {
                throw new TranslatedCommandException("command.auth.usage");
            }
        }
        // 3 args? must be a command - player - pass
        else if (args.length == 3)
        {
            // pasre setPass
            if (args[0].equalsIgnoreCase("setPass"))
            {
                PlayerPassData.setPassword(userID, args[2]);
                OutputHandler.chatConfirmation(sender, Translator.format("Password set for %s", userID));
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        ArrayList<String> list = new ArrayList<String>();
        switch (args.length)
        {
        case 1:
            if (sender instanceof EntityPlayer)
            {
                list.addAll(getListOfStringsMatchingLastWord(args, playerCommands));
            }
            else
            {
                list.addAll(getListOfStringsMatchingLastWord(args, serverCommands));
            }
            break;
        case 2:
            if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("setpass") || args[0].equalsIgnoreCase("unregister"))
            {
                list.addAll(getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames()));
            }
        }
        return list;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.auth";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String s = "/auth help";
        if (sender instanceof EntityPlayer)
        {
            s = s + " Manages your authentication profile.";
        }
        else
        {
            s = s + " Controls the authentication module.";
        }
        return s;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
