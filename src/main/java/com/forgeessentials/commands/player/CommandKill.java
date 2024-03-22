package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandKill extends ForgeEssentialsCommandBuilder
{

    public CommandKill(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "kill";
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

    public String getUsage(CommandSourceStack sender)
    {
        return "/kill <player> Commit suicide or kill other players (with special permission).";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleCommands.PERM + ".kill.others", DefaultPermissionLevel.OP,
                "Use /kill on other players");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("victim", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (EntityArgument.getPlayer(ctx, "victim") != getServerPlayer(ctx.getSource())
                && hasPermission(getServerPlayer(ctx.getSource()).createCommandSourceStack(), ModuleCommands.PERM + ".kill.others"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "victim");
            if (!player.hasDisconnected())
            {
                player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
                ChatOutputHandler.chatError(player, Translator.translate("You were killed. You probably deserved it."));
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Player %s does not exist, or is not online.",
                        player.getDisplayName().getString());
            }
            return Command.SINGLE_SUCCESS;
        }
        else
        {
            getServerPlayer(ctx.getSource()).hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            ChatOutputHandler.chatError(ctx.getSource(),
                    Translator.translate("You were killed. You probably deserved it."));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "victim");
        if (!player.hasDisconnected())
        {
            player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            ChatOutputHandler.chatError(player, Translator.translate("You were killed. You probably deserved it."));
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Player %s does not exist, or is not online.",
                    player.getDisplayName().getString());
        }
        return Command.SINGLE_SUCCESS;
    }
}
