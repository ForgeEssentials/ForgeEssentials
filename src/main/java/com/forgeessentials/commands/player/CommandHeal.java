package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandHeal extends ForgeEssentialsCommandBuilder
{

    public CommandHeal(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "heal";
    }

    public String getUsage(ServerPlayerEntity sender)
    {
        if (sender instanceof PlayerEntity)
        {
            return "/heal <player> Heal yourself or other players (if you have permission).";
        }
        else
        {
            return "/heal <player> Heal a player.";
        }
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
        return ModuleCommands.PERM + ".heal";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Heal others");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("others")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "others")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank")
        {
            heal(ctx.getSource().getPlayerOrException());
        }
        else if (params.toString() == "others" && PermissionAPI.hasPermission(getServerPlayer(ctx.getSource()), getPermissionNode() + ".others"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (player != null)
            {
                heal(player);
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), String.format("Player %s does not exist, or is not online.", player.getDisplayName()));
            }
        }
        else
        {
            throw new TranslatedCommandException(getUsage(ctx.getSource().getPlayerOrException()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "others")
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (player != null)
            {
                heal(player);
            }
            else
            {
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", player.getDisplayName());
            }
        }
        else
        {
            throw new TranslatedCommandException(getUsage(ctx.getSource().getPlayerOrException()));
        }
        return Command.SINGLE_SUCCESS;
    }

    public void heal(PlayerEntity target)
    {
        float toHealBy = target.getMaxHealth() - target.getHealth();
        target.heal(toHealBy);
        target.clearFire();;
        target.getFoodData().eat(20, 1.0F);
        ChatOutputHandler.chatConfirmation(target, "You were healed.");
    }
}
