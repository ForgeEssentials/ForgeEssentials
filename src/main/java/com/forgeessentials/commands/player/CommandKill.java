package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandKill extends ForgeEssentialsCommandBuilder
{

    public CommandKill(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
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

    public String getUsage(CommandSource sender)
    {
        return "/kill <player> Commit suicide or kill other players (with special permission).";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".kill";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Use /kill on other players");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("victim", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "setPass")
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (EntityArgument.getPlayer(ctx, "victim")!=getServerPlayer(ctx.getSource())&&PermissionAPI.hasPermission(getServerPlayer(ctx.getSource()), getPermissionNode() + ".others"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "victim");
            if (!player.hasDisconnected())
            {
                player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
                ChatOutputHandler.chatError(player, Translator.translate("You were killed. You probably deserved it."));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName());
        }
        else
        {
            getServerPlayer(ctx.getSource()).hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            ChatOutputHandler.chatError(ctx.getSource(), Translator.translate("You were killed. You probably deserved it."));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "victim");
        if (!player.hasDisconnected())
        {
            player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            ChatOutputHandler.chatError(player, Translator.translate("You were killed. You probably deserved it."));
        }
        else
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName());
        return Command.SINGLE_SUCCESS;
    }
}
