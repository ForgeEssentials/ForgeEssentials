package com.forgeessentials.commands.player;

import java.util.Collection;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.GameType;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandGameMode extends ForgeEssentialsCommandBuilder
{
    public CommandGameMode(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "gamemode";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "gm" };
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        for (GameType gametype : GameType.values())
        {
            if (gametype != GameType.NOT_SET)
            {
                baseBuilder
                        .then(Commands.literal(gametype.getName())
                                .executes(CommandContext -> execute(CommandContext, "single-" + gametype.getName()))
                                .then(Commands.argument("target", EntityArgument.players()).executes(
                                        CommandContext -> execute(CommandContext, "other-" + gametype.getName()))))
                        .executes(CommandContext -> execute(CommandContext, "blank"));
            }
        }
        return baseBuilder;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            setGameMode(ctx.getSource(), getServerPlayer(ctx.getSource()));
            return Command.SINGLE_SUCCESS;
        }
        String[] args = params.split("-");
        if (args[0].equals("single"))
        {
            setGameMode(ctx.getSource(), getServerPlayer(ctx.getSource()), GameType.byName(args[1]));
            return Command.SINGLE_SUCCESS;
        }
        Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(ctx, "target");
        setGameModes(ctx.getSource(), players, GameType.byName(args[1]), true);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Cant set gamemode of the console");
            return Command.SINGLE_SUCCESS;
        }
        String[] args = params.split("-");
        if (args[0].equals("single"))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Cant set gamemode of the console");
            return Command.SINGLE_SUCCESS;
        }
        Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(ctx, "target");
        setGameModes(ctx.getSource(), players, GameType.byName(args[1]), false);
        return Command.SINGLE_SUCCESS;
    }

    public void setGameMode(CommandSource sender, ServerPlayerEntity target)
    {

        setGameMode(sender, target, target.isCreative() ? GameType.SURVIVAL : GameType.CREATIVE);
    }

    public void setGameMode(CommandSource sender, ServerPlayerEntity target, GameType mode)
    {
        if (target.gameMode.getGameModeForPlayer() != mode)
        {
            target.setGameMode(mode);
            target.fallDistance = 0.0F;
            ChatOutputHandler.chatNotification(sender, Translator.format("%1$s's gamemode was changed to %2$s.",
                    target.getDisplayName().getString(), mode.getName()));
        }
    }

    public void setGameModes(CommandSource source, Collection<ServerPlayerEntity> players, GameType mode,
            boolean isPlayer)
    {
        for (ServerPlayerEntity serverplayerentity : players)
        {
            if (isPlayer)
            {
                if (serverplayerentity != getServerPlayer(source)
                        && !hasPermission(source, getPermissionNode() + ".others"))
                {
                    ChatOutputHandler.chatError(source, "You dont have permission to change others gamemodes.");
                    return;
                }
            }
            if (serverplayerentity.gameMode.getGameModeForPlayer() != mode)
            {
                serverplayerentity.setGameMode(mode);
                ChatOutputHandler.chatNotification(source, Translator.format("%1$s's gamemode was changed to %2$s.",
                        serverplayerentity.getDisplayName().getString(), mode.getName()));
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP,
                "Change others' game modes");
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getName();
    }

}
