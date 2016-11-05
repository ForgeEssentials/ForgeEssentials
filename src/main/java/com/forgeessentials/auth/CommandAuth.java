package com.forgeessentials.auth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.FEApi;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.ForgeEssentialsCommandBase;
import com.forgeessentials.util.TranslatedCommandException;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.Utils;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;

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
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            if (!ModuleAuth.isEnabled())
            {
                ChatUtil.chatWarning(sender, "The authentication service has been disabled by your server admin.");
                return;
            }

            if (ModuleAuth.isRegistered(sender.getPersistentID()))
            {
                if (ModuleAuth.isAuthenticated(sender))
                {
                    ChatUtil.chatNotification(sender, "You are logged in to the auth service.");
                }
                else
                {
                    ChatUtil.chatNotification(sender, "You are registered with the auth service, but you are not logged in.");
                }
            }
            else
            {
                ChatUtil.chatWarning(sender, "You are not registered with the auth service.");
            }

            throw new TranslatedCommandException("command.auth.usage");
        }

        boolean hasAdmin = PermissionManager.checkPermission(sender, getPermissionNode() + ".admin");

        // one arg? must be help.
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                ChatUtil.chatConfirmation(sender, " - /auth register <password>");
                ChatUtil.chatConfirmation(sender, " - /auth login <password>");
                ChatUtil.chatConfirmation(sender, " - /auth changepass <oldpass> <newpass>  - changes your password");

                if (!hasAdmin)
                {
                    return;
                }

                ChatUtil.chatConfirmation(sender, " - /auth kick <player>  - forces the player to login again");
                ChatUtil.chatConfirmation(sender, " - /auth setpass <player> <password>  - sets the players password");
                ChatUtil.chatConfirmation(sender, " - /auth unregister <player>  - forces the player to register again");
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
                if (!ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", sender.getPersistentID());

                if (PasswordManager.checkPassword(sender.getPersistentID(), args[1]))
                {
                    // login worked
                    ModuleAuth.authenticate(sender.getPersistentID());
                    ChatUtil.chatConfirmation(sender, "Login successful.");
                    FEApi.getFEEventBus().post(new PlayerAuthLoginEvent.Success(sender, Source.COMMAND));
                }
                else
                {
                    FEApi.getFEEventBus().post(new PlayerAuthLoginEvent.Failure(sender));
                    throw new TranslatedCommandException("Login failed.");
                }

                return;

            }
            // parse register
            else if (args[0].equalsIgnoreCase("register"))
            {
                if (ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is already registered!", sender.getPersistentID());

                if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineRegistration)
                    throw new TranslatedCommandException("Registrations have been disabled.");

                PasswordManager.setPassword(sender.getPersistentID(), args[1]);
                ChatUtil.chatConfirmation(sender, "Registration successful.");
                return;
            }

            // stop if unlogged.
            if (!ModuleAuth.isAuthenticated(sender))
                throw new TranslatedCommandException("Login required. Try /auth help.");

            boolean isLogged = true;

            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                ChatUtil.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
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
                    ModuleAuth.deauthenticate(player.getPersistentID());
                    ChatUtil.chatConfirmation(sender,
                            Translator.format("Player %s was logged out from the authentication service.", player.getCommandSenderName()));
                    ChatUtil.chatWarning(player, "You have been logged out from the authentication service. Please login again.");
                    return;
                }
            }
            // parse ./auth setpass
            else if (args[0].equalsIgnoreCase("setpass"))
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

                if (!ModuleAuth.isRegistered(player.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", player.getCommandSenderName());

                PasswordManager.setPassword(player.getPersistentID(), null);
                ChatUtil.chatConfirmation(sender,
                        Translator.format("Player %s has been removed from the authentication service.", player.getCommandSenderName()));
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
            if (!ModuleAuth.isAuthenticated(sender))
                throw new TranslatedCommandException("Login required. Try /auth help.");

            // parse changePass
            if (args[0].equalsIgnoreCase("changepass"))
            {
                if (args[1].equals(args[2]))
                {
                    ChatUtil.chatConfirmation(sender, "You can't use this new password - it's the same as what was previously there.");
                    return;
                }

                if (!ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", sender.getCommandSenderName());

                if (!PasswordManager.checkPassword(sender.getPersistentID(), args[1]))
                {
                    ChatUtil.chatConfirmation(sender, "Could not change the password - your old password is wrong");
                    return;
                }

                PasswordManager.setPassword(sender.getPersistentID(), args[2]);
                ChatUtil.chatConfirmation(sender, "Password change successful.");
                return;

            }

            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                ChatUtil.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
            }

            // pasre setPass
            if (args[0].equalsIgnoreCase("setPass"))
            {
                if (!hasAdmin)
                    throw new PermissionDeniedException();
                PasswordManager.setPassword(player.getPersistentID(), args[2]);
                ChatUtil.chatConfirmation(sender, Translator.format("Password set for %s", player.getCommandSenderName()));
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
                ChatUtil.chatNotification(sender, " - /auth kick <player>  - forces the player to login again");
                ChatUtil.chatNotification(sender, " - /auth setpass <player> <password>  - sets the players password to the specified");
                ChatUtil.chatNotification(sender, " - /auth unregister <player>  - forces the player to register again");
                return;
            }
            else
            {
                throw new TranslatedCommandException("/auth help");
            }
        }

        boolean isLogged = true;

        // check if the player is logged.
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
        if (player == null)
        {
            ChatUtil.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
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
                    ModuleAuth.deauthenticate(player.getPersistentID());
                    ChatUtil.chatConfirmation(sender,
                            Translator.format("Player %s was logged out from the authentication service.", player.getCommandSenderName()));
                    ChatUtil.chatWarning(player, "You have been logged out from the authentication service. Please login again.");
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
                if (!ModuleAuth.isRegistered(player.getPersistentID()))
                    throw new TranslatedCommandException("message.auth.error.notregisterred", args[1]);
                PasswordManager.setPassword(player.getPersistentID(), null);
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
                PasswordManager.setPassword(player.getPersistentID(), args[2]);
                ChatUtil.chatConfirmation(sender, Translator.format("Password set for %s", player.getCommandSenderName()));
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
                list.addAll(Utils.getListOfStringsMatchingLastWord(args, playerCommands));
            }
            else
            {
                list.addAll(Utils.getListOfStringsMatchingLastWord(args, serverCommands));
            }
            break;
        case 2:
            if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("setpass") || args[0].equalsIgnoreCase("unregister"))
            {
                list.addAll(Utils.getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames()));
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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
