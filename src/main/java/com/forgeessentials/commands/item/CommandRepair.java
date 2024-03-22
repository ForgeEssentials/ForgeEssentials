package com.forgeessentials.commands.item;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandRepair extends ForgeEssentialsCommandBuilder
{

    public CommandRepair(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "repair";
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
        APIRegistry.perms.registerPermission(ModuleCommands.PERM + ".repair.others", DefaultPermissionLevel.OP,
                "Allows repairing items held by another player");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("self")
                        .then(Commands.literal("Custom")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "custom-self"))))
                        .then(Commands.literal("MaxValue")
                                .executes(CommandContext -> execute(CommandContext, "max-self"))))
                .then(Commands.literal("others")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("custom")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(CommandContext -> execute(CommandContext, "custom-others"))))

                                .then(Commands.literal("MaxValue")
                                        .executes(CommandContext -> execute(CommandContext, "max-others")))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        String[] args = params.split("-");
        if (args[1].equals("self"))
        {
            if (args[0].equals("max"))
            {
                ItemStack item = getServerPlayer(ctx.getSource()).getMainHandItem();
                if (item == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a reparable item.");
                    return Command.SINGLE_SUCCESS;
                }
                item.setDamageValue(0);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Repared item to max.");

            }
            else if (args[0].equals("custom"))
            {
                ItemStack item = getServerPlayer(ctx.getSource()).getMainHandItem();
                if (item == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a reparable item.");
                    return Command.SINGLE_SUCCESS;
                }
                item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Repared item to the selected amount.");
            }
        }
        else if (args[1].equals("others"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (args[0].equals("max"))
            {
                ItemStack item = player.getMainHandItem();
                if (item == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "They are not holding a reparable item.");
                    return Command.SINGLE_SUCCESS;
                }
                item.setDamageValue(0);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Repared item to max.");
                
            }
            else if (args[0].equals("custom"))
            {
                ItemStack item = player.getMainHandItem();
                if (item == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "They are not holding a reparable item.");
                    return Command.SINGLE_SUCCESS;
                }
                item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Repared item to the selected amount.");
            }
        }
        return Command.SINGLE_SUCCESS;

    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        String[] args = params.split("-");
        if (args[1].equals("others"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (args[0].equals("max"))
            {
                ItemStack item = player.getMainHandItem();
                if (item == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "They are not holding a reparable item.");
                    return Command.SINGLE_SUCCESS;
                }
                item.setDamageValue(0);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Repared item to max.");
            }
            else if (args[0].equals("custom"))
            {
                ItemStack item = player.getMainHandItem();
                if (item == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "They are not holding a reparable item.");
                    return Command.SINGLE_SUCCESS;
                }
                item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Repared item to the selected amount.");
            }
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You must select a player!");

        }
        return Command.SINGLE_SUCCESS;
    }
}
