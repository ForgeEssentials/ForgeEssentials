package com.forgeessentials.commands.player;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandDoAs extends ForgeEssentialsCommandBuilder
{

    public CommandDoAs(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".doas";
    }

    @Override
    public void registerExtraPermissions()
    {
        PermissionAPI.registerNode("fe.commands.doas.console", DefaultPermissionLevel.OP, "Use /doas as the console");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", MessageArgument.message())
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext))
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        String playerS = MessageArgument.getMessage(ctx, "player").getString();
        String message = MessageArgument.getMessage(ctx, "messag").getString();
        if (params.toString() == "blank")
        {
            ChatOutputHandler.chatError(ctx.getSource(), "/doas <player> <command> Run a command as another player.");
            return Command.SINGLE_SUCCESS;
        }
        if ((ctx.getSource().getEntity() instanceof ServerPlayerEntity) && playerS.equalsIgnoreCase("[CONSOLE]"))
        {
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
            if (!PermissionAPI.hasPermission(player, "fe.commands.doas.console"))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
            
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player), message);
        }

        ServerPlayerEntity player = (ServerPlayerEntity) UserIdent.getPlayerByMatchOrUsername(null, playerS);
        if (player != null)
        {
            ChatOutputHandler.chatWarning(player, Translator.format("Player %s is attempting to issue a command as you.", ctx.getSource().getEntity().getName().getString()));
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(ctx.getSource(), message);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Successfully issued command as %s", playerS));
        }
        else
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", playerS);
    }
}
