package com.forgeessentials.chat.command;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandNickname extends ForgeEssentialsCommandBuilder
{

    public CommandNickname(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "nickname";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "nick" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleChat.PERM + ".nickname.others", DefaultPermissionLevel.OP, "Edit other players' nicknames");
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("clearSelf").executes(CommandContext -> execute(CommandContext, "delS")))
                .then(Commands.literal("clearPlayer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "delO"))))
                .then(Commands.literal("setSelf")
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "setS"))))
                .then(Commands.literal("setPlayer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "setO")))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("delS"))
        {
            ModuleChat.setPlayerNickname((Player) ctx.getSource().getEntity(), null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Nickname removed.");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("setS"))
        {
            String name = StringArgumentType.getString(ctx, "name");
            ModuleChat.setPlayerNickname((Player) ctx.getSource().getEntity(), name);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Nickname set to " + name);
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        if (params.equals("delO"))
        {
            ModuleChat.setPlayerNickname(player, null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed nickname of %s", player));
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("setO"))
        {
            String name = StringArgumentType.getString(ctx, "name");
            ModuleChat.setPlayerNickname(player, name);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Set nickname of %s to %s", player, name));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("delS") || params.equals("setS"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Console can only modify player nicknames!"));
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        if (params.equals("delO"))
        {
            ModuleChat.setPlayerNickname(player, null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed nickname of %s", player));
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("setO"))
        {
            String name = StringArgumentType.getString(ctx, "name");
            ModuleChat.setPlayerNickname(player, name);
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Set nickname of %s to %s", player, name));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
