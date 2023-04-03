package com.forgeessentials.commands.item;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRepair extends ForgeEssentialsCommandBuilder
{

    public CommandRepair(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".repair";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Allows repairing items held by another player");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("self")
                        .then(Commands.literal("Custom")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "Custom","Self")
                                                )
                                        )
                                )
                        .then(Commands.literal("MaxValue")
                                .executes(CommandContext -> execute(CommandContext, "Max", "Self")
                                        )
                                )
                        )
                .then(Commands.literal("others")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("Custom")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(CommandContext -> execute(CommandContext, "Custom","Others")
                                                        )
                                                )
                                        )
                                
                                .then(Commands.literal("MaxValue")
                                        .executes(CommandContext -> execute(CommandContext, "Max", "Others")
                                                )
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params[1].toString() =="self")
        {
            if(params[0].toString() == "Max") 
            {
                ItemStack item = getServerPlayer(ctx.getSource()).getMainHandItem();
                if (item == null)
                    throw new TranslatedCommandException("You are not holding a reparable item.");
                item.setDamageValue(0);
            }
            else if (params[0].toString() == "Custom") 
            {
                ItemStack item = getServerPlayer(ctx.getSource()).getMainHandItem();
                if (item == null)
                    throw new TranslatedCommandException("You are not holding a reparable item.");
                item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
            } 
        }
        else if (params[1].toString() =="others" && PermissionAPI.hasPermission(getServerPlayer(ctx.getSource()), getPermissionNode() + ".others"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if(params[0].toString() == "Max") 
            {
                ItemStack item = player.getMainHandItem();
                if (item != null)
                    item.setDamageValue(0);
            }
            else if (params[0].toString() == "Custom") 
            {
                ItemStack item = player.getMainHandItem();
                if (item != null)
                item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
            } 
        }
        return Command.SINGLE_SUCCESS;

    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params[1].toString() =="others")
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if(params[0].toString() == "Max") 
            {
                ItemStack item = player.getMainHandItem();
                if (item != null)
                    item.setDamageValue(0);
            }
            else if (params[0].toString() == "Custom") 
            {
                ItemStack item = player.getMainHandItem();
                if (item != null)
                item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
            }
        }
        else //params[1].toString() =="self"
        {
            throw new TranslatedCommandException("You must select a player!");

        }
        return Command.SINGLE_SUCCESS;
    }
}
