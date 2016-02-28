package com.forgeessentials.auth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;

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
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new TranslatedCommandException("command.auth.usage");
        }

        boolean hasAdmin = PermissionManager.checkPermission(sender, getPermissionNode() + ".admin");

        // one arg? must be help.
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                ChatOutputHandler.chatConfirmation(sender, " - /auth register <password>");
                ChatOutputHandler.chatConfirmation(sender, " - /auth login <password>");
                ChatOutputHandler.chatConfirmation(sender, " - /auth changepass <oldpass> <newpass>  - changes your password");

                if (!hasAdmin)
                {
                    return;
                }

                ChatOutputHandler.chatConfirmation(sender, " - /auth kick <player>  - forces the player to login again");
                ChatOutputHandler.chatConfirmation(sender, " - /auth setpass <player> <password>  - sets the players password");
                ChatOutputHandler.chatConfirmation(sender, " - /auth unregister <player>  - forces the player to register again");
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
                    ChatOutputHandler.chatConfirmation(sender, "Login successful.");
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success(sender, Source.COMMAND));
                }
                else
                {
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Failure(sender));
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
                ChatOutputHandler.chatConfirmation(sender, "Registration successful.");
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
                ChatOutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
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
                    ChatOutputHandler.chatConfirmation(sender,
                            Translator.format("Player %s was logged out from the authentication service.", player.getName()));
                    ChatOutputHandler.chatWarning(player, "You have been logged out from the authentication service. Please login again.");
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

                if (!ModuleAuth.isRegistered(player.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", player.getName());

                PasswordManager.setPassword(player.getPersistentID(), null);
                ChatOutputHandler.chatConfirmation(sender,
                        Translator.format("Player %s has been removed from the authentication service.", player.getName()));
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
                    ChatOutputHandler.chatConfirmation(sender, "You can't use this new password - it's the same as what was previously there.");
                    return;
                }

                if (!ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Player %s is not registered!", sender.getName());

                if (!PasswordManager.checkPassword(sender.getPersistentID(), args[1]))
                {
                    ChatOutputHandler.chatConfirmation(sender, "Could not change the password - your old password is wrong");
                    return;
                }

                PasswordManager.setPassword(sender.getPersistentID(), args[2]);
                ChatOutputHandler.chatConfirmation(sender, "Password change successful.");
                return;

            }

            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                ChatOutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
            }

            // pasre setPass
            if (args[0].equalsIgnoreCase("setPass"))
            {
                if (!hasAdmin)
                    throw new PermissionDeniedException();
                PasswordManager.setPassword(player.getPersistentID(), args[2]);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Password set for %s", player.getName()));
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
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
                ChatOutputHandler.chatNotification(sender, " - /auth kick <player>  - forces the player to login again");
                ChatOutputHandler.chatNotification(sender, " - /auth setpass <player> <password>  - sets the players password to the specified");
                ChatOutputHandler.chatNotification(sender, " - /auth unregister <player>  - forces the player to register again");
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
            ChatOutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
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
                    ChatOutputHandler.chatConfirmation(sender,
                            Translator.format("Player %s was logged out from the authentication service.", player.getName()));
                    ChatOutputHandler.chatWarning(player, "You have been logged out from the authentication service. Please login again.");
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
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Password set for %s", player.getName()));
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
