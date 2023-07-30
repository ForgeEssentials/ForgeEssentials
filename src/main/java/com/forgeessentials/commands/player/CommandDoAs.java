package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDoAs extends ForgeEssentialsCommandBuilder
{

    public CommandDoAs(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "doas";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission("fe.commands.doas.console", DefaultPermissionLevel.OP,
                "Use /doas as the console");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", StringArgumentType.word())
                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "commandto"))))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        String playerS = StringArgumentType.getString(ctx, "player");
        String message = StringArgumentType.getString(ctx, "command");
        if (params.toString().equals("blank"))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "/doas <player> <command> Run a command as another player.");
            return Command.SINGLE_SUCCESS;
        }
        if ((ctx.getSource().getEntity() instanceof ServerPlayerEntity) && playerS.equalsIgnoreCase("_CONSOLE_"))
        {
            ServerPlayerEntity player = getServerPlayer(ctx.getSource());
            if (!hasPermission(player.createCommandSourceStack(), "fe.commands.doas.console"))
            {
                ChatOutputHandler.chatWarning(player, FEPermissions.MSG_NO_COMMAND_PERM);
                return Command.SINGLE_SUCCESS;
            }
            ServerLifecycleHooks.getCurrentServer().getCommands()
                    .performCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player.createCommandSourceStack())
                            .createCommandSourceStack(), message);
            return Command.SINGLE_SUCCESS;
        }

        PlayerEntity player = UserIdent.getPlayerByUsername(playerS);
        if (player != null)
        {
            ChatOutputHandler.chatWarning(player,
                    Translator.format("Player %s is attempting to issue a command as you.",
                            ctx.getSource().getDisplayName().getString()));
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(player.createCommandSourceStack(),
                    message);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Successfully issued command as %s", playerS));
        }
        else
        {
            ChatOutputHandler.chatWarning(ctx.getSource(),
                    Translator.format("Player %s does not exist, or is not online.", playerS));
        }
        return Command.SINGLE_SUCCESS;
    }
}
