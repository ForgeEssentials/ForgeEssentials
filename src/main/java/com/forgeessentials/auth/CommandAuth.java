package com.forgeessentials.auth;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandAuth extends ForgeEssentialsCommandBuilder
{
    public CommandAuth(boolean enabled)
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
        return baseBuilder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("login")
                        .then(Commands.argument("password", StringArgumentType.word())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("register")
                        .then(Commands.argument("password", StringArgumentType.word())
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
                                .then(Commands.argument("passwordOld", StringArgumentType.word())
                                        .then(Commands.argument("passwordNew", StringArgumentType.word())
                                                .executes(CommandContext -> execute(CommandContext, "changepass")
                                                        )
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("setPass")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("passwordNew", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "setPass")
                                                )
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank"));
	}

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (!ModuleAuth.isEnabled())
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "The authentication service has been disabled by your server admin.");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("blank"))
        {
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
            return Command.SINGLE_SUCCESS;
        }

        boolean hasAdmin = PermissionAPI.hasPermission((PlayerEntity) ctx.getSource().getEntity(), getPermissionNode() + ".admin");

        // help.
        if (params.equals("help"))
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
        if (params.equals("login"))
        {
            if (!ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID())) {
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format("Player %s is not registered.", ctx.getSource().getEntity().getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }

            if (PasswordManager.checkPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), StringArgumentType.getString(ctx, "password")))
            {
                // login worked
                ModuleAuth.authenticate(((PlayerEntity) ctx.getSource().getEntity()).getUUID());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Login successful.");
                APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success((PlayerEntity) ctx.getSource().getEntity(), Source.COMMAND));
            }
            else
            {
                APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Failure((PlayerEntity) ctx.getSource().getEntity()));
                ChatOutputHandler.chatWarning(ctx.getSource(), "Login failed.");
            }

            return Command.SINGLE_SUCCESS;

        }

        // parse register
        if (params.equals("register"))
        {
            if (ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID())) {
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format("Player %s is already registered!", ((PlayerEntity) ctx.getSource().getEntity()).getUUID().toString()));
                return Command.SINGLE_SUCCESS;

            }

            if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineRegistration) {
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.translate("Registrations have been disabled."));
                return Command.SINGLE_SUCCESS;
            }

            PasswordManager.setPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), StringArgumentType.getString(ctx, "password"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Registration successful.");
            return Command.SINGLE_SUCCESS;
        }

        // stop if unlogged.
        if (!ModuleAuth.isAuthenticated(((PlayerEntity) ctx.getSource().getEntity()).getUUID())) {
            ChatOutputHandler.chatWarning(ctx.getSource(), "Login required. Try /auth help.");
            return Command.SINGLE_SUCCESS;
        }

        boolean isLogged = true;

        // check if the player is logged.
        ServerPlayerEntity player = (ServerPlayerEntity) UserIdent.getPlayerByMatchOrUsername(ctx.getSource(), EntityArgument.getPlayer(ctx, "player").getDisplayName().getString());
        if (player == null)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "A player of that name is not on the server. Doing the action anyways.");
            isLogged = false;
        }

        // parse ./auth kick
        if (params.equals("kick"))
        {
            if (!hasAdmin)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.translate("You don't have permissions for that."));
                return Command.SINGLE_SUCCESS;
            }
            else if (!isLogged)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Player %s is currently online", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
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
        if (params.equals("unregister"))
        {
            if (!hasAdmin) {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.translate("You don't have permissions for that."));
                return Command.SINGLE_SUCCESS;
            }

            if (!ModuleAuth.isRegistered(player.getUUID())) {
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format("Player %s is not registered.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
            PasswordManager.setPassword(player.getUUID(), null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Player %s has been removed from the authentication service.", player.getName()));
            return Command.SINGLE_SUCCESS;
        }

        if (!ModuleAuth.isAuthenticated(((PlayerEntity) ctx.getSource().getEntity()).getUUID())) {
            ChatOutputHandler.chatWarning(ctx.getSource(), "Login required. Try /auth help.");
            return Command.SINGLE_SUCCESS;
        }

        // parse changePass
        if (params.equals("changepass"))
        {
            
            if (StringArgumentType.getString(ctx, "passwordOld").equals(StringArgumentType.getString(ctx, "passwordNew")))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "You can't use this new password - it's the same as what was previously there.");
                return Command.SINGLE_SUCCESS;
            }

            if (!ModuleAuth.isRegistered(((PlayerEntity) ctx.getSource().getEntity()).getUUID())) {
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format("Player %s is not registered.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
            if (!PasswordManager.checkPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), StringArgumentType.getString(ctx, "passwordOld")))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Could not change the password - your old password is wrong");
                return Command.SINGLE_SUCCESS;
            }

            PasswordManager.setPassword(((PlayerEntity) ctx.getSource().getEntity()).getUUID(), StringArgumentType.getString(ctx, "passwordNew"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Password change successful.");
            return Command.SINGLE_SUCCESS;

        }

        // parse setPass
        if (params.equals("setPass"))
        {
            if (!hasAdmin) {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.translate("You don't have permissions for that."));
                return Command.SINGLE_SUCCESS;
            }
            PasswordManager.setPassword(player.getUUID(), StringArgumentType.getString(ctx, "passwordNew"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Password set for %s", player.getName()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (!ModuleAuth.isEnabled())
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "The authentication service has been disabled on this server.");
            return Command.SINGLE_SUCCESS;
        }
        // help.
        if (params.equals("help") || params.equals("blank") || params.equals("login") || params.equals("register") || params.equals("changepass"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /auth kick <player>  - forces the player to login again");
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /auth setpass <player> <password>  - sets the players password to the specified");
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /auth unregister <player>  - forces the player to register again");
            return Command.SINGLE_SUCCESS;
        }

        boolean isLogged = true;

        // check if the player is logged.
        PlayerEntity player = UserIdent.get(EntityArgument.getPlayer(ctx, "player")).getPlayer();
        if (player == null)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "A player of that name is not on the server. Doing the action anyways.");
            isLogged = false;
        }

        // parse ./auth kick
        if (params.equals("kick"))
        {
            if (!isLogged)
            {
                ChatOutputHandler.chatError(player.createCommandSourceStack(), Translator.format("Player %s is not logged in", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
            else
            {
                ModuleAuth.deauthenticate(player.getUUID());
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Player %s was logged out from the authentication service.", player.getDisplayName().getString()));
                ChatOutputHandler.chatWarning(player.createCommandSourceStack(),
                        "You have been logged out from the authentication service. Please login again.");
                return Command.SINGLE_SUCCESS;
            }
        }

        if (params.equals("unregister"))
        {
            if (!ModuleAuth.isRegistered(player.getUUID())) {
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format("Player %s is not registered.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
            PasswordManager.setPassword(player.getUUID(), null);
            return Command.SINGLE_SUCCESS;
        }

        // pasre setPass
        if (params.equals("setPass"))
        {
            PasswordManager.setPassword(player.getUUID(), StringArgumentType.getString(ctx, "passwordNew"));
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
