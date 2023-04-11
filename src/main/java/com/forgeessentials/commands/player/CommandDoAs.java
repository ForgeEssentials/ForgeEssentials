package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandDoAs extends ForgeEssentialsCommandBuilder
{

    public CommandDoAs(boolean enabled)
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
        return baseBuilder
                .then(Commands.argument("player", StringArgumentType.word())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "blank"))
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        String playerS = StringArgumentType.getString(ctx, "player");
        String message = StringArgumentType.getString(ctx, "message");
        if (params.toString() == "blank")
        {
            ChatOutputHandler.chatError(ctx.getSource(), "/doas <player> <command> Run a command as another player.");
            return Command.SINGLE_SUCCESS;
        }
        if ((ctx.getSource().getEntity() instanceof ServerPlayerEntity) && playerS.equalsIgnoreCase("[CONSOLE]"))
        {
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
            if (!PermissionAPI.hasPermission(player, "fe.commands.doas.console")){
                ChatOutputHandler.chatWarning(player, FEPermissions.MSG_NO_COMMAND_PERM);
                return Command.SINGLE_SUCCESS;
            }
            
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player.createCommandSourceStack()).createCommandSourceStack(), message);
        }

        ServerPlayerEntity player = (ServerPlayerEntity) UserIdent.getPlayerByMatchOrUsername(null, playerS);
        if (player != null)
        {
            ChatOutputHandler.chatWarning(player, Translator.format("Player %s is attempting to issue a command as you.", ctx.getSource().getEntity().getDisplayName().getString()));
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(ctx.getSource(), message);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Successfully issued command as %s", playerS));
        }
        else{
            ChatOutputHandler.chatWarning(player, Translator.format("Player %s does not exist, or is not online.", playerS));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
