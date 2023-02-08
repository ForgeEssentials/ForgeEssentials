package com.forgeessentials.auth;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.TranslatedCommandException.PlayerNotFoundException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandAuth extends ForgeEssentialsCommandBuilder
{
    public CommandAuth(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "auth";
    }

    public LiteralArgumentBuilder<CommandSource> setExecution()
	{
        return builder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("login")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("register")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "register")
                                        )
                                )
                        )
                .then(Commands.literal("kick")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "kick")
                                        )
                                )
                        )
                .then(Commands.literal("unregister")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "unregister")
                                        )
                                )
                        )
                .then(Commands.literal("changepass")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("passwordOld", MessageArgument.message())
                                        .then(Commands.argument("passwordNew", MessageArgument.message())
                                                .executes(CommandContext -> execute(CommandContext, "changepass")
                                                        )
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("setPass")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("passwordNew", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext, "setPass")
                                                )
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank"));
	}

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank")
        {
            if (!ModuleAuth.isEnabled())
            {
                ChatOutputHandler.chatWarning(ctx.getSource(), "The authentication service has been disabled by your server admin.");
                return Command.SINGLE_SUCCESS;
            }

            if (ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
            {
                if (ModuleAuth.isAuthenticated(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
                {
                    ChatOutputHandler.chatNotification(ctx.getSource(), "You are logged in to the auth service.");
                }
                else
                {
                    ChatOutputHandler.chatNotification(ctx.getSource(), "You are registered with the auth service, but you are not logged in.");
                }
            }
            else
            {
                ChatOutputHandler.chatWarning(ctx.getSource(), "You are not registered with the auth service.");
            }

            throw new TranslatedCommandException("command.auth.usage");
        }

        boolean hasAdmin = PermissionAPI.hasPermission((PlayerEntity) ctx.getSource().getEntity(), getPermissionNode() + ".admin");

        // help.
        if (params.toString() == "help")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /auth register <password>");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /auth login <password>");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /auth changepass <oldpass> <newpass>  - changes your password");

            if (!hasAdmin)
            {
                return Command.SINGLE_SUCCESS;
            }

            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /auth kick <player>  - forces the player to login again");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /auth setpass <player> <password>  - sets the players password");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /auth unregister <player>  - forces the player to register again");
            return Command.SINGLE_SUCCESS;
        }

        // parse login
        if (params.toString() == "login")
        {
            if (!ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
                throw new TranslatedCommandException("Player %s is not registered!", ((PlayerEntity) ctx.getSource().getEntity()).getUUID());

            if (PasswordManager.checkPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), MessageArgument.getMessage(ctx, "login").getString()))
            {
                // login worked
                ModuleAuth.authenticate(((PlayerEntity) ctx.getSource().getEntity()).getUUID());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Login successful.");
                APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success((PlayerEntity) ctx.getSource().getEntity(), Source.COMMAND));
            }
            else
            {
                APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Failure((PlayerEntity) ctx.getSource().getEntity()));
                throw new TranslatedCommandException("Login failed.");
            }

            return Command.SINGLE_SUCCESS;

        }

        // parse register
        if (params.toString() == "register")
        {
            if (ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
                throw new TranslatedCommandException("Player %s is already registered!", ((PlayerEntity) ctx.getSource().getEntity()).getUUID());

            if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineRegistration)
                throw new TranslatedCommandException("Registrations have been disabled.");

            PasswordManager.setPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), MessageArgument.getMessage(ctx, "login").getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Registration successful.");
            return Command.SINGLE_SUCCESS;
        }

        // stop if unlogged.
        if (!ModuleAuth.isAuthenticated(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
            throw new TranslatedCommandException("Login required. Try /auth help.");

        boolean isLogged = true;

        // check if the player is logged.
        ServerPlayerEntity player = (ServerPlayerEntity) UserIdent.getPlayerByMatchOrUsername(ctx.getSource(), EntityArgument.getPlayer(ctx, "player").getName().getString());
        if (player == null)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "A player of that name is not on the server. Doing the action anyways.");
            isLogged = false;
        }

        // parse ./auth kick
        if (params.toString() == "kick")
        {
            if (!hasAdmin)
            {
                throw new PermissionDeniedException();
            }
            else if (!isLogged)
            {
                throw new PlayerNotFoundException("commands.generic.player.notFound");
            }
            else
            {
                ModuleAuth.deauthenticate(player.getUUID());
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Player %s was logged out from the authentication service.", player.getName()));
                ChatOutputHandler.chatWarning(player.createCommandSourceStack(),
                        "You have been logged out from the authentication service. Please login again.");
                return Command.SINGLE_SUCCESS;
            }
        }

        // parse ./auth unregister
        if (params.toString() == "unregister")
        {
            if (!hasAdmin)
                throw new PermissionDeniedException();

            if (!ModuleAuth.isRegistered(player.getUUID()))
                throw new TranslatedCommandException("Player %s is not registered!", player.getName());

            PasswordManager.setPassword(player.getUUID(), null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Player %s has been removed from the authentication service.", player.getName()));
            return Command.SINGLE_SUCCESS;
        }

        if (!ModuleAuth.isAuthenticated(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
            throw new TranslatedCommandException("Login required. Try /auth help.");

        // parse changePass
        if (params.toString() == "changepass")
        {
            
            if (MessageArgument.getMessage(ctx, "passwordOld").getString().equals(MessageArgument.getMessage(ctx, "passwordNew").getString()))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "You can't use this new password - it's the same as what was previously there.");
                return Command.SINGLE_SUCCESS;
            }

            if (!ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID()))
                throw new TranslatedCommandException("Player %s is not registered!", ((PlayerEntity) ctx.getSource().getEntity()).getName().getString());

            if (!PasswordManager.checkPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), MessageArgument.getMessage(ctx, "passwordOld").getString()))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Could not change the password - your old password is wrong");
                return Command.SINGLE_SUCCESS;
            }

            PasswordManager.setPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), MessageArgument.getMessage(ctx, "passwordNew").getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Password change successful.");
            return Command.SINGLE_SUCCESS;

        }

        // parse setPass
        if (params.toString() == "setPass")
        {
            if (!hasAdmin)
                throw new PermissionDeniedException();
            PasswordManager.setPassword(player.getUUID(), MessageArgument.getMessage(ctx, "passwordNew").getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Password set for %s", player.getName()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank" || params.toString() == "login" || params.toString() == "register" || params.toString() == "changepass")
        {
            throw new TranslatedCommandException("/auth help");
        }

        // help.
        if (params.toString() == "help")
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /auth kick <player>  - forces the player to login again");
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /auth setpass <player> <password>  - sets the players password to the specified");
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /auth unregister <player>  - forces the player to register again");
            return Command.SINGLE_SUCCESS;
        }

        boolean isLogged = true;

        // check if the player is logged.
        PlayerEntity player = UserIdent.getPlayerByMatchOrUsername(ctx.getSource(), EntityArgument.getPlayer(ctx, "player").getName().getString());
        if (player == null)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "A player of that name is not on the server. Doing the action anyways.");
            isLogged = false;
        }

        // parse ./auth kick
        if (params.toString() == "kick")
        {
            if (!isLogged)
            {
                throw new TranslatedCommandException("/auth kick <player");
            }
            else
            {
                ModuleAuth.deauthenticate(player.getUUID());
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Player %s was logged out from the authentication service.", player.getName()));
                ChatOutputHandler.chatWarning(player.createCommandSourceStack(),
                        "You have been logged out from the authentication service. Please login again.");
                return Command.SINGLE_SUCCESS;
            }
        }

        if (params.toString() == "unregister")
        {
            if (!ModuleAuth.isRegistered(player.getUUID()))
                throw new TranslatedCommandException("message.auth.error.notregisterred", player.getName());
            PasswordManager.setPassword(player.getUUID(), null);
            return Command.SINGLE_SUCCESS;
        }

        // pasre setPass
        if (params.toString() == "setPass")
        {
            PasswordManager.setPassword(player.getUUID(), MessageArgument.getMessage(ctx, "passwordNew").getString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Password set for %s", player.getName()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.auth";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

}
